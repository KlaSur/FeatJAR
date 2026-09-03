/*
 * Copyright (C) 2026 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula-analysis-ganak.
 *
 * formula-analysis-ganak is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula-analysis-ganak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula-analysis-ganak. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula-analysis-ganak> for further information.
 */
package de.featjar.analysis.ganak.cli;

import java.util.stream.Collectors;

import de.featjar.analysis.AAnalysisCommand;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.computation.Computations;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.conversion.ComputeBooleanClauseList;
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.computation.ComputeNNFFormula;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.IFormula;

/**
 * A command which computes an analysis result for a formula using Ganak.
 * 
 * @param <T>  the type of the analysis result
 */
public abstract class AGanakAnalysisCommand<T> extends AAnalysisCommand<T> {

    /**
     * Option for setting the seed for the pseudo random generator.
     */
    public static final Option<Long> RANDOM_SEED_OPTION = Option.newOption("seed", Option.LongParser) //
            .setDescription("Seed for the pseudo random generator") //
            .setDefaultValue(1L);

    /**
     * Option for setting the input format of the formula.
     */
    public static final Option<String> INPUT_FORMAT = Option.newStringEnumOption(
                    "input-format",
                    FormulaFormats.getInstance().getExtensions().stream()
                            .filter(IFormat::supportsParse)
                            .map(IFormat::getName)
                            .collect(Collectors.toList()))
            .setDescription("Format of the input. If not specified, tries to auto detect.");

    protected IFormula inputFormula;
    protected VariableMap variableMap;

    @Override
    protected IComputation<T> newComputation(OptionList optionParser) {
        Result<IFormat<IFormula>> userInputFormat =
                optionParser.getResult(INPUT_FORMAT).mapOptional(FormulaFormats.getInstance()::getFormatByName);
        Result<IFormula> parseResult = userInputFormat.isPresent()
                ? readFromInput(optionParser, userInputFormat.get())
                : readFromInput(optionParser, FormulaFormats.getInstance());
        IFormula userInput = parseResult.get();
        
        return newAnalysis(
                optionParser,
                Computations.of(userInput)
                        .map(ComputeNNFFormula::new)
                        .map(ComputeCNFFormula::new)
                        .map(ComputeBooleanClauseList::new)
                        .peekResult(
                                getClass(), "variableMap", clauseList -> variableMap = clauseList.getVariableMap()));
    }

    protected abstract IComputation<T> newAnalysis(
            OptionList optionParser, IComputation<BooleanAssignmentList> formula);
}

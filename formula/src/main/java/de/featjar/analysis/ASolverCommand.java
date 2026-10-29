/*
 * Copyright (C) 2026 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */

package de.featjar.analysis;

import de.featjar.base.cli.MultiOption;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionParser;
import de.featjar.base.cli.Options;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.IFormatSupplier;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.conversion.ComputeBooleanClauseList;
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.computation.ComputeNNFFormula;
import de.featjar.formula.io.BooleanAssignmentListFormats;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.io.dimacs.BooleanAssignmentListDimacsFormat;
import de.featjar.formula.structure.IFormula;

public abstract class ASolverCommand<T> extends AAnalysisCommand<T> {
     /**
     * Option for setting the seed for the pseudo random generator.
     */
    public static final Option<Long> RANDOM_SEED_OPTION = Options.newOption("seed", Options.LongParser) //
            .setDescription("Seed for the pseudo random generator") //
            .setDefaultArgument("1");
    
    /**
     * Option for setting the input format of the formula.
     */
    public static final Option<IFormatSupplier<IFormula>> INPUT_FORMAT =
            Options.newInputFormatOption(FormulaFormats.class);

    /**
     * Option for setting the output format of the formula.
     */
    public static final Option<IFormat<BooleanAssignmentList>> OUTPUT_FORMAT = 	
        Options.newOutputFormatOption( BooleanAssignmentListFormats.class, 
            new BooleanAssignmentListDimacsFormat().getName());

    /**
     * Option for specifying the analysis type.
     */
    public static final Option<String> ANALYSIS_TYPE = 	
        Options.newOption("analysis-type", Options.StringParser)
                .setDescription("Type of the analysis");

    /**
     * Option for providing arbitrary arguments to the solver.
     */
    public static final MultiOption<String> SOLVER_PARAMETERS = Options.newListOption(
        "solver-args", Options.StringParser);

    @Override
    protected IComputation<T> newComputation(OptionParser optionParser) {
        Result<IFormula> parseResult = readFromInput(optionParser, optionParser.get(INPUT_FORMAT));

        final IComputation<T> newAnalysis = newAnalysis(optionParser, parseResult.toComputation());
        return newAnalysis;
    }

    protected abstract IComputation<T> newAnalysis(
            OptionParser optionParser, IComputation<IFormula> formula);

}

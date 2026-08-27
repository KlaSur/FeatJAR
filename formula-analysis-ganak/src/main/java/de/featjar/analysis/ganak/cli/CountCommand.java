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

import de.featjar.analysis.ganak.computation.ComputeCountSolutionGanak;
import de.featjar.base.cli.ListOption;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.computation.IComputation;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.combination.VariableSetsCombinationSpecification;
import de.featjar.formula.io.csv.BooleanAssignmentGroupsCSVFormat;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class CountCommand extends AGanakAnalysisCommand<BigInteger> {
    /**
    * Literals to be kept
    */
    public static final ListOption<String> LITERALS_PROJECT_OPTION = (ListOption<String>)
            Option.newListOption("project", Option.StringParser)
                    .setDescription(
                            "Literals to be projected. If not set, all features will be projected.");

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Computes the number of satisfying assignments using ganak");
    }

    @Override
    public IComputation<BigInteger> newAnalysis(
            OptionList optionParser, IComputation<BooleanAssignmentList> formula) {
        List<String> projectLiterals = 
                optionParser.getResult(LITERALS_PROJECT_OPTION).orElse(List.of());
        VariableNamesList variablesToKeep = new VariableNamesList(projectLiterals);
           
        return formula.map(ComputeCountSolutionGanak::new)
        .set(ComputeCountSolutionGanak.VARIABLES_TO_KEEP, variablesToKeep);
    }

    @Override
    public Optional<String> getShortName() {
        return Optional.of("count-ganak");
    }
}

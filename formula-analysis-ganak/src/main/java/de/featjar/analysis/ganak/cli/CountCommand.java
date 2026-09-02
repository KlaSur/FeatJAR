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
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * A command which counts the number of satisfying assignments of a formula using Ganak, optionally with 
 * projected or sliced literals.
 */
public class CountCommand extends AGanakAnalysisCommand<BigInteger> {

    /**
     * Literals to be removed
     */
    public static final ListOption<String> LITERALS_SLICE_OPTION = (ListOption<String>)
            Option.newListOption("slice", Option.StringParser).setDescription("Literals to be removed.");

    /**
    * Literals to be kept
    */
    public static final ListOption<String> LITERALS_PROJECT_OPTION = (ListOption<String>)
            Option.newListOption("project", Option.StringParser)
                    .setDescription(
                            "Literals to be projected. If not set, all features will be projected. The slice option has a higher priority, i.e. if both the project and slice option contain the same literal, it will be removed.");

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Computes the number of satisfying assignments using ganak");
    }

    @Override
    public IComputation<BigInteger> newAnalysis(
            OptionList optionParser, IComputation<BooleanAssignmentList> formula) {
        List<String> sliceLiterals = 
                optionParser.getResult(LITERALS_SLICE_OPTION).orElse(List.of());
        List<String> projectLiterals = 
                optionParser.getResult(LITERALS_PROJECT_OPTION).orElse(List.of());

        // already compute result in order to obtain the VariableMap
        formula.computeResult();

        // retrieve variable indices using the VariableMap
        int[] sliceIndices = sliceLiterals.stream()
          .map(variableMap::get)
          .filter(Result::isPresent)
          .mapToInt(Result::get)
          .toArray();

        int[] projectIndices = projectLiterals.stream()
          .map(variableMap::get)
          .filter(Result::isPresent)
          .mapToInt(Result::get)
          .toArray();

        // set the variable indices as dependencies of ComputeCountSolutionGanak
        BooleanAssignment variablesToRemove = new BooleanAssignment(sliceIndices);
        BooleanAssignment variablesToKeep = new BooleanAssignment(projectIndices);

        return formula.map(ComputeCountSolutionGanak::new)
            .set(ComputeCountSolutionGanak.VARIABLES_TO_REMOVE, variablesToRemove)
            .set(ComputeCountSolutionGanak.VARIABLES_TO_KEEP, variablesToKeep);
    }

    @Override
    public Optional<String> getShortName() {
        return Optional.of("count-ganak");
    }
}

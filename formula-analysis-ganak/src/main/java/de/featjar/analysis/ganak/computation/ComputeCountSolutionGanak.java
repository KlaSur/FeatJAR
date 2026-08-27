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
package de.featjar.analysis.ganak.computation;

import de.featjar.base.computation.ComputeConstant;
import de.featjar.base.computation.Dependency;
import de.featjar.analysis.ganak.cli.VariableNamesList;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

import java.math.BigInteger;
import java.util.List;

public class ComputeCountSolutionGanak extends AGanakAnalysis<BigInteger> {
    public static final Dependency<VariableNamesList> VARIABLES_TO_KEEP =
            Dependency.newDependency(VariableNamesList.class);

    public static final Dependency<VariableNamesList> VARIABLES_TO_REMOVE =
            Dependency.newDependency(VariableNamesList.class);


    public ComputeCountSolutionGanak(IComputation<BooleanAssignmentList> cnfFormula) {
        super(cnfFormula, new ComputeConstant<>(new VariableNamesList()), 
            new ComputeConstant<>(new VariableNamesList()));
    }

    public ComputeCountSolutionGanak(ComputeCountSolutionGanak other) {
        super(other);
    }

    @Override
    public Result<BigInteger> compute(List<Object> dependencyList, Progress progress) {
        VariableNamesList inlcude = VARIABLES_TO_KEEP.get(dependencyList);
        VariableNamesList exclude = VARIABLES_TO_REMOVE.get(dependencyList);


        if (!inlcude.isEmpty() || !exclude.isEmpty()) {
           return initializeSolver(dependencyList).countSolution(inlcude, exclude);
        }

        return initializeSolver(dependencyList).countSolution();
    }
}

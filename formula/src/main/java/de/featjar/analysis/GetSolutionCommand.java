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

import java.util.Optional;

import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionParser;
import de.featjar.base.computation.IComputation;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.assignment.conversion.ComputeBooleanClauseList;
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.computation.ComputeNNFFormula;

public class GetSolutionCommand extends ASolverCommand<Assignment> {
    /**
     * Option for specifying the solver.
     */
    public static final Option<IAnalysis> SOLVER_TYPE = newSolverOption(
        GetSolution.class).setDescription("Type of the solver");

    public IComputation<Assignment> newAnalysis(
            OptionParser optionParser, IComputation<IFormula> formula) {
              
        IComputation<BooleanAssignmentList> assignmentList = formula.map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(ComputeBooleanClauseList::new)
                .peekResult(getClass(), "variableMap", clauseList -> variableMap = clauseList.getVariableMap());

        ICore solverType = (ICore) optionParser.get(SOLVER_TYPE);
        IComputation<BooleanAssignmentList> booleanSolution = solverType.core(assignmentList);

        IComputation<Assignment> assignment = variableMap.toAssignment(booleanSolution.get().get()).toComputation();

        return assignment;
    }

    @Override
    public Optional<String> getShortName() {
        return Optional.of("get-solution");
    }
}

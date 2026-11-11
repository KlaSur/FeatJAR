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
import java.util.stream.Collectors;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionParser;
import de.featjar.base.cli.Options;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.conversion.ComputeBooleanClauseList;
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.computation.ComputeNNFFormula;
import de.featjar.formula.structure.IFormula;

public class CoreCommand extends ASolverCommand<Assignment> {
    /**
     * Option for specifying the solver.
     */
    public static final Option<ICore> SOLVER_TYPE = Options.newChoiceOption(
            "solver-type", name -> Result.ofOptional(
            Core.getInstance().getSolverByName(name)),
            Core.getInstance().getExtensions().stream()
                                .map(IAnalysis::getName)
                               .collect(Collectors.toList()))
        .setDescription("Type of the solver");

    public IComputation<Assignment> newAnalysis(
            OptionParser optionParser, IComputation<IFormula> formula) {
        IComputation<BooleanAssignmentList> assignmentList = formula.map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(ComputeBooleanClauseList::new);
        return optionParser.get(SOLVER_TYPE).core(assignmentList);
    }


    @Override
    public Optional<String> getShortName() {
        return Optional.of("core");
    }
}

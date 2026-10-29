/*
 * Copyright (C) 2026 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula-analysis-cadical.
 *
 * formula-analysis-cadical is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula-analysis-cadical is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula-analysis-cadical. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula-analysis-cadical> for further information.
 */

package de.featjar.analysis.cadical.computation;

import de.featjar.analysis.IGetSolution;
import de.featjar.base.computation.IComputation;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanSolution;

public class GetSolutionCadical implements IGetSolution {
    public IComputation<BooleanSolution> getSolution(IComputation<BooleanAssignmentList> formula) {
        return formula.map(ComputeGetSolutionCadiCal::new);
    }

    public String getName() {
        return "Cadical";
    }
}

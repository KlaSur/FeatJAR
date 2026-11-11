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

import de.featjar.analysis.ICore;
import de.featjar.base.computation.IComputation;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

public class CoreCadical implements ICore {
    public IComputation<Assignment> core(IComputation<BooleanAssignmentList> formula) {
        return formula.map(ComputeCoreCadiCal::new)
            .map(CoreCadical.class, "assignment-conversion", list -> list.getVariableMap().toAssignment(list.getFirst()));
    }

    public String getName() {
        return "Cadical";
    }
}

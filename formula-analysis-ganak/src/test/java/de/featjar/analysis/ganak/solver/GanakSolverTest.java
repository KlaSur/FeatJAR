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
package de.featjar.analysis.ganak.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.featjar.Common;
import de.featjar.analysis.ganak.cli.VariableNamesList;
import de.featjar.analysis.ganak.computation.ComputeCountSolutionGanak;
import de.featjar.analysis.ganak.computation.ComputeSolutionGanak;
import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.assignment.conversion.ComputeBooleanClauseList;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.BiImplies;
import de.featjar.formula.structure.connective.Implies;
import de.featjar.formula.structure.connective.Or;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.predicate.Literal;
import net.bytebuddy.implementation.bind.annotation.Argument.BindingMechanic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GanakSolverTest extends Common {

    @BeforeAll
    public static void begin() {
        FeatJAR.testConfiguration().initialize();
    }

    @AfterAll
    public static void end() {
        FeatJAR.deinitialize();
    }

     @Test
    public void formulaIsSatisfiable() {
        final Literal a = Expressions.literal("a");
        final Literal b = Expressions.literal("b");
        final Literal c = Expressions.literal("c");

        final Or or = new Or(a, b, c);
        final Or notOr = new Or(new Not(a), new Not(b), new Not(c));
        final And formula = new And(or, notOr);

        IFormula cnf = formula.toCNF().orElseThrow();
        final Result<Boolean> result = Computations.of(cnf)
                .map(ComputeBooleanClauseList::new)
                .map(ComputeSolutionGanak::new)
                .computeResult();
        assertTrue(result.isPresent(), result::printProblems);
        assertEquals(true,result.get());
    }

    @Test
    public void formulaHas6Solutions() {
        final Literal a = Expressions.literal("a");
        final Literal b = Expressions.literal("b");
        final Literal c = Expressions.literal("c");

        final Or or = new Or(a, b, c);
        final Or notOr = new Or(new Not(a), new Not(b), new Not(c));
        final And formula = new And(or, notOr);

        IFormula cnf = formula.toCNF().orElseThrow();
        final Result<BigInteger> result = Computations.of(cnf)
                .map(ComputeBooleanClauseList::new)
                .map(ComputeCountSolutionGanak::new)
                .computeResult();
        assertTrue(result.isPresent(), result::printProblems);
        assertEquals(BigInteger.valueOf(6),result.get());
    }

    @Test
    public void formulaHas4SolutionsWithProjection() {
        final Literal a = Expressions.literal("a");
        final Literal b = Expressions.literal("b");
        final Literal c = Expressions.literal("c");

        final Or or = new Or(a, b, c);
        final Or notOr = new Or(new Not(a), new Not(b), new Not(c));
        final And formula = new And(or, notOr);

        List<String> variablesToKeep = new ArrayList<>();
        variablesToKeep.add("a");
        variablesToKeep.add("b");
        VariableNamesList variableNamesToKeep = new VariableNamesList(variablesToKeep);

        IFormula cnf = formula.toCNF().orElseThrow();
        final Result<BigInteger> result = Computations.of(cnf)
                .map(ComputeBooleanClauseList::new)
                .map(ComputeCountSolutionGanak::new)
                .set(ComputeCountSolutionGanak.VARIABLES_TO_KEEP, variableNamesToKeep)
                .computeResult();
        assertTrue(result.isPresent(), result::printProblems);
        assertEquals(BigInteger.valueOf(4),result.get());
    }

    @Test
    public void formulaHas4SolutionsWithSlicing() {
        final Literal a = Expressions.literal("a");
        final Literal b = Expressions.literal("b");
        final Literal c = Expressions.literal("c");

        final Or or = new Or(a, b, c);
        final Or notOr = new Or(new Not(a), new Not(b), new Not(c));
        final And formula = new And(or, notOr);

        List<String> variableToRemove = new ArrayList<>();
        variableToRemove.add("a");
        VariableNamesList variableNameToRemove = new VariableNamesList(variableToRemove);

        IFormula cnf = formula.toCNF().orElseThrow();
        final Result<BigInteger> result = Computations.of(cnf)
                .map(ComputeBooleanClauseList::new)
                .map(ComputeCountSolutionGanak::new)
                .set(ComputeCountSolutionGanak.VARIABLES_TO_REMOVE, variableNameToRemove)
                .computeResult();
        assertTrue(result.isPresent(), result::printProblems);
        assertEquals(BigInteger.valueOf(4),result.get());
    }
}

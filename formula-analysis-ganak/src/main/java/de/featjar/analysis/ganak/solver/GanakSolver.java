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

import de.featjar.analysis.ISolver;
import de.featjar.analysis.ganak.bin.GanakBinary;
import de.featjar.analysis.ganak.cli.VariableNamesList;
import de.featjar.base.FeatJAR;
import de.featjar.base.data.Result;
import de.featjar.base.env.Process;
import de.featjar.base.env.TempFile;
import de.featjar.base.io.IO;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.conversion.ComputeBooleanClauseList;
import de.featjar.formula.io.dimacs.BooleanAssignmentListDimacsFormat;

import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GanakSolver implements ISolver {
    protected final BooleanAssignmentList formula;
    protected Duration timeout = Duration.ZERO;
    protected boolean isTimeoutOccurred;

    // regex for matching the solution line 
    protected static Pattern pattern = Pattern.compile("c s exact arb int (\\d+)");

    public GanakSolver(BooleanAssignmentList formula) { // todo: use boolean clause list input
        this.formula = formula;
    }

    public BooleanAssignmentList getFormula() {
        return formula;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        Objects.requireNonNull(timeout);
        FeatJAR.log().debug("setting timeout to " + timeout);
        this.timeout = timeout;
    }

    public boolean isTimeoutOccurred() {
        return isTimeoutOccurred;
    }

    public Result<Boolean> hasSolution() {
        if (countSolution().get().compareTo(BigInteger.ONE) >= 0) {
            return Result.of(true);
        }
        return Result.of(false);
    }

    public Result<BigInteger> countSolution() {
        isTimeoutOccurred = false;
        GanakBinary extension = FeatJAR.extension(GanakBinary.class);
        try (TempFile tempFile = new TempFile("ganakInput", ".dimacs")) {
            IO.save(formula, tempFile.getPath(), new BooleanAssignmentListDimacsFormat());
            Process process = extension.getProcess(
                    tempFile.getPath().toString());
            return process.get().mapResult(this::parseCount);
        } catch (Exception e) {
            FeatJAR.log().error(e);
            return Result.empty(e);
        }
    }

    public Result<BigInteger> countSolution(VariableNamesList include, VariableNamesList exclude) {
        isTimeoutOccurred = false;
        GanakBinary extension = FeatJAR.extension(GanakBinary.class);
        try (TempFile tempFile = new TempFile("ganakInput", ".dimacs")) {
            IO.save(formula, tempFile.getPath(), new BooleanAssignmentListDimacsFormat());

            // get variable indices depending on whether exclude or include is set
            VariableMap variableMap = formula.getVariableMap();
            BooleanAssignment indices = new BooleanAssignment();
            if (!exclude.isEmpty()) {
                VariableMap slicedVariableMap = variableMap.clone();
                for (String variable : exclude) {
                    slicedVariableMap.remove(variable);
                }
                indices = slicedVariableMap.getVariables();
            } else if (!include.isEmpty()) {
                indices = variableMap.getVariables(include);
            }

            // construct a DIMACS comment which indicates which variables are in 
            // the projection set 
            String comment = "c p show ";
            for (int index : indices.get()) {
                comment += index + " ";
            }
            comment += "0 \n";

            // append the comment as the last line to the DIMACS file
            IO.append(comment, tempFile.getPath());
            
            Process process = extension.getProcess(
                    tempFile.getPath().toString());
            return process.get().mapResult(this::parseCount);
        } catch (Exception e) {
            FeatJAR.log().error(e);
            return Result.empty(e);
        }
    }

    private Result<BigInteger> parseCount(List<String> lines) {
        if (lines.isEmpty()) {
            throw new RuntimeException("Not output from solver");
        }

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);

            if (matcher.matches()) {
                return Result.of(new BigInteger(matcher.group(1)));
            }
        }

        throw new RuntimeException(String.format("Could not parse: %s", String.join("\n", lines)));
    }
}

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
import de.featjar.analysis.RuntimeTimeoutException;
import de.featjar.analysis.ganak.bin.GanakBinary;
import de.featjar.base.FeatJAR;
import de.featjar.base.data.Result;
import de.featjar.base.env.Process;
import de.featjar.base.env.TempFile;
import de.featjar.base.io.IO;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.io.dimacs.BooleanAssignmentListDimacsFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GanakSolver implements ISolver {
    protected final BooleanAssignmentList formula;
    protected Duration timeout = Duration.ZERO;
    protected boolean isTimeoutOccurred;

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

    public Result<Integer> countSolution() {
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

    private Result<Integer> parseCount(List<String> lines) {
        if (lines.isEmpty()) {
            throw new RuntimeException("Not output from solver");
        }
        
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("c s exact arb int")) {
                int value = Integer.parseInt(lines.get(i).replaceAll("[^0-9]", ""));
                return Result.of(value);
            }
        }

        throw new RuntimeException(String.format("Could not parse: %s", String.join("\n", lines)));
    }
}

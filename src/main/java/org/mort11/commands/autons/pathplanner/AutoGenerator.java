// AutoGenerator.java
package org.mort11.commands.autons.pathplanner;

import java.util.ArrayList;
import java.util.List;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AutoGenerator extends SequentialCommandGroup {
    static SequentialCommandGroup auto;
    static List<PathPlannerPath> paths;
    static int pathIndex;

    public AutoGenerator() {
        auto = new SequentialCommandGroup();
    }

    public static Command generate(String autoName, Command... otherCommands) throws ClassNotFoundException {
        return buildAuto(autoName, false, otherCommands);
    }

    public static Command generateMirrored(String autoName, Command... otherCommands) throws ClassNotFoundException {
        return buildAuto(autoName, true, otherCommands);
    }

    private static Command buildAuto(String autoName, boolean mirror, Command... otherCommands) throws ClassNotFoundException {
        auto = new SequentialCommandGroup();

        try {
            paths = PathPlannerAuto.getPathGroupFromAutoFile(autoName);
        } catch (Exception e) {
            throw new ClassNotFoundException("Auto file not found: " + autoName);
        }

        if (mirror) {
            List<PathPlannerPath> flippedPaths = new ArrayList<>();
            for (PathPlannerPath path : paths) {
                flippedPaths.add(path.flipPath());
            }
            paths = flippedPaths;
        }

        int greaterLength = (otherCommands.length > paths.size() ? otherCommands.length : paths.size());

        for (int i = 0; i < greaterLength; i++) {
            if (otherCommands.length < (i + 1) && paths.size() >= (i)) {
                auto = new SequentialCommandGroup(
                    auto,
                    AutoBuilder.followPath(paths.get(i))
                );
            } else if (otherCommands.length >= (i) && paths.size() < (i + 1)) {
                auto = new SequentialCommandGroup(
                    auto,
                    otherCommands[i]
                );
            } else {
                auto = new SequentialCommandGroup(
                    auto,
                    otherCommands[i],
                    AutoBuilder.followPath(paths.get(i))
                );
            }
        }

        return auto;
    }
}
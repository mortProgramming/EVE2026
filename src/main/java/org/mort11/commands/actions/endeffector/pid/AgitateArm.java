package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import org.mort11.subsystems.IntakeArm;

public class AgitateArm extends SequentialCommandGroup {

    public AgitateArm(IntakeArm arm) {
        addCommands(
            Commands.repeatingSequence(
                new SetArm(arm, IntakeArm.Position.AGITATE).withTimeout(0.5),
                new SetArm(arm, IntakeArm.Position.INTAKE).withTimeout(0.5)
            )
        );
    }
}
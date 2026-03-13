package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.IntakeArm;

public class SetArm extends Command {

    private final IntakeArm arm;
    private final IntakeArm.Position position;

    public SetArm(IntakeArm arm, IntakeArm.Position position) {

        this.arm = arm;
        this.position = position;

        addRequirements(arm);
    }

    @Override
    public void initialize() {

        arm.setPivot(position);

    }

    @Override
    public boolean isFinished() {

        return arm.isPositionWithinTolerance();

    }
}
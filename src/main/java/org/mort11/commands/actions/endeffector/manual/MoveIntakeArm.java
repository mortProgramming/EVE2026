package org.mort11.commands.actions.endeffector.manual;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.IntakeArm;

public class MoveIntakeArm extends Command {

    private final IntakeArm arm;
    private final DoubleSupplier input;

    public MoveIntakeArm(IntakeArm arm, DoubleSupplier input) {

        this.arm = arm;
        this.input = input;

        setName("Move Intake Arm");

        addRequirements(arm);
    }

    @Override
    public void execute() {

        double value = MathUtil.applyDeadband(input.getAsDouble(), 0.05);

        arm.setManualPercent(value);

    }

    @Override
    public void end(boolean interrupted) {

        arm.stop();

    }
}
package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;

import org.mort11.RobotContainer;
import org.mort11.subsystems.Shooter;

import static org.mort11.configs.constants.PortConstants.Controller.*;

public class SetShooter extends Command {
    private final Shooter shooter;
    private final DoubleSupplier rpm;

    public SetShooter(Shooter shooter, DoubleSupplier rpm) {
        this.shooter = shooter;
        this.rpm = rpm;
        addRequirements(shooter);
    }

    public SetShooter(Shooter shooter, double rpm) {
        this(shooter, () -> rpm);
    }

    @Override
    public void execute() {
        double targetRPM = rpm.getAsDouble();

        if (targetRPM == 0) {
            shooter.stop();
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
            return;
        }

        shooter.setRPM(targetRPM);

        if (shooter.isVelocityWithinTolerance()) {
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, RUMBLE_AMOUNT);
        } else {
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
        }
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
        RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
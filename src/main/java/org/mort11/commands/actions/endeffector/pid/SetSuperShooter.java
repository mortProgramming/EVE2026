package org.mort11.commands.actions.endeffector.pid;

import java.util.function.DoubleSupplier;

import org.mort11.commands.actions.endeffector.manual.moveFeeder;
import org.mort11.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

import static org.mort11.configs.constants.PhysicalConstants.Shooter.*;

public class SetSuperShooter extends ParallelCommandGroup {
    
    public SetSuperShooter(DoubleSupplier shooterRPM, DoubleSupplier turretDeg, DoubleSupplier hoodDeg) {
        addCommands(
            new ParallelCommandGroup(
                new SetShooter(shooterRPM),
                new SetTurret(turretDeg),
                new SetEvanHood(hoodDeg)
            ),
            new SequentialCommandGroup(
                new WaitUntilCommand(() -> Shooter.getInstance().isAtTargetRPM(shooterRPM.getAsDouble())),
                new moveFeeder(-1).withTimeout(1.0)
            )
        );
    }
    
    public SetSuperShooter(double shooterRPM, double turretDeg, double hoodDeg) {
        addCommands(
            new ParallelRaceGroup(
                new SetShooter(shooterRPM),
                new SetTurret(turretDeg),
                new SetEvanHood(hoodDeg)
            ),
            new SequentialCommandGroup(
                new WaitUntilCommand(() -> Shooter.getInstance().isAtTargetRPM(shooterRPM)),
                new moveFeeder(-1).withTimeout(1.0)
            )
        );
    }
}
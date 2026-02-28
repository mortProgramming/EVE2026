package org.mort11.commands.actions.endeffector.pid;

import java.util.function.DoubleSupplier;

import org.mort11.commands.actions.endeffector.manual.moveFeeder;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class SetSuperShooter extends SequentialCommandGroup {
    
    public SetSuperShooter(DoubleSupplier shooterRPM, DoubleSupplier turretDeg, DoubleSupplier hoodDeg) {
        addCommands(
            new ParallelRaceGroup(
                new SetShooter(shooterRPM),
                new SetTurret(turretDeg),
                new SetEvanHood(hoodDeg)
            ),
            new moveFeeder(-1).withTimeout(1.0)
        );
    }
    
    public SetSuperShooter(double shooterRPM, double turretDeg, double hoodDeg) {
        addCommands(
            new ParallelRaceGroup(
                new SetShooter(shooterRPM),
                new SetTurret(turretDeg),
                new SetEvanHood(hoodDeg)
            ),
            new moveFeeder(-1).withTimeout(1.0)
        );
    }
}

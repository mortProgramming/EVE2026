package org.mort11.commands.actions.endeffector.pid;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class SetSuperShooter extends ParallelCommandGroup {
    
    public SetSuperShooter(DoubleSupplier shooterRPM, DoubleSupplier turretDeg, DoubleSupplier hoodDeg) {
        addCommands(
            new SetHoodShooter(shooterRPM, hoodDeg),
            new SetTurret(turretDeg)
        );
    }
    
    public SetSuperShooter(double shooterRPM, double turretDeg, double hoodDeg) {
        addCommands(
            new SetHoodShooter(shooterRPM, hoodDeg),
            new SetTurret(turretDeg)
        );
    }
}

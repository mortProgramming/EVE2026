package org.mort11.commands.actions.endeffector.pid;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class SetHoodShooter extends ParallelCommandGroup {
    
    public SetHoodShooter(DoubleSupplier shooterRPM, DoubleSupplier hoodDeg) {
        addCommands(
            new SetShooter(shooterRPM),
            new SetEvanHood(hoodDeg)
        );
    }
    
    public SetHoodShooter(double shooterRPM, double hoodDeg) {
        addCommands(
            new SetShooter(shooterRPM),
            new SetEvanHood(hoodDeg)
        );
    }
}

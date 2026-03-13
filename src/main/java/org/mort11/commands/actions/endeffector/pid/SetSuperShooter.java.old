package org.mort11.commands.actions.endeffector.pid;

import java.util.function.DoubleSupplier;

import org.mort11.commands.actions.endeffector.manual.moveFeeder;
import org.mort11.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class SetSuperShooter extends ParallelCommandGroup {
    
    public SetSuperShooter(DoubleSupplier shooterRPM, DoubleSupplier hoodDeg) {
        addCommands(
            new SetShooter(shooterRPM),
            new SetEvanHood(hoodDeg),          
            new SequentialCommandGroup(        
                new WaitUntilCommand(() -> Shooter.getInstance().isAtTargetRPM(shooterRPM.getAsDouble())),
                new moveFeeder(-1).withTimeout(1.0)
            )
        );
    }

    public SetSuperShooter(double shooterRPM, double hoodDeg) {
        addCommands(
            new SetShooter(shooterRPM),
            new SetEvanHood(hoodDeg),
            new SequentialCommandGroup(
                new WaitUntilCommand(() -> Shooter.getInstance().isAtTargetRPM(shooterRPM)),
                new moveFeeder(-1).withTimeout(1.0)
            )
        );
    }
}
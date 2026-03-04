package org.mort11.commands.autons.pathplanner;

import org.mort11.commands.actions.endeffector.manual.moveFeeder;
import org.mort11.commands.actions.endeffector.manual.moveLeftIntake;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.actions.endeffector.pid.SetSuperShooter;
import org.mort11.commands.actions.endeffector.pid.SetTurret;
import org.mort11.commands.autons.timed.Taxi;
import org.mort11.configs.LookUpTable;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.EvanHood;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;
import org.mort11.subsystems.Vision;

import com.pathplanner.lib.auto.NamedCommands;
public class BasicCommands {
    // Remove all the static fields — pass what you need into setCommands()
    
    public static void setCommands(OdometryHelper odometry) {
        NamedCommands.registerCommand("Taxi", new Taxi());

        NamedCommands.registerCommand(
            "SetSuperShooterdist",
            new SetSuperShooter(
                () -> LookUpTable.getNeededShooterRPM(odometry.getDistanceToHub()),
                () -> LookUpTable.getNeededHoodAngle(odometry.getDistanceToHub())
            )
        );

        NamedCommands.registerCommand("SetShooter", new SetShooter(1750));

        
        NamedCommands.registerCommand("Feeder", new moveFeeder(-1).withTimeout(1.5));
        NamedCommands.registerCommand("Intake", new moveLeftIntake(1));
    }
}
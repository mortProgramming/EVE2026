package org.mort11.commands.autons.pathplanner;

// import org.mort11.commands.actions.endeffector.moveIntake.java.moveLeftIntake;
// import org.mort11.commands.actions.endeffector.moveIntake.java.moveLeftRoller;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.autons.timed.Taxi;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class BasicCommands {

    public static void setCommands(OdometryHelper odometry, Shooter shooter) {
        NamedCommands.registerCommand("Taxi", new Taxi());

        NamedCommands.registerCommand("SetShooter", 
            new SetShooter(shooter, 1750).withTimeout(1.5));

        NamedCommands.registerCommand("SetShooterFast", 
            new SetShooter(shooter, 1750).withTimeout(0.5));

        // NamedCommands.registerCommand("Feeder", 
        //     new ParallelCommandGroup(
        //         new moveFeeder(-1),
        //         new SetShooter(shooter, 2250)
        //     ).withTimeout(3));

        NamedCommands.registerCommand("StaticTrenchShoot", 
            new SetShooter(shooter, 0));

        NamedCommands.registerCommand("StaticBumpShoot", 
            new SetShooter(shooter, 0));

        // NamedCommands.registerCommand("IntakeArmDown", 
        //     new moveLeftIntake(-0.25).withTimeout(2.25));

        // NamedCommands.registerCommand("IntakeArmUp", 
        //     new moveLeftIntake(0.25).withTimeout(0.7));

        // NamedCommands.registerCommand("IntakeFeather", 
        //     moveLeftIntake.IntakeFeather());

        // NamedCommands.registerCommand("IntakeRoller", 
        //     new moveLeftRoller(1).withTimeout(5));

        // NamedCommands.registerCommand("Intake", 
        //     new moveLeftIntake(-0.25).withTimeout(0.9));

        // NamedCommands.registerCommand("Intake Roller", 
        //     new moveLeftRoller(1).withTimeout(5));
    }
}
package org.mort11.commands.autons.pathplanner;

import org.mort11.commands.actions.endeffector.manual.moveFeeder;
import org.mort11.commands.actions.endeffector.manual.moveLeftIntake;
import org.mort11.commands.actions.endeffector.manual.moveLeftRoller;
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

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
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

        NamedCommands.registerCommand("SetShooter", new SetShooter(1750).withTimeout(1.5));
        NamedCommands.registerCommand("SetShooterFast", new SetShooter(1750).withTimeout(.5));

        // NamedCommands.registerCommand("IntakeArmDown", new SetShooter(1750).withTimeout(3));
        // NamedCommands.registerCommand("IntakeArmDown", new SetShooter(1750).withTimeout(3));


        
        NamedCommands.registerCommand("Feeder", 
        new ParallelCommandGroup(
            new moveFeeder(-1),
         new SetShooter(2250)
        ).withTimeout(3));
        //feeder with rpm catch

        // NamedCommands.registerCommand("Feeder", 
        // new ParallelCommandGroup(
        //     new moveFeeder(-1),
        //  new SetShooter(2250)
        // ).until(it has been at target RPM for 3 seconds));

        NamedCommands.registerCommand("IntakeArmDown", new moveLeftIntake(-0.25).withTimeout(2.25));
        NamedCommands.registerCommand("IntakeArmUp", new moveLeftIntake(0.25).withTimeout(0.7));

        //This one runs repeateadly check IntakeFeather
        NamedCommands.registerCommand("IntakeFeather", moveLeftIntake.IntakeFeather());

        

        NamedCommands.registerCommand("IntakeRoller", new moveLeftRoller(1).withTimeout(5));

        //Warren Hills stuff
        NamedCommands.registerCommand("StaticTrenchShoot",new SetShooter(0/*this rpm value must be the rpm needed to shooot from trench */));
        NamedCommands.registerCommand("StaticBumpShoot",new SetShooter(0/*this rpm value must be the rpm needed to shooot from bump */));
            new SetShooter(2250)
        .withTimeout(3);
        NamedCommands.registerCommand("Intake", new moveLeftIntake(-0.25).withTimeout(0.9));
        NamedCommands.registerCommand("Intake Roller", new moveLeftRoller(1).withTimeout(5));
        
    }
}
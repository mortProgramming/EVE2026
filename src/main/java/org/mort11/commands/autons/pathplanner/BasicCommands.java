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
    public static CommandSwerveDrivetrain drivetrain;
    public static Vision vision;
    public static EvanHood hood;
    public static Shooter shooter;
    private static final OdometryHelper odometry = new OdometryHelper(drivetrain);


    public static void setCommands() {
    NamedCommands.registerCommand("Taxi", new Taxi()); 
    NamedCommands.registerCommand("SetSuperShooter0inches", new SetSuperShooter(1750, odometry.getAngleForTurretDeg(), 73));
    NamedCommands.registerCommand("Set Super Shooter 71 inches", new SetSuperShooter(1750, odometry.getAngleForTurretDeg(), 73));
    // Indented to look nicer, above didn't use double suppliers which could lead to it not running correctly due to maybe race command
    NamedCommands.registerCommand(
        "SetSuperShooterdist",
        new SetSuperShooter(
            () -> LookUpTable.getNeededShooterRPM(odometry.getDistanceToHub()),
            () -> odometry.getAngleForTurretDeg(),
            () -> LookUpTable.getNeededHoodAngle(odometry.getDistanceToHub())
        )
    );   
    NamedCommands.registerCommand("SetShooter", new SetShooter(1750));
    NamedCommands.registerCommand("SetTurret", new SetTurret(odometry.getAngleForTurretDeg()));
    NamedCommands.registerCommand("Feeder", new moveFeeder(-1).withTimeout(1.5));
    NamedCommands.registerCommand("Intake", new moveLeftIntake(1)); 
    // NamedCommands.registerCommand("SetTurret", new SetTurret(null));
    // NamedCommands.registerCommand("SetEvanHood", new SetEvanHood(null));
    // NamedCommands.registerCommand("SetSuperShooter", new SetSuperShooter(null, null, null)); 
        
    }  
}

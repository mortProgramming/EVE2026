package org.mort11.commands.autons.pathplanner;

import org.mort11.RobotContainer;
import org.mort11.commands.actions.endeffector.manual.moveFeeder;
import org.mort11.commands.actions.endeffector.manual.moveLeftIntake;
import org.mort11.commands.actions.endeffector.pid.SetEvanHood;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.actions.endeffector.pid.SetSuperShooter;
import org.mort11.commands.actions.endeffector.pid.SetTurret;
import org.mort11.commands.autons.timed.Taxi;
import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.Vision;

import com.pathplanner.lib.auto.NamedCommands;

public class BasicCommands {
    public static CommandSwerveDrivetrain drivetrain;
    public static Vision vision;

    public static void setCommands() {
    NamedCommands.registerCommand("Taxi", new Taxi()); 
    NamedCommands.registerCommand("SetSuperShooter0inches", new SetSuperShooter(1750, 0, 73));
    NamedCommands.registerCommand("Set Super Shooter 71 inches", new SetSuperShooter(1750, 0, 73));
    NamedCommands.registerCommand("SetShooter", new SetShooter(1750));
    NamedCommands.registerCommand("Feeder", new moveFeeder(-1).withTimeout(1.5)); 
    NamedCommands.registerCommand("Intake", new moveLeftIntake(1)); 
    // NamedCommands.registerCommand("SetTurret", new SetTurret(null));
    // NamedCommands.registerCommand("SetEvanHood", new SetEvanHood(null));
    // NamedCommands.registerCommand("SetSuperShooter", new SetSuperShooter(null, null, null)); 
        
    }
}

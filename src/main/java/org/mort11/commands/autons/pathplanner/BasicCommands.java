package org.mort11.commands.autons.pathplanner;

import org.mort11.commands.actions.endeffector.manual.MoveFeeder;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeArm;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeRoller;
import org.mort11.commands.actions.endeffector.pid.AgitateArm;
import org.mort11.commands.actions.endeffector.pid.SetArm;
//import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;
import org.mort11.subsystems.Floor;
import org.mort11.subsystems.Feeder;
import com.pathplanner.lib.auto.NamedCommands;

public class BasicCommands {

    public static void setCommands(OdometryHelper odometry, Shooter shooter, IntakeArm intake, IntakeRoller intakeRoller, Feeder feeder, Floor floor) {
        // NamedCommands.registerCommand("Taxi", new Taxi());
        //IntakeArm commands
        NamedCommands.registerCommand("IntakeUp", new SetArm(intake, IntakeArm.Position.HOMED));
        NamedCommands.registerCommand("IntakeDown", new SetArm(intake, IntakeArm.Position.INTAKE));
        NamedCommands.registerCommand("IntakeAgitate", new AgitateArm(intake));
        //IntakeRoller/Feeder commands
        NamedCommands.registerCommand("IntakeRollerIntake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        NamedCommands.registerCommand("IntakeRollerOuttake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.OUTTAKE));
        NamedCommands.registerCommand("FeederIntake", new MoveFeeder(feeder, floor));
        NamedCommands.registerCommand("FeederOuttake", new MoveFeeder(feeder, floor));
        //
        //Shooter commands
        //NamedCommands.registerCommand("SetShooter", new SetShooter(shooter, 4000));

    }
}
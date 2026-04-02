package org.mort11.commands.autons.pathplanner;

import org.mort11.commands.actions.endeffector.manual.MoveClimber;
import org.mort11.commands.actions.endeffector.manual.MoveFeeder;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeArm;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeRoller;
import org.mort11.commands.actions.endeffector.pid.AgitateArm;
import org.mort11.commands.actions.endeffector.pid.PrepareShotCommand;
import org.mort11.commands.actions.endeffector.pid.SetArm;
import org.mort11.commands.actions.endeffector.pid.SetFeeder;

import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.autons.apriltag.RotateToHub;
//import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;
import org.mort11.subsystems.Floor;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.Feeder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.FollowPathCommand;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class BasicCommands {

    public static void setCommands(OdometryHelper odometry, Shooter shooter, IntakeArm intake, IntakeRoller intakeRoller, Floor floor, Feeder feeder, Hood hood) {
        // NamedCommands.registerCommand("Taxi", new Taxi());
        //IntakeArm commands
        PrepareShotCommand prepareShot = new PrepareShotCommand(shooter, hood, odometry); 

        NamedCommands.registerCommand("IntakeUp", new SetArm(intake, IntakeArm.Position.HOMED));
        NamedCommands.registerCommand("IntakeDown", new SetArm(intake, IntakeArm.Position.INTAKE));
        NamedCommands.registerCommand("IntakeAgitate", new AgitateArm(intake));
        NamedCommands.registerCommand("WaitIntakeAgitate", new AgitateArm(intake).until(prepareShot::isReadyToShoot)); 
        

        //IntakeRoller/Feeder commands
        NamedCommands.registerCommand("IntakeRollerIntake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        NamedCommands.registerCommand("IntakeRollerOuttake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.OUTTAKE));
        
        //Climber commands
        NamedCommands.registerCommand("Climb", new MoveClimber(-1));
        //Shooter commands
        NamedCommands.registerCommand("WindUp" , new SetShooter(3400).withTimeout(2));
        NamedCommands.registerCommand("SetShooter" , new SetShooter(3200));//was 3400
        NamedCommands.registerCommand("FeederIntake", new SetFeeder(4000));

        NamedCommands.registerCommand("PrepareAndShoot",
            new ParallelDeadlineGroup(
                //wait until ready, then feed 
                new WaitUntilCommand(prepareShot::isReadyToShoot)
                    .withTimeout(1)
                    .andThen(new SetFeeder(5500).withTimeout(6)),
                prepareShot  //shooter + hood active the entire time including during the feed
            )
        );

        //drive commands
        NamedCommands.registerCommand("LockOn",(new RotateToHub(odometry)).withTimeout(2));


    }
}
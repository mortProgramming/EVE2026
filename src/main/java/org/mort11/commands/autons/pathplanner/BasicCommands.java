package org.mort11.commands.autons.pathplanner;

import org.mort11.commands.actions.endeffector.manual.MoveClimber;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeRoller;
import org.mort11.commands.actions.endeffector.pid.AgitateArm;
import org.mort11.commands.actions.endeffector.pid.PrepareShotCommand;
import org.mort11.commands.actions.endeffector.pid.SetArm;
import org.mort11.commands.actions.endeffector.pid.SetFeeder;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.autons.apriltag.RotateToHub;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class BasicCommands {

    public static void setCommands(OdometryHelper odometry, Shooter shooter, IntakeArm intake, IntakeRoller intakeRoller, Floor floor, Feeder feeder, Hood hood) {

        PrepareShotCommand prepareShot = new PrepareShotCommand(shooter, hood, odometry);
        PrepareShotCommand prepareShotTest = new PrepareShotCommand(shooter, hood, odometry);

        //intakeArm commands
        NamedCommands.registerCommand("IntakeUp", new SetArm(intake, IntakeArm.Position.HOMED));
        NamedCommands.registerCommand("IntakeDown", new SetArm(intake, IntakeArm.Position.INTAKE));
        NamedCommands.registerCommand("IntakeAgitate", new AgitateArm(intake));
        NamedCommands.registerCommand("IntakeAgitateShort", new AgitateArm(intake).withTimeout(3.5));
        NamedCommands.registerCommand("WaitIntakeAgitate", new AgitateArm(intake).until(prepareShot::isReadyToShoot));

        //intakeRoller/feeder commands
        NamedCommands.registerCommand("IntakeRollerIntake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        NamedCommands.registerCommand("IntakeRollerOuttake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.OUTTAKE));

        //climber commands
        NamedCommands.registerCommand("Climb", new MoveClimber(-1));

        //shooter commands
        NamedCommands.registerCommand("WindUp", new SetShooter(3400).withTimeout(2));
        NamedCommands.registerCommand("SetShooter", new SetShooter(3200));
        NamedCommands.registerCommand("FeederIntake", new SetFeeder(4000));

        //prepare and shoot commands
        NamedCommands.registerCommand("PrepareAndShoot", prepareShot);
        NamedCommands.registerCommand("PrepareToShoot", prepareShot);

        NamedCommands.registerCommand("PrepareAndShootTest",
            new ParallelCommandGroup(
                new ParallelDeadlineGroup(
                    new WaitUntilCommand(prepareShotTest::isReadyToShoot)
                        .withTimeout(1)
                        .andThen(new SetFeeder(5500).withTimeout(4)),
                    prepareShotTest
                )
            )
        );

        //drive commands
        NamedCommands.registerCommand("LockOn", new RotateToHub(odometry).withTimeout(2.5));
    }
}
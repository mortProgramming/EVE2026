package org.mort11.commands.autons.pathplanner;

import org.mort11.commands.actions.endeffector.manual.MoveClimber;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeRoller;
import org.mort11.commands.actions.endeffector.pid.AgitateArm;
import org.mort11.commands.actions.endeffector.pid.PrepareShotCommand;
import org.mort11.commands.actions.endeffector.pid.SetArm;
import org.mort11.commands.actions.endeffector.pid.SetFeeder;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.actions.endeffector.pid.ShootFar;
import org.mort11.commands.autons.apriltag.RotateToHub;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

import java.util.Set;

public class BasicCommands {

    public static void setCommands(OdometryHelper odometry, Shooter shooter, IntakeArm intake, IntakeRoller intakeRoller, Floor floor, Feeder feeder, Hood hood) {

        // intakeArm commands
        NamedCommands.registerCommand("IntakeUp", new SetArm(intake, IntakeArm.Position.HOMED));
        NamedCommands.registerCommand("IntakeDown", new SetArm(intake, IntakeArm.Position.INTAKE));
        NamedCommands.registerCommand("IntakeAgitate", new AgitateArm(intake));
        NamedCommands.registerCommand("IntakeAgitateShort", new AgitateArm(intake).withTimeout(3.5));

        // intakeRoller/feeder commands
        NamedCommands.registerCommand("IntakeRollerIntake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        NamedCommands.registerCommand("IntakeRollerOuttake", new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.OUTTAKE));

        // climber commands
        NamedCommands.registerCommand("Climb", new MoveClimber(-1));

        // shooter commands
        NamedCommands.registerCommand("WindUp", new SetShooter(3400).withTimeout(2));
        NamedCommands.registerCommand("SetShooter", new SetShooter(3200));
        NamedCommands.registerCommand("FeederIntake", new SetFeeder(4000));
        NamedCommands.registerCommand("ShooterManual", new ShootFar(hood, 2500, 0.23));

        // drive commands
        NamedCommands.registerCommand("LockOn", new RotateToHub(odometry).withTimeout(2.5));

        NamedCommands.registerCommand("WaitIntakeAgitate",
            Commands.defer(() -> {
                PrepareShotCommand fresh = new PrepareShotCommand(shooter, hood, odometry);
                return new AgitateArm(intake).until(fresh::isReadyToShoot);
            }, Set.of(shooter, hood, intake))
        );

        NamedCommands.registerCommand("PrepareAndShoot",
            Commands.defer(() -> new PrepareShotCommand(shooter, hood, odometry),
                Set.of(shooter, hood))
        );

        NamedCommands.registerCommand("PrepareToShoot",
            Commands.defer(() -> new PrepareShotCommand(shooter, hood, odometry),
                Set.of(shooter, hood))
        );

        NamedCommands.registerCommand("PrepareShot",
            Commands.defer(() -> new PrepareShotCommand(shooter, hood, odometry),
                Set.of(shooter, hood))
        );

        // PrepareAndShootTest — the one you actually use in paths
        NamedCommands.registerCommand("PrepareAndShootTest",
            Commands.defer(() -> {
                PrepareShotCommand fresh = new PrepareShotCommand(shooter, hood, odometry);
                return new ParallelDeadlineGroup(
                    new SequentialCommandGroup(
                        new WaitUntilCommand(fresh::isReadyToShoot).withTimeout(1),
                        new SetFeeder(5500).withTimeout(4)
                    ),
                    fresh
                );
            }, Set.of(shooter, hood))
        );
    }
}
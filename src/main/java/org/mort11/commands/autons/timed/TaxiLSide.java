package org.mort11.commands.autons.timed;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import org.mort11.commands.autons.timed.TimedDrive;
import org.mort11.commands.autons.timed.TimedFeed;
import org.mort11.commands.autons.timed.TimedIntake;
import org.mort11.commands.autons.timed.TimedIntakeArm;
import org.mort11.commands.autons.timed.TimedRotateToHub;
import org.mort11.commands.autons.timed.TimedShoot;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

/*
deploy arm down to INTAKE position 
drive left away from hub, rotate toward hub + spin up shooter
feed + shoot

Register in RobotContainer:
   new TaxiLSide(intakeArm, intakeRoller, shooter, hood, odometry)

*/
public class TaxiLSide extends SequentialCommandGroup {

    public TaxiLSide(
        IntakeArm arm,
        IntakeRoller roller,
        Shooter shooter,
        Hood hood,
        OdometryHelper odometry
    ) {
        addCommands(
            new ParallelCommandGroup(
                //intake rolls the entire auton
                new TimedIntake(20, roller, IntakeRoller.Speed.INTAKE),

                new SequentialCommandGroup(
                    //arm down before driving
                    new TimedIntakeArm(1.5, arm, IntakeArm.Position.INTAKE),

                    //drive left away from the hub
                    new TimedDrive(2, 0, -1, 0),

                    //rotate toward hub + spin up shooter simultaneously
                    new ParallelCommandGroup(
                        new TimedRotateToHub(2, odometry),
                        new TimedShoot(3, shooter, hood, odometry)
                    ),

                    // feed + shoot
                    new ParallelCommandGroup(
                        new TimedShoot(10, shooter, hood, odometry),
                        new TimedFeed(10, 4800)
                    )
                )
            )
        );
    }
}
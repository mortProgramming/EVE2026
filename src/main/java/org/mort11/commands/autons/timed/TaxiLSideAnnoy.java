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
arm + drive forward simultaneously
 drive back to starting position
 deploy arm + rotate to hub + spin up shooter simultaneously
 feed + shoot
 */
public class TaxiLSideAnnoy extends SequentialCommandGroup {

    public TaxiLSideAnnoy(
        IntakeArm arm,
        IntakeRoller roller,
        Shooter shooter,
        Hood hood,
        OdometryHelper odometry
    ) {
        addCommands(
            new ParallelCommandGroup(

                //intake roller runs for the entire auton
                new TimedIntake(20, roller, IntakeRoller.Speed.INTAKE),

                new SequentialCommandGroup(

                    //deploy arm + drive forward simultaneously
                    new ParallelCommandGroup(
                        new TimedIntakeArm(2, arm, IntakeArm.Position.INTAKE),
                        new TimedDrive(2, 0, 2.5, 0)
                    ),

                    new TimedDrive(2, 2.2, 0, 0),

                    //arm, back left simultaneously
                    new ParallelCommandGroup(
                        new TimedIntakeArm(0.8, arm, IntakeArm.Position.HOMED),
                        new TimedDrive(2, -2.2, 0, 0)
                    ),

                    //drive back to starting position
                    new TimedDrive(2, 0, -2.5, 0),

                    //forward slightly
                    new TimedDrive(2, 0, 1, 0),

                    //deploy arm + rotate to hub + spin up shooter simultaneously
                    new ParallelCommandGroup(
                        new TimedIntakeArm(0.8, arm, IntakeArm.Position.INTAKE),
                        new TimedRotateToHub(3, odometry),
                        new TimedShoot(3, shooter, hood, odometry)
                    ),

                    // Step 7: Feed + shoot
                    new ParallelCommandGroup(
                        new TimedShoot(10, shooter, hood, odometry),
                        new TimedFeed(10, 4800)
                    )
                )
            )
        );
    }
}
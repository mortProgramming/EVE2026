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
 
deploy arm down to INTAKE position first 
drive back while intaking
rotate + keep arm deployed
sweep forward and back to collect balls
rotate back to face hub
intake arm while spinning up shooter 
feed + shoot

 */
public class TaxiCenterDepot extends SequentialCommandGroup {

    public TaxiCenterDepot(
        IntakeArm arm,
        IntakeRoller roller,
        Shooter shooter,
        Hood hood,
        OdometryHelper odometry
    ) {
        addCommands(
            new ParallelCommandGroup(
                // Intake rolls throughout the entire auton
                new TimedIntake(20, roller, IntakeRoller.Speed.INTAKE),

                new SequentialCommandGroup(
                    new TimedIntakeArm(1.5, arm, IntakeArm.Position.INTAKE),

                    //drive backwards away from the hub
                    new TimedDrive(2, -1, -1, 0),

                    //rotate to face depot, armalreadyyy down
                    new TimedDrive(2, 0, 0, -1),

                    new SequentialCommandGroup(
                        //sweep forward to collect balls
                        new TimedDrive(2, 0.75, 0, 0),

                        //abck
                        new TimedDrive(2, -0.75, 0, 0),

                        //rotate back toward hub
                        new TimedRotateToHub(2, odometry),

                        //arm down, spin up shooter, then feed
                        new ParallelCommandGroup(
                            new TimedIntakeArm(0.6, arm, IntakeArm.Position.HOMED),

                            new SequentialCommandGroup(
                                //spin up shooter while rotating
                                new ParallelCommandGroup(
                                    new TimedRotateToHub(2, odometry),
                                    new TimedShoot(3, shooter, hood, odometry)
                                ),

                                // feeder + shooter running together
                                new ParallelCommandGroup(
                                    new TimedShoot(10, shooter, hood, odometry),
                                    new TimedFeed(10, 4800)
                                )
                            )
                        )
                    )
                )
            )
        );
    }
}
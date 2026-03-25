package org.mort11.commands.autons.timed;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import org.mort11.commands.autons.timed.TimedDrive;
import org.mort11.commands.autons.timed.TimedFeed;
import org.mort11.commands.autons.timed.TimedIntake;
import org.mort11.commands.autons.timed.TimedIntakeArm;
import org.mort11.commands.autons.timed.TimedShoot;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

/*
 * Sequence:
 *  1. Drive back while intaking
 *  2. Rotate + deploy intake arm
 *  3. Sweep forward and back to collect balls
 *  4. Rotate back to face hub
 *  5. Stow intake arm while spinning up shooter (distance-based)
 *  6. Feed + shoot
 *
 * Register in RobotContainer:
 *   new TaxiCenterDepot(m_intakeArm, m_intakeRoller, m_shooter, m_hood, m_odometryHelper)
 *
 * TUNE:
 *  - TimedDrive speeds/directions for your field side
 *  - TimedFeed RPM (currently PhysicalConstants.Feeder.FEED_RPM = 4800)
 *  - TimedIntakeArm durations — confirm arm reaches position in time given
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
                // Keep rolling intake throughout the entire drive sequence
                new TimedIntake(20, roller, IntakeRoller.Speed.INTAKE),

                new SequentialCommandGroup(
                    // Step 1: Drive backwards away from the hub
                    new TimedDrive(2, -1, -1, 0),

                    // Step 2: Rotate to face depot + deploy intake arm simultaneously
                    new ParallelCommandGroup(
                        new TimedDrive(2, 0, 0, -1),
                        new TimedIntakeArm(0.6, arm, IntakeArm.Position.INTAKE)
                    ),

                    new SequentialCommandGroup(
                        // Step 3a: Sweep forward to collect balls
                        new TimedDrive(2, 0.75, 0, 0),

                        // Step 3b: Sweep back
                        new TimedDrive(2, -0.75, 0, 0),

                        // Step 4: Rotate back toward hub
                        new TimedDrive(2, 0, 0, 1),

                        // Step 5 + 6: Stow arm, spin up shooter, then feed
                        new ParallelCommandGroup(
                            new TimedIntakeArm(0.6, arm, IntakeArm.Position.STOWED),

                            new SequentialCommandGroup(
                                // Spin up shooter while finishing the rotate
                                new ParallelCommandGroup(
                                    new TimedDrive(2, 0, 0, 0.58),
                                    new TimedShoot(3, shooter, hood, odometry)
                                ),

                                // Shoot — feeder + shooter running together
                                // FEED_RPM = 4800 from PhysicalConstants.Feeder
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
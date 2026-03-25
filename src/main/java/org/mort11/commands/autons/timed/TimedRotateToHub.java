package org.mort11.commands.autons.timed;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import org.mort11.RobotContainer;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.OdometryHelper;

public class TimedRotateToHub extends Command {

    private final CommandSwerveDrivetrain drivetrain;
    private final OdometryHelper odometry;
    private final Timer timer;
    private final double time;

    public TimedRotateToHub(double time, OdometryHelper odometry) {
        this.drivetrain = RobotContainer.getSwerveDrivetrain();
        this.odometry = odometry;
        this.time = time;
        this.timer = new Timer();
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        timer.reset();
        timer.start();
    }

    @Override
    public void execute() {
        Pose2d robotPose = odometry.getPose();

        Translation2d hub = odometry.getHubTarget().getTranslation();
        Translation2d robotToHub = hub.minus(robotPose.getTranslation());
        Rotation2d fieldAngleToHub = robotToHub.getAngle();

        double headingErrorDeg = fieldAngleToHub
            .minus(robotPose.getRotation())
            .getDegrees();

        headingErrorDeg = Math.IEEEremainder(headingErrorDeg, 360.0);

        double rotationSpeed = drivetrain.calculateChangeRotateController(headingErrorDeg);

        // No translation during auton — robot stays put and just rotates
        drivetrain.setDrive(new ChassisSpeeds(0, 0, -rotationSpeed));

        SmartDashboard.putNumber("Auton Heading Error to Hub (deg)", headingErrorDeg);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.setDrive(new ChassisSpeeds(0, 0, 0));
    }

    @Override
    public boolean isFinished() {
        return timer.get() > time;
    }
}
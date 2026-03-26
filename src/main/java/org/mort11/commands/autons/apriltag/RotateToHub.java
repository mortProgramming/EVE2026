package org.mort11.commands.autons.apriltag;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import org.mort11.RobotContainer;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.OdometryHelper;

public class RotateToHub extends Command {

    private final CommandSwerveDrivetrain drivetrain;
    private final OdometryHelper odometryHelper;
    private final boolean holdContinuously;

    public RotateToHub(OdometryHelper odometryHelper) {
        this(odometryHelper, true);
    }

    public RotateToHub(OdometryHelper odometryHelper, boolean holdContinuously) {
        this.drivetrain = RobotContainer.getSwerveDrivetrain();
        this.odometryHelper = odometryHelper;
        this.holdContinuously = holdContinuously;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        Pose2d robotPose = odometryHelper.getPose();

        Translation2d hubTarget = odometryHelper.getHubTarget().getTranslation();
        Translation2d robotToHub = hubTarget.minus(robotPose.getTranslation());
        Rotation2d fieldAngleToHub = robotToHub.getAngle();

        double headingErrorDeg = fieldAngleToHub
            .minus(robotPose.getRotation())
            .getDegrees();

        headingErrorDeg = Math.IEEEremainder(headingErrorDeg, 360.0);

        double rotationSpeed = drivetrain.calculateChangeRotateController(headingErrorDeg);

        double maxSpeed = org.mort11.configs.constants.TunerConstants.kSpeedAt12Volts
            .in(edu.wpi.first.units.Units.MetersPerSecond);

        double xSpeed = -RobotContainer.getDriverController().getLeftY() * maxSpeed;
        double ySpeed = -RobotContainer.getDriverController().getLeftX() * maxSpeed;

        drivetrain.setDrive(new ChassisSpeeds(xSpeed, ySpeed, -rotationSpeed));

        SmartDashboard.putNumber("Heading Error to Hub (deg)", headingErrorDeg);
        SmartDashboard.putNumber("Field Angle to Hub (deg)", fieldAngleToHub.getDegrees());
        SmartDashboard.putNumber("Rotation Speed Output", rotationSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.setDrive(new ChassisSpeeds(0, 0, 0));
    }

    @Override
    public boolean isFinished() {
        if (holdContinuously) return false;

        Pose2d robotPose = odometryHelper.getPose();
        Translation2d hubTarget = odometryHelper.getHubTarget().getTranslation();
        Translation2d robotToHub = hubTarget.minus(robotPose.getTranslation());
        Rotation2d fieldAngleToHub = robotToHub.getAngle();

        double headingErrorDeg = Math.IEEEremainder(
            fieldAngleToHub.minus(robotPose.getRotation()).getDegrees(),
            360.0
        );

        return Math.abs(headingErrorDeg) < 2.0;
    }
}
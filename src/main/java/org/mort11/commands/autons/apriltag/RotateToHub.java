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

  
    private static final double kP             = 0.3;  
    private static final double kD             = 0.001; 
    private static final double MAX_ROT_SPEED  = 7.0;   
    private static final double DEADBAND_DEG   = 1.5;  

    private double previousError = 0.0;

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
    public void initialize() {
        previousError = 0.0;
    }

    @Override
    public void execute() {
        Pose2d robotPose = odometryHelper.getPose();

        Translation2d hubTarget = odometryHelper.getHubTarget().getTranslation();
        Translation2d robotToHub = hubTarget.minus(robotPose.getTranslation());
        Rotation2d fieldAngleToHub = robotToHub.getAngle();

        double headingErrorDeg = Math.IEEEremainder(
            fieldAngleToHub.minus(robotPose.getRotation()).getDegrees(),
            360.0
        );

        double rotationSpeed;
        if (Math.abs(headingErrorDeg) < DEADBAND_DEG) {
            rotationSpeed = 0.0;
            previousError = headingErrorDeg;
        } else {
            double derivative = headingErrorDeg - previousError;
            rotationSpeed = (kP * headingErrorDeg) + (kD * derivative);
            rotationSpeed = Math.max(-MAX_ROT_SPEED, Math.min(MAX_ROT_SPEED, rotationSpeed));
            previousError = headingErrorDeg;
        }

        double maxSpeed = org.mort11.configs.constants.TunerConstants.kSpeedAt12Volts
            .in(edu.wpi.first.units.Units.MetersPerSecond);

        double xSpeed = -RobotContainer.getDriverController().getLeftY() * maxSpeed;
        double ySpeed = -RobotContainer.getDriverController().getLeftX() * maxSpeed;

        drivetrain.setDrive(new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed));

        SmartDashboard.putNumber("Heading Error to Hub (deg)", headingErrorDeg);
        SmartDashboard.putNumber("Field Angle to Hub (deg)", fieldAngleToHub.getDegrees());
        SmartDashboard.putNumber("Rotation Speed Output", rotationSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        previousError = 0.0;
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
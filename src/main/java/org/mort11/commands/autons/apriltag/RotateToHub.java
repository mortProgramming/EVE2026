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

    public RotateToHub(OdometryHelper odometryHelper) {
        this.drivetrain = RobotContainer.getSwerveDrivetrain();
        this.odometryHelper = odometryHelper;

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

        drivetrain.setDrive(new ChassisSpeeds(0, 0, -rotationSpeed));

        SmartDashboard.putNumber("Heading Error to Hub (deg)", headingErrorDeg);
        SmartDashboard.putNumber("Field Angle to Hub (deg)", fieldAngleToHub.getDegrees());
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.setDrive(new ChassisSpeeds(0, 0, 0)); 
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
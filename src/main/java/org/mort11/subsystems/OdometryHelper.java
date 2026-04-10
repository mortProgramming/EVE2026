package org.mort11.subsystems;

import static org.mort11.configs.constants.PhysicalConstants.Field.BLUE_HUB_X;
import static org.mort11.configs.constants.PhysicalConstants.Field.BLUE_HUB_Y;
import static org.mort11.configs.constants.PhysicalConstants.Field.RED_HUB_X;
import static org.mort11.configs.constants.PhysicalConstants.Field.RED_HUB_Y;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class OdometryHelper extends SubsystemBase {
    private static OdometryHelper instance;

    private final CommandSwerveDrivetrain drivetrain;
    private final Field2d field;
    private final Limelight limelightFront;
    private final Limelight limelightBack;

    private final Translation2d Redhub = new Translation2d(RED_HUB_X, RED_HUB_Y);
    private final Translation2d Bluehub = new Translation2d(BLUE_HUB_X, BLUE_HUB_Y);

    public OdometryHelper(CommandSwerveDrivetrain drivetrain, Limelight limelightFront, Limelight limelightBack) {
        this.drivetrain = drivetrain;
        this.limelightFront = limelightFront;
        this.limelightBack = limelightBack;
        this.field = new Field2d();

        SmartDashboard.putData("Field", field);
    }

    @Override
    public void periodic() {
        setFieldObj();

        Vision.updateRobotOrientation(drivetrain);

        Pose2d robotPose = drivetrain.getState().Pose;

        Optional<Limelight.Measurement> frontMeasurement = limelightFront.getMeasurement(robotPose);

        if (frontMeasurement.isPresent()) {
            Limelight.Measurement m = frontMeasurement.get();
            drivetrain.addVisionMeasurement(
                m.poseEstimate.pose,
                m.poseEstimate.timestampSeconds,
                m.standardDeviations
            );
            SmartDashboard.putString("Active Limelight", "Front");
        // } else {
        //     limelightBack.getMeasurement(robotPose).ifPresent(m -> {
        //         drivetrain.addVisionMeasurement(
        //             m.poseEstimate.pose,
        //             m.poseEstimate.timestampSeconds,
        //             m.standardDeviations
        //         );
        //     });
        //     SmartDashboard.putString("Active Limelight", "Back (fallback)");
        // }

        SmartDashboard.putNumber("Robot X", robotPose.getX());
        SmartDashboard.putNumber("Robot Y", robotPose.getY());
        SmartDashboard.putNumber("Robot Heading", robotPose.getRotation().getDegrees());

        field.setRobotPose(robotPose);

        SmartDashboard.putNumber("Distance To Target", getDistanceToTarget());
        SmartDashboard.putNumber("Distance from hub", getDistanceToHub());
    }
}

    public double getDistanceToTarget() {
        Pose2d pose = drivetrain.getState().Pose;
        if (isBlue()) {
            return pose.getTranslation().getDistance(Bluehub);
        } else {
            return pose.getTranslation().getDistance(Redhub);
        }
    }

    public Pose2d getPose() {
        return drivetrain.getState().Pose;
    }

    public static Boolean isBlue() {
        return DriverStation.getAlliance().isPresent()
            ? DriverStation.getAlliance().get() == Alliance.Blue
            : true;
    }

    public Pose2d getHubTarget() {
        if (isBlue()) {
            return new Pose2d(BLUE_HUB_X, BLUE_HUB_Y, new Rotation2d());
        } else {
            return new Pose2d(RED_HUB_X, RED_HUB_Y, new Rotation2d());
        }
    }

    public double getDistanceToHub() {
        Pose2d pose = drivetrain.getState().Pose;
        if (isBlue()) {
            return pose.getTranslation().getDistance(Bluehub);
        } else {
            return pose.getTranslation().getDistance(Redhub);
        }
    }

    public void setFieldObj() {
        FieldObject2d redHub = field.getObject("Red Hub");
        redHub.setPose(new Pose2d(RED_HUB_X, RED_HUB_Y, new Rotation2d()));

        FieldObject2d blueHub = field.getObject("Blue Hub");
        blueHub.setPose(new Pose2d(BLUE_HUB_X, BLUE_HUB_Y, new Rotation2d()));
    }
}
package org.mort11.subsystems;

import java.util.Optional;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.mort11.LimelightHelpers;
import org.mort11.LimelightHelpers.PoseEstimate;

public class Limelight extends SubsystemBase {
    private final String name;
    private final NetworkTable telemetryTable;
    private final StructPublisher<Pose2d> posePublisher;

    public Limelight(String name) {
        this.name = name;
        this.telemetryTable = NetworkTableInstance.getDefault().getTable("SmartDashboard/" + name);
        this.posePublisher = telemetryTable.getStructTopic("Estimated Robot Pose", Pose2d.struct).publish();
    }

    public Optional<Measurement> getMeasurement(Pose2d currentRobotPose) {
        LimelightHelpers.SetRobotOrientation(name, currentRobotPose.getRotation().getDegrees(), 0, 0, 0, 0, 0);

        final PoseEstimate poseEstimate_MegaTag1 = LimelightHelpers.getBotPoseEstimate_wpiBlue(name);
        final PoseEstimate poseEstimate_MegaTag2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(name);

        if (
            poseEstimate_MegaTag1 == null
                || poseEstimate_MegaTag2 == null
                || poseEstimate_MegaTag1.tagCount == 0
                || poseEstimate_MegaTag2.tagCount == 0
        ) {
            return Optional.empty();
        }

        final boolean isAuton = DriverStation.isAutonomous();

        final PoseEstimate primaryEstimate;
        final Matrix<N3, N1> standardDeviations;

        if (isAuton) {
            // During auton, use MegaTag1 (full 6DOF solve) since MegaTag2's
            // gyro-assisted translation is unreliable until the gyro is well-calibrated.
            // Rotation std dev is set very high so the estimator ignores vision rotation
            // and trusts the gyro exclusively.
            primaryEstimate = poseEstimate_MegaTag1;
            standardDeviations = VecBuilder.fill(0.5, 0.5, 9999.0);
        } else {
            // During teleop, combine the stable translation from MegaTag2 with
            // the rotation from MegaTag1 to counteract gyro drift.
            poseEstimate_MegaTag2.pose = new Pose2d(
                poseEstimate_MegaTag2.pose.getTranslation(),
                poseEstimate_MegaTag1.pose.getRotation()
            );
            primaryEstimate = poseEstimate_MegaTag2;
            standardDeviations = VecBuilder.fill(0.1, 0.1, 10.0);
        }

        posePublisher.set(primaryEstimate.pose);

        return Optional.of(new Measurement(primaryEstimate, standardDeviations));
    }

    public static class Measurement {
        public final PoseEstimate poseEstimate;
        public final Matrix<N3, N1> standardDeviations;

        public Measurement(PoseEstimate poseEstimate, Matrix<N3, N1> standardDeviations) {
            this.poseEstimate = poseEstimate;
            this.standardDeviations = standardDeviations;
        }
    }
}
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
        telemetryTable.getEntry("capture_rewind").setDoubleArray(new double[] {0,160});
    }
    public void startNewRewind(){
        telemetryTable.getEntry("captured_rewind").setDoubleArray(new double[] {telemetryTable.getEntry("captured_rewind").getDoubleArray(new double[2])[0]+1,160});
        //To explain what the fuck this line does, essentially its a long, complicated way of incrementing a value in an array by one and feeding back into the captured rewind key.
        telemetryTable.getEntry("rewind_enable_set").setDouble(1);
    }

    public Optional<Measurement> getMeasurement(Pose2d currentRobotPose) {
        LimelightHelpers.SetRobotOrientation(
            name, currentRobotPose.getRotation().getDegrees(), 0, 0, 0, 0, 0
        );

        final PoseEstimate mt1 = LimelightHelpers.getBotPoseEstimate_wpiBlue(name);
        final PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(name);

        if (mt1 == null || mt2 == null || mt1.tagCount == 0 || mt2.tagCount == 0) {
            return Optional.empty();
        }

        // Reject noisy single-tag solves with high ambiguity
        if (mt1.tagCount == 1 && mt1.rawFiducials.length > 0
                && mt1.rawFiducials[0].ambiguity > 0.7) {
            return Optional.empty();
        }

        // Reject measurements from very far away (meters)
        if (mt1.avgTagDist > 4.0) {
            return Optional.empty();
        }

        final PoseEstimate primaryEstimate;
        final Matrix<N3, N1> stdDevs;

        if (DriverStation.isAutonomous()) {
            // During auton, use MegaTag1 (full 6DOF solve).
            // Rotation std dev is set very high so the estimator ignores vision
            // rotation and trusts the gyro exclusively.
            primaryEstimate = mt1;
            stdDevs = VecBuilder.fill(0.5, 0.5, 9999.0);
        } else {
            // During teleop, use MegaTag2 which uses the gyro for rotation.
            // Do NOT override the rotation with MegaTag1 — that introduces noise.
            // High rotation std dev tells the estimator to keep trusting the gyro.
            primaryEstimate = mt2;
            stdDevs = VecBuilder.fill(0.7, 0.7, 9999.0);
        }

        posePublisher.set(primaryEstimate.pose);
        return Optional.of(new Measurement(primaryEstimate, stdDevs));
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
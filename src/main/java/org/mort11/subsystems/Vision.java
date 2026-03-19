package org.mort11.subsystems;

import static org.mort11.configs.constants.VisionConstants.FRONT_CAMERA_NAME;

import org.mort11.LimelightHelpers;
import org.opencv.dnn.Net;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {
    private static Vision instance;

    private HttpCamera limelightOneFeed;
    private HttpCamera limelightTwoFeed;
    private HttpCamera limelightThreeFeed;
    private HttpCamera limelightFourFeed;

    private AprilTagFieldLayout fieldLayout;

    private NetworkTable cameraTableOne;
    private NetworkTable cameraTableTwo;
    private NetworkTable cameraTableThree;
    private NetworkTable cameraTableFour;

    // Schmitt trigger state and thresholds for thermal throttling
    // private static final double LIMELIGHT_THROTTLE_ON_TEMP_C  = 60.0; // °C: throttle kicks in above this
    // private static final double LIMELIGHT_THROTTLE_OFF_TEMP_C = 45.0; // °C: throttle removed below this (hysteresis)
    public static final int    LIMELIGHT_THROTTLE_VALUE      = 100;  // Numer of frames to skip (Value Range: 100 to 200. 200 means 100% throttling, 100 means 50% throttling)
    // private final java.util.Map<String, Boolean> limelightThrottleState = new java.util.HashMap<>();

    private static final String LL3_NAME = "limelight-three";

    private static final String[] LIMELIGHTS = {
        "limelight-one",
        "limelight-two",
        "limelight-three",
        "limelight-four"
    };

    public static String[] getLimelights() {
        return LIMELIGHTS;
    }

    public Vision() {
        fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltAndymark);

        cameraTableOne   = NetworkTableInstance.getDefault().getTable("limelight-one");
        cameraTableTwo   = NetworkTableInstance.getDefault().getTable("limelight-two");
        cameraTableThree = NetworkTableInstance.getDefault().getTable("limelight-three");
        cameraTableFour  = NetworkTableInstance.getDefault().getTable("limelight-four");

        limelightOneFeed   = new HttpCamera("limelight-one",   "http://limelight-one.local:5800/stream.mjpeg");
        limelightTwoFeed   = new HttpCamera("limelight-two",   "http://limelight-two.local:5800/stream.mjpeg");
        limelightThreeFeed = new HttpCamera("limelight-three", "http://limelight-three.local:5800/stream.mjpeg");
        limelightFourFeed  = new HttpCamera("limelight-four",  "http://limelight-four.local:5800/stream.mjpeg");

        CameraServer.addCamera(limelightOneFeed);
        CameraServer.addCamera(limelightTwoFeed);
        CameraServer.addCamera(limelightThreeFeed);
        CameraServer.addCamera(limelightFourFeed);

        // In the Vision constructor, after camera setup:
        // Forward, side, up in meters (converted from inches).
        LimelightHelpers.setCameraPose_RobotSpace(
            "limelight-three",
            -3.0 * 0.0254,   // forward — (measured about -3 inches)
            0.0  * 0.0254,   // side — (should be at 0 inches)
            25.0 * 0.0254,   // up — (about 25 inches off the floor)
            0.0,             // roll degrees: (should be 0)
            -16.0,               // pitch degrees: (rotating up) ==> negative rotation around Y-axis). Measured about -16 degrees from onboard IMU (http://limelight-three.local:5801/)
            0.0              // yaw degrees: (should be 0)
        );
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Tag ID", getTagId());
        SmartDashboard.putNumber("X Degrees", getTX());
        SmartDashboard.putBoolean("Tag Detected?", hasTag());
        
        // Only push telemetry as "not throttled" when the robot is actually enabled.
        // When disabled, disabledPeriodic() handles this with throttled=true.
        // Without this guard, periodic() and disabledPeriodic() fight each other
        // every loop cycle causing the Throttled boolean to flip rapidly.
        if (edu.wpi.first.wpilibj.DriverStation.isEnabled()) {
            updateLimelightTelemetry(false);
        }
    }

    // ---------- Camera / Limelight Methods ----------

    public boolean hasTag() {
        return cameraTableOne.getEntry("tv").getDouble(0) == 1
            || cameraTableTwo.getEntry("tv").getDouble(0) == 1
            || cameraTableThree.getEntry("tv").getDouble(0) == 1
            || cameraTableFour.getEntry("tv").getDouble(0) == 1;
    }

    public int getTagId() {
        if (cameraTableOne.getEntry("tv").getDouble(0) == 1) {
            return (int) cameraTableOne.getEntry("tid").getInteger(-1);
        } else if (cameraTableTwo.getEntry("tv").getDouble(0) == 1) {
            return (int) cameraTableTwo.getEntry("tid").getInteger(-1);
        } else if (cameraTableThree.getEntry("tv").getDouble(0) == 1) {
            return (int) cameraTableThree.getEntry("tid").getInteger(-1);
        } else if (cameraTableFour.getEntry("tv").getDouble(0) == 1) {
            return (int) cameraTableFour.getEntry("tid").getInteger(-1);
        }
        return -1;
    }

    public double getTX() {
        if (cameraTableOne.getEntry("tv").getDouble(0) == 1) {
            return cameraTableOne.getEntry("tx").getDouble(0);
        } else if (cameraTableTwo.getEntry("tv").getDouble(0) == 1) {
            return cameraTableTwo.getEntry("tx").getDouble(0);
        } else if (cameraTableThree.getEntry("tv").getDouble(0) == 1) {
            return cameraTableThree.getEntry("tx").getDouble(0);
        } else if (cameraTableFour.getEntry("tv").getDouble(0) == 1) {
            return cameraTableFour.getEntry("tx").getDouble(0);
        }
        return 0;
    }

    public double getTY() {
        if (cameraTableOne.getEntry("tv").getDouble(0) == 1) {
            return cameraTableOne.getEntry("ty").getDouble(0);
        } else if (cameraTableTwo.getEntry("tv").getDouble(0) == 1) {
            return cameraTableTwo.getEntry("ty").getDouble(0);
        } else if (cameraTableThree.getEntry("tv").getDouble(0) == 1) {
            return cameraTableThree.getEntry("ty").getDouble(0);
        } else if (cameraTableFour.getEntry("tv").getDouble(0) == 1) {
            return cameraTableFour.getEntry("ty").getDouble(0);
        }
        return 0;
    }

    public double getTA() {
        if (cameraTableOne.getEntry("tv").getDouble(0) == 1) {
            return cameraTableOne.getEntry("ta").getDouble(0);
        } else if (cameraTableTwo.getEntry("tv").getDouble(0) == 1) {
            return cameraTableTwo.getEntry("ta").getDouble(0);
        } else if (cameraTableThree.getEntry("tv").getDouble(0) == 1) {
            return cameraTableThree.getEntry("ta").getDouble(0);
        } else if (cameraTableFour.getEntry("tv").getDouble(0) == 1) {
            return cameraTableFour.getEntry("ta").getDouble(0);
        }
        return 0;
    }

    public double[] getCameraPosition() {
        return new double[] {
            getTX(),
            getTY(),
            getTA()
        };
    }

    private NetworkTable getFirstActiveTable() {
        if (cameraTableOne.getEntry("tv").getDouble(0) == 1)   return cameraTableOne;
        if (cameraTableTwo.getEntry("tv").getDouble(0) == 1)   return cameraTableTwo;
        if (cameraTableThree.getEntry("tv").getDouble(0) == 1) return cameraTableThree;
        if (cameraTableFour.getEntry("tv").getDouble(0) == 1)  return cameraTableFour;
        return cameraTableOne; // default fallback
    }

    public Pose2d getRobotPosition() {
        NetworkTable table = getFirstActiveTable();
        double[] poseNums = table.getEntry("botpose_orb_wpiblue").getDoubleArray(new double[6]);
        return new Pose2d(
            poseNums[0],
            poseNums[1],
            new Rotation2d(Math.toRadians(poseNums[4]))
        );
    }

    public Pose2d getRelativeRobotPosition() {
        NetworkTable table = getFirstActiveTable();
        double[] poseNums = table.getEntry("camerapose_targetspace").getDoubleArray(new double[6]);
        return new Pose2d(
            poseNums[0],
            poseNums[2],
            new Rotation2d(Math.toRadians(poseNums[4]))
        );
    }

    public Pose3d get3dRobotPosition() {
        NetworkTable table = getFirstActiveTable();
        double[] poseNums = table.getEntry("botpose_orb_wpiblue").getDoubleArray(new double[6]);
        return new Pose3d(
            new Translation3d(poseNums[0], poseNums[1], poseNums[2]),
            new Rotation3d(
                Math.toRadians(poseNums[3]),
                Math.toRadians(poseNums[4]),
                Math.toRadians(poseNums[5])
            )
        );
    }

    public Pose2d getFieldTagPose(int tagId) {
        return fieldLayout.getTagPose(tagId).get().toPose2d();
    }

    public void setLEDMode(int mode) {
        cameraTableOne.getEntry("ledMode").setNumber(mode);
        cameraTableTwo.getEntry("ledMode").setNumber(mode);
        cameraTableThree.getEntry("ledMode").setNumber(mode);
        cameraTableFour.getEntry("ledMode").setNumber(mode);
    }

    public void setRobotOrientation(double yaw, double yawRate) {
        double[] orientation = {yaw, yawRate, 0, 0, 0, 0};
        cameraTableOne.getEntry("robot_orientation_set").setDoubleArray(orientation);
        cameraTableTwo.getEntry("robot_orientation_set").setDoubleArray(orientation);
        cameraTableThree.getEntry("robot_orientation_set").setDoubleArray(orientation);
        cameraTableFour.getEntry("robot_orientation_set").setDoubleArray(orientation);
    }

    public double[] getPicturePosition() {
        return new double[]{0.0, 0.0, 0.0};
    }

    // ---------- End Camera / Limelight Methods ----------

    // ---------- Limelight Throttle Methods ----------

    /**
     * Publishes hardware telemetry (FPS, CPU temperature, RAM usage, board temperature)
     * for every Limelight to SmartDashboard.
     * Also publishes a per-camera "Throttled" boolean.
     * Called from disabledPeriodic() so values update even while the robot is disabled.
     *
     * @param throttled pass true when the robot is disabled (cameras are being throttled),
     *                  false when the robot is enabled (cameras are at full speed).
     */
    public void updateLimelightTelemetry(boolean throttled) {
        for (String name : LIMELIGHTS) {
            // Read all hardware stats at once using the HWData struct in LimelightHelpers
            LimelightHelpers.HWData hw = LimelightHelpers.getHWData(name);

            // Push telemetry — these update SmartDashboard even while the robot is disabled
            // because disabledPeriodic() calls this method every cycle
            SmartDashboard.putNumber( name + " FPS",          hw.fps);
            SmartDashboard.putNumber( name + " CPU Temp (C)", hw.cpuTempC);
            SmartDashboard.putNumber( name + " RAM (%)",      hw.ramUsagePct);
            SmartDashboard.putNumber( name + " Temp (C)",     hw.tempC);
            SmartDashboard.putBoolean(name + " Throttled",    throttled);

            // Actually send the throttle command to the camera
            LimelightHelpers.SetThrottle(name, throttled ? LIMELIGHT_THROTTLE_VALUE : 0);

            // Set the Limelight green LEDs to show throttle status visually:
            //   Throttled (disabled)  → Force Blink  — steady uniform blink, "I am throttled"
            //   Not throttled (enabled) → Pipeline Control — normal detection behavior
            if (throttled) {
                // LimelightHelpers.setLEDMode_ForceBlink(name);
                LimelightHelpers.setLEDMode_ForceOn(name);
            } else {
                LimelightHelpers.setLEDMode_PipelineControl(name);
            }
        }
    }

    // ---------- End Limelight Throttle Methods ----------

    // ---------------- MEGATAG2 SUPPORT ----------------

    public static class VisionMeasurement {
        public Pose2d pose;
        public double timestamp;
        public int tagCount;
        public double avgTagDist;
    }

    public static VisionMeasurement getMeasurement(String limelightName) {
        var estimate =
            LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);

        if (estimate == null)
            return null;

        VisionMeasurement vm = new VisionMeasurement();
        vm.pose = estimate.pose;
        vm.timestamp = estimate.timestampSeconds;
        vm.tagCount = estimate.tagCount;
        vm.avgTagDist = estimate.avgTagDist;

        return vm;
    }

    public static void updateRobotOrientation(CommandSwerveDrivetrain drivetrain) {
        double yaw = drivetrain.getPose().getRotation().getDegrees();
        double yawRate = Math.toDegrees(drivetrain.getRobotRelativeSpeeds().omegaRadiansPerSecond);

        for (String name : LIMELIGHTS) {
            LimelightHelpers.SetRobotOrientation(name, yaw, yawRate, 0.0, 0.0, 0.0, 0.0);
        }
    }

    /**
     * Reads the onboard IMU (Inertial Measurement Unit) from each camera and publishes
     * pitch, roll, yaw, and the accelerometer-derived mount pitch angle to SmartDashboard.
     *
     * Mount pitch is computed from the accelerometer using gravity as a reference,
     * so it is valid even when the robot is not leveled — as long as the robot is stationary.
     *
     * Positive mount pitch = camera lens tilted upward (verify sign on your bench).
     */
    // private void updateCameraIMUTelemetry() {
    //     for (String name : cameraNames) {
    //         LimelightHelpers.IMUData imu = LimelightHelpers.getIMUData(name);

    //         // Raw IMU angles reported by the Limelight firmware
    //         SmartDashboard.putNumber(name + " IMU Pitch (deg)", imu.Pitch);
    //         SmartDashboard.putNumber(name + " IMU Roll (deg)",  imu.Roll);
    //         SmartDashboard.putNumber(name + " IMU Yaw (deg)",   imu.Yaw);

    //         // Accelerometer-derived mount pitch — valid while stationary.
    //         // Uses gravity vector to compute tilt of the camera's lens axis from horizontal.
    //         double ax = imu.accelX;
    //         double ay = imu.accelY;
    //         double az = imu.accelZ;

    //         double mountPitchDeg = 0.0;
    //         if (ax != 0.0 || ay != 0.0 || az != 0.0) { // guard against all-zeros (disconnected)
    //             mountPitchDeg = Math.toDegrees(Math.atan2(-ax, Math.sqrt(ay * ay + az * az)));
    //         }

    //         SmartDashboard.putNumber(name + " Mount Pitch (deg)", mountPitchDeg);
    //     }
    // }

    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }
}
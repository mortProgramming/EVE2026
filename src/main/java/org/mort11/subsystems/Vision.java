package org.mort11.subsystems;

import static org.mort11.configs.constants.VisionConstants.FRONT_CAMERA_NAME;

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
    private AprilTagFieldLayout fieldLayout;
    private NetworkTable cameraTable;

    private static final String[] LIMELIGHTS = {
        "limelight-one",
        "limelight-two"
    };

    public static String[] getLimelights() {
        return LIMELIGHTS;
    }

    public Vision() {
        fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);
        cameraTable = NetworkTableInstance.getDefault().getTable(FRONT_CAMERA_NAME);

        limelightOneFeed = new HttpCamera("limelight-one", "http://limelight-one.local:5800/stream.mjpeg");
        limelightTwoFeed = new HttpCamera("limelight-two", "http://limelight-two.local:5800/stream.mjpeg");

        CameraServer.addCamera(limelightOneFeed);
        CameraServer.addCamera(limelightTwoFeed);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Tag ID", getTagId());
        SmartDashboard.putNumber("X Degrees", getTX());
        SmartDashboard.putBoolean("Tag Detected?", hasTag());
    }

    // ---------- Camera / Limelight Methods ----------

    public boolean hasTag() {
        return cameraTable.getEntry("tv").getDouble(0) == 1;
    }

    public int getTagId() {
        return hasTag() ? (int) cameraTable.getEntry("tid").getInteger(-1) : -1;
    }

    public double getTX() {
        return cameraTable.getEntry("tx").getDouble(0);
    }

    public double getTY() {
        return cameraTable.getEntry("ty").getDouble(0);
    }

    public double getTA() {
        return cameraTable.getEntry("ta").getDouble(0);
    }

    public double[] getCameraPosition() {
        return new double[] {
            getTX(),
            getTY(),
            getTA()
        };
    }

    public Pose2d getRobotPosition() {
        double[] poseNums = cameraTable.getEntry("botpose_orb_wpiblue").getDoubleArray(new double[6]);
        return new Pose2d(
            poseNums[0],
            poseNums[1],
            new Rotation2d(Math.toRadians(poseNums[4]))
        );
    }

    public Pose2d getRelativeRobotPosition() {
        double[] poseNums = cameraTable.getEntry("camerapose_targetspace").getDoubleArray(new double[6]);
        return new Pose2d(
            poseNums[0],
            poseNums[2],
            new Rotation2d(Math.toRadians(poseNums[4]))
        );
    }

    public Pose3d get3dRobotPosition() {
        double[] poseNums = cameraTable.getEntry("botpose_orb_wpiblue").getDoubleArray(new double[6]);
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
        cameraTable.getEntry("ledMode").setNumber(mode);
    }

    public void setRobotOrientation(double yaw, double yawRate) {
        double[] orientation = {yaw, yawRate, 0, 0, 0, 0};
        cameraTable.getEntry("robot_orientation_set").setDoubleArray(orientation);
    }

    public double[] getPicturePosition() {
        return new double[]{0.0, 0.0, 0.0};
    }

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

    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }
}
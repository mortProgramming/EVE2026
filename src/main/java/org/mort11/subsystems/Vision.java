package org.mort11.subsystems;

import static org.mort11.configs.constants.VisionConstants.FRONT_CAMERA_NAME;

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
        fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);

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
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Tag ID", getTagId());
        SmartDashboard.putNumber("X Degrees", getTX());
        SmartDashboard.putBoolean("Tag Detected?", hasTag());
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

    // ---------------- MEGATAG2 SUPPORT ----------------

    public static class VisionMeasurement {
        public Pose2d pose;
        public double timestamp;
        public int tagCount;
        public double avgTagDist;
    }

    public static VisionMeasurement getMeasurement(String limelightName) {
        var estimate = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);

        if (!OdometryHelper.isBlue()){
            estimate = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);
        }

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
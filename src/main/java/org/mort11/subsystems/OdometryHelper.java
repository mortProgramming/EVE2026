package org.mort11.subsystems;

import static org.mort11.configs.constants.PhysicalConstants.Field.BLUE_HUB_X;
import static org.mort11.configs.constants.PhysicalConstants.Field.BLUE_HUB_Y;
import static org.mort11.configs.constants.PhysicalConstants.Field.RED_HUB_X;
import static org.mort11.configs.constants.PhysicalConstants.Field.RED_HUB_Y;
import static org.mort11.configs.constants.PhysicalConstants.Turret.TURRET_MAX_ANGLE;
import static org.mort11.configs.constants.PhysicalConstants.Turret.TURRET_MIN_ANGLE;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
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

    
    // change to hub pose i
    private final Translation2d Redhub = new Translation2d(RED_HUB_X, RED_HUB_Y);
    private final Translation2d Bluehub = new Translation2d(BLUE_HUB_X, BLUE_HUB_Y);


    public OdometryHelper(CommandSwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        this.field = new Field2d();

        SmartDashboard.putData("Field", field);
    }
    
    // Can replace methods in here for camera table ones
    @Override
    public void periodic() {
        setFieldObj();

        Vision.updateRobotOrientation(drivetrain);

        // Add vision data from ALL cameras
        //addVisionMeasurements();

        // Get fused pose (AFTER vision updates)
        Pose2d robotPose = drivetrain.getState().Pose;
        
        // Dashboard output
        SmartDashboard.putNumber("Robot X", robotPose.getX());
        SmartDashboard.putNumber("Robot Y", robotPose.getY());
        SmartDashboard.putNumber("Robot Heading",
            robotPose.getRotation().getDegrees());

        field.setRobotPose(robotPose);

        // Distance to target
        SmartDashboard.putNumber(
            "Distance To Target",
            getDistanceToTarget()
        );
        SmartDashboard.putNumber("Distance from hub", getDistanceToHub());
    }

    private void addVisionMeasurements() {
        for (String name : Vision.getLimelights()) {

            Vision.VisionMeasurement vm = Vision.getMeasurement(name);

            if (vm == null) continue;

            // Must see at least 1 tag
            if (vm.tagCount == 0) continue;

            // Reject bad measurements
            if (vm.avgTagDist > 6.0) continue;

            Matrix<N3, N1> stdDevs =
                edu.wpi.first.math.VecBuilder.fill(
                    0.5 + vm.avgTagDist * 0.2,  // X
                    0.5 + vm.avgTagDist * 0.2,  // Y
                    999999                     // ignore rotation
                );

            drivetrain.addVisionMeasurement(
                vm.pose,
                vm.timestamp,
                stdDevs
            );

            // Debug per camera
            SmartDashboard.putBoolean(name + " Has Tag", vm.tagCount > 0);
            SmartDashboard.putNumber(name + " X", vm.pose.getX());
            SmartDashboard.putNumber(name + " Y", vm.pose.getY());
            SmartDashboard.putNumber(name + " Dist", vm.avgTagDist);
            SmartDashboard.putNumber(name + " Tags", vm.tagCount);
        }
    }

    // Distance calculation 
    public double getDistanceToTarget() {
        Pose2d pose = drivetrain.getState().Pose;
        return pose.getTranslation().getDistance(Redhub);
    }
    
    public Pose2d getPose() {
        return drivetrain.getState().Pose;
    }

    public static Boolean isBlue() {
		return DriverStation.getAlliance().isPresent() ? DriverStation.getAlliance().get() == Alliance.Blue : true;
	}

    public Pose2d getHubTarget() {
        if (isBlue()) {
            return new Pose2d(BLUE_HUB_X, BLUE_HUB_Y, new Rotation2d());
        } else {
            return new Pose2d(RED_HUB_X, RED_HUB_Y, new Rotation2d());
        }
    }

    // public double getAngleForTurretDeg() {
    //     Pose2d pose = drivetrain.getState().Pose;
        
    //     if (drivetrain == null) {
    //         System.out.println("DRIVETRAIN IS NULL");
    //     }
        
    //     Translation2d target = getHubTarget().getTranslation();

    //     Translation2d robotToTarget = target.minus(pose.getTranslation());

    //     Rotation2d angleToTarget = robotToTarget.getAngle();

    //     Rotation2d turretAngle = angleToTarget.minus(pose.getRotation());

    //     double angle = Math.IEEEremainder(turretAngle.getDegrees(), 360.0);

    //     //Clamped it
    //     angle = Math.max(TURRET_MIN_ANGLE, Math.min(TURRET_MAX_ANGLE, angle));

    //     return angle;
    // }

    // public boolean turretCanAim() {
    //     Pose2d pose = drivetrain.getState().Pose;

    //     Translation2d target = getHubTarget().getTranslation();

    //     Translation2d robotToTarget = target.minus(pose.getTranslation());

    //     Rotation2d angleToTarget = robotToTarget.getAngle();

    //     Rotation2d turretAngle = angleToTarget.minus(pose.getRotation());

    //     double angle = Math.IEEEremainder(turretAngle.getDegrees(), 360.0);

    //     return angle >= TURRET_MIN_ANGLE &&
    //         angle <= TURRET_MAX_ANGLE;
    // }

    // Make values for target pass
    // Pose2d getPassTarget(){
    // }

    public double getDistanceToHub() {
        Pose2d pose = drivetrain.getState().Pose;
        if (isBlue()) {
            return pose.getTranslation().getDistance(Bluehub);
        } else {
            return pose.getTranslation().getDistance(Redhub);
        }
    }

    // Add hub markers to the Field2d
    public void setFieldObj(){
        FieldObject2d redHub = field.getObject("Red Hub");
        redHub.setPose(new Pose2d(RED_HUB_X, RED_HUB_Y, new Rotation2d()));

        FieldObject2d blueHub = field.getObject("Blue Hub");
        blueHub.setPose(new Pose2d(BLUE_HUB_X, BLUE_HUB_Y, new Rotation2d()));
    }
//     public static OdometryHelper getInstance() {
//         if (instance == null) {
//             instance = new OdometryHelper();
//         }
//         return instance;
//     }
}
package org.mort11.configs.constants;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import static edu.wpi.first.units.Units.RPM;


public final class PhysicalConstants {

	public static final double ROBOT_VOLTAGE = 12;

	public static final class KrakenX60 {
		public static final AngularVelocity kFreeSpeed = RotationsPerSecond.of(100.0); // 6000 RPM at 12V
	}

    public static final class CommandSwerveDrivetrain {
		public static final double DRIVETRAIN_TRACKWIDTH_METERS = Units.inchesToMeters(20.50);
		public static final double DRIVETRAIN_WHEELBASE_METERS = Units.inchesToMeters(21.75);

		public static final double DRIVEBASE_RADIUS_METERS = Math.hypot(DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0);

		public static final double FRONT_LEFT_OFFSET = 21.3 + 90 + 180;
		public static final double FRONT_RIGHT_OFFSET = 3.08 + 90 + 180;
		public static final double BACK_LEFT_OFFSET = 311.75 + 90 + 180;
		public static final double BACK_RIGHT_OFFSET = 346.73 + 90 + 180;

		public static final int IMU_TO_ROBOT_FRONT_ANGLE = 270;

		public static final double WHEEL_COEFFICIENT_OF_FRICTION = 1.200;
		public static final double ROBOT_MASS = Units.lbsToKilograms(145);
		public static final double ROBOT_MOMENT_OF_INERTIA = ROBOT_MASS * Math.pow(Units.inchesToMeters(Math.hypot(26, 28)), 2) / 2;

		public static final double DRIVE_MOTOR_CURRENT_LIMIT = 60;
		public static final double DRIVE_MOTOR_MAX_RPM = 6000;

		public static final double DRIVE_REDUCTION = (16.0 / 50.0) * (28.0 / 16.0) * (15.0 / 45.0);
		public static final double WHEEL_DIAMETER = Units.inchesToMeters(4);
		public static final double ROTATIONS_TO_METERS = WHEEL_DIAMETER * Math.PI;
		public static final double MAX_SPEED = 11.71;
		public static final double ODOMETRY_MULTIPLIER = 5.67;
    }

	public static final class Feeder {
    	public static final double FEEDER_STATOR_CURRENT_LIMIT = 120;
    	public static final double FEEDER_SUPPLY_CURRENT_LIMIT = 50;
		public static final double FEED_SPEED = 0.9;
		public static final double FEED_RPM =5000;
	}

	public static final class Hood {
    	public static final double SERVO_LENGTH_MM = 100;
    	public static final double MAX_SERVO_SPEED_MM_PER_SEC = 20;
    	public static final double MIN_POSITION = 0.01;
    	public static final double MAX_POSITION = 0.77;
    	public static final double POSITION_TOLERANCE = 0.01;

    	public static final int MIN_PULSE_WIDTH_SERVO = 1000;
    	public static final int MIDDLE_PULSE_WIDTH_SERVO = 1500;
    	public static final int MAX_PULSE_WIDTH_SERVO = 2000;
    	public static final int PULSE_PERIOD_WIDTH_SERVO = 20000;
	}

	public static final class Intake {
    	public static final double ARM_STATOR_CURRENT_LIMIT = 80; 
    	public static final double ARM_SUPPLY_CURRENT_LIMIT = 30; 

		public static final double ROLLER_STATOR_CURRENT_LIMIT = 120;
		public static final double ROLLER_SUPPLY_CURRENT_LIMIT = 70;

    	public static final double PIVOT_REDUCTION = 50.0;
    	public static final double POSITION_TOLERANCE_DEG = 5.0;

    //pos in degrees
    	public static final double HOMED_DEG = 0;
    	public static final double STOWED_DEG = 5;
    	public static final double INTAKE_DEG = -68;
    	public static final double AGITATE_DEG = -10;

    //roller speed
    	public static final double INTAKE_SPEED = 1;
		public static final double OUTTAKE_SPEED = -1;
	}

    public static final class Shooter {
	    public static final double STATOR_CURRENT_LIMIT = 110;
    	public static final double SUPPLY_CURRENT_LIMIT = 50;
    	//public static final double VELOCITY_TOLERANCE_RPM = 100;
    	public static final double SHOOTER_SPEED_BUZZ_TOLERANCE = 0.05;

    	public static final double MAX_SHOOTER_RPM = 6020;
    	public static final double RPM_CHANGE_PER_SEC = 3000;
		
    }

	public static final class Field {
		public static final double BLUE_HUB_X = 4.625594;
		public static final double BLUE_HUB_Y = 4.034536;

		public static final double RED_HUB_X = 11.915394;
		public static final double RED_HUB_Y = 4.034536;

		public static final double RED_PASS_X = 0.0;
		public static final double RED_PASS_Y = 0.0;

		public static final double BLUE_PASS_X = 0.0;
		public static final double BLUE_PASS_Y = 0.0;
	}



	public static class Landmarks {
    	public static Translation2d hubPosition() {
       		if (DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red) {
            	return new Translation2d(Field.RED_HUB_X, Field.RED_HUB_Y);
       			}
        return new Translation2d(Field.BLUE_HUB_X, Field.BLUE_HUB_Y);
		}
	}
}

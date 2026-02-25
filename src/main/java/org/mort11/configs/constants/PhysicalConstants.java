package org.mort11.configs.constants;
import edu.wpi.first.math.util.Units;

public final class PhysicalConstants {

	public static final double ROBOT_VOLTAGE = 12;

    public static final class CommandSwerveDrivetrain {
        // The left-to-right distance between the drivetrain wheels measured from center
		// to center.
		public static final double DRIVETRAIN_TRACKWIDTH_METERS = Units.inchesToMeters(20.75);
		// The front-to-back distance between the drivetrain wheels measured from center
		// to center.
		public static final double DRIVETRAIN_WHEELBASE_METERS = Units.inchesToMeters(22.75);

		public static final double DRIVEBASE_RADIUS_METERS = Math.hypot(
			DRIVETRAIN_TRACKWIDTH_METERS / 2.0, DRIVETRAIN_WHEELBASE_METERS / 2.0
		);

		// public static final double FRONT_LEFT_OFFSET = 293.9;
		// public static final double FRONT_RIGHT_OFFSET = 273.1;
		// public static final double BACK_LEFT_OFFSET = 223.3;
		// public static final double BACK_RIGHT_OFFSET = 255.5;

		public static final double FRONT_LEFT_OFFSET = 21.3 + 90 + 180;
		public static final double FRONT_RIGHT_OFFSET = 3.08 + 90 + 180;
		public static final double BACK_LEFT_OFFSET = 311.75 + 90 + 180;
		public static final double BACK_RIGHT_OFFSET = 346.73 + 90 + 180;

		public static final int IMU_TO_ROBOT_FRONT_ANGLE = 270;

		public static final double WHEEL_COEFFICIENT_OF_FRICTION = 1;
		public static final double ROBOT_MASS = 47;
		public static final double ROBOT_MOMENT_OF_INERTIA = ROBOT_MASS * Math.pow(Units.inchesToMeters(Math.hypot(26, 28)), 2) / 2;
		// public static final double ROBOT_MOMENT_OF_INERTIA = 1.25;

		public static final double DRIVE_MOTOR_CURRENT_LIMIT = 60;
		public static final double DRIVE_MOTOR_MAX_RPM = 6000;

		public static final double DRIVE_REDUCTION = (16.0 / 50.0) * (28.0 / 16.0) * (15.0 / 45.0);
		public static final double WHEEL_DIAMETER = Units.inchesToMeters(4);  //0.1014
		public static final double ROTATIONS_TO_METERS = WHEEL_DIAMETER * Math.PI;
		public static final double MAX_SPEED = DRIVE_REDUCTION * ROTATIONS_TO_METERS * (DRIVE_MOTOR_MAX_RPM / 60);

		public static final double ODOMETRY_MULTIPLIER = 5.67;
		// public static final double ODOMETRY_MULTIPLIER = 5.575;
    }
	public static final class Feeder {
		public static final int FEEDER_SMART_CURRENT_LIMIT = 50;
		public static final int FEEDER_SECONDARY_CURRENT_LIMIT = 100;
    }

	public static final class Hood {
		// gear ratio * 360
		public static final double DEG_PER_ROTATION = 360 / 18;
		//The maximum inch displacement for every 0.02 sec cycle	
		public static final double MAXIMUM_DEG_CHANGE = 5;
		public static final double HOOD_DEG_OFFSET = 89.5;

		public static final double HOOD_START_HEIGHT = 80;

		public static final int MIN_PULSE_WIDTH_SERVO = 500;
		public static final int MIDDLE_PULSE_WIDTH_SERVO = 1500;
		public static final int MAX_PULSE_WIDTH_SERVO = 2500;
		public static final int PULSE_PERIOD_WIDTH_SERVO = 20000;
	}

    public static final class IntakeArmLeft {

    }

    public static final class IntakeArmRight {

    }

    public static final class IntakeRollerLeft {
		public static final int ROLLER_LEFT_SMART_CURRENT_LIMIT = 40;
    	public static final double ROLLER_LEFT_SECONDARY_CURRENT_LIMIT = 60;

    }

    public static final class IntakeRollerRight {
		public static final int ROLLER_RIGHT_SMART_CURRENT_LIMIT = 40;
    	public static final double ROLLER_RIGHT_SECONDARY_CURRENT_LIMIT = 60;
    }

	public static final class Turret {
		//speeds
		public static final double MANUAL_SPEED = 0.15;

		//positions and ratios
        public static final double MOTOR_ROTATIONS_TO_TURRET_DEG = (1.0 / 15.0) * (24.0 / 150.0) * 360;
		public static final double STARTING_POSITION_DEG = 0;

		public final static double TURRET_MIN_ANGLE = -135;
		public final static double TURRET_MAX_ANGLE = 135;
    }

    public static final class Shooter {
		public static final double SHOOTER_STATOR_CURRENT_LIMIT = 80;
		public static final double SHOOTER_SUPPLY_CURRENT_LIMIT = 35;
		public static final double MAX_SHOOTER_RPM = 7758;

		public static final double SHOOTER_SPEED_BUZZ_TOLERANCE = 0.05; //in percent of wanted RPM
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
}

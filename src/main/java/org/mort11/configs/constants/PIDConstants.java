package org.mort11.configs.constants;

import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;

public final class PIDConstants {
    public static final class CommandSwerveDrivetrain {

    }
    

    public static final class Hood {
		//PID Rotational
		public final static double ROT_KP = 0.25;
		public final static double ROT_KI = 0;
		public final static double ROT_KD = 0.01; // try 0.1, 0.15, 0.2

		public final static Constraints ROT_CONSTRAINTS = new Constraints(100, 200);

        public final static double ROT_TOLERANCE = 2.0;
        public final static double ROT_SPEED_TOLERANCE = 10.0;
    }

    public static final class Shooter {
    public static final double KP = 0.53;
    public static final double KI = 0.01;
    public static final double KD = 0.0;

    public static final double KV_LEFT   = 0.15;  
    public static final double KV_MIDDLE = 0.15;
    public static final double KV_RIGHT  = 0.15;

    public static final double KS = 0.15; 
    public static final double KA = 0.2;
    public static final double VELOCITY_TOLERANCE_RPM = 75;
    }
 
    public static final class Intake {
        public static final double KP = 300;
        public static final double KI = 0;
        public static final double KD = 0;
    }

    public static final class Feeder{
        public static final double KP = 0.2;
        public static final double KI = 0;
        public static final double KD = 0.0;

        public static final double KV = 0.12;
    }
}

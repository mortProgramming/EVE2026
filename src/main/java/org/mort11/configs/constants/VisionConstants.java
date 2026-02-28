package org.mort11.configs.constants;

public class VisionConstants {
        // Camera / Limelight Names
        public static final String FRONT_CAMERA_NAME = "limelight-one";
    
        // Field Dimensions (in meters)
        public static final double FIELD_LENGTH = 16.54;
        public static final double FIELD_WIDTH = 8.07;
    
        // AprilTag IDs and layout (optional, if you want to reference tags by ID)
        // public static final int[] APRIL_TAG_IDS = {1, 2, 3, 4, 5, 6, 7, 8};
    
        // Camera settings / limits (example)
        public static final double MAX_LED_MODE = 3; // 0=off, 1=blink, 2=on, 3=default
        public static final double MIN_LED_MODE = 0;
    
        // Optional: Camera mounting offset (from robot center) in meters
        public static final double CAMERA_X_OFFSET = 0.0;
        public static final double CAMERA_Y_OFFSET = 0.0;
        public static final double CAMERA_Z_OFFSET = 0.5; // height of camera from floor
        public static final double CAMERA_PITCH = 0.0;    // in degrees
        public static final double CAMERA_YAW = 0.0;      // in degrees
        public static final double CAMERA_ROLL = 0.0;     // in degrees
    
    }

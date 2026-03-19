package org.mort11.configs.constants;

import edu.wpi.first.math.util.Units;

public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees (straight up = 90 deg)
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 1750, 0.0},
         //lower placeholder
        // {Units.inchesToMeters(71), 2000, 73}, //found in mill
        // {Units.inchesToMeters(107), 2500, 73},
        // {Units.inchesToMeters(189), 3000, 73},
        // {Units.inchesToMeters(238), 3500, 73},
        {1, 3990, 0.20},
        {2, 4000, 0.20},
        {3, 4100, 0.22},
        {4, 4500, 0.30},
        {5, 4700, 0.35},
        {6, 5000, 0.77} //upper placholder
    };

    //distance in meters, time in air seconds
    public static final double[][] timeInAir = {
        {0, 0},
        {10, 10}
    };
}

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
        // {1, 3950, 0.19}, saturday pids
    
        // {1.5, 3990, 0.20},
        // {2, 4000, 0.20},
        // {2.5, 4050, 0.21},
        // {3, 4300, 0.20},
        // {4, 4500, 0.215},
        // {5, 4900, 0.35},
        // {6, 6000, 0.77} //upper placholder
        {1, 3200, 0.19},
        {1.5, 3300, 0.20},
        {2, 3600, 0.20},
        {2.5, 3700, 0.35},
        {3, 3800, 0.36},
        {4, 4000, 0.40},
        {5, 4200, 0.60},
        {6, 5200, 0.77} //upper placholder
    };

    //distance in meters, time in air seconds
    public static final double[][] timeInAir = {
        {0, 0},
        {1, 10},
        {2 , 10},
        {3 , 10},
        {4 , 10},
        {5, 10},
        {6, 10}
    };
}

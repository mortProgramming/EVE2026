package org.mort11.configs.constants;

import edu.wpi.first.math.util.Units;

public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees (straight up = 90 deg)
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 1750, 0.0},
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
        {1, 1.0},
        {2 , 1.1},
        {3 , 1.2},
        {4 , 1.3},
        {5, 1.4},
        {6, 1.7}
    };
}

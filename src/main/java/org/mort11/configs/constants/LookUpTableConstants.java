package org.mort11.configs.constants;


public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees (straight up = 90 deg)
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 1700, 0.0},
        {1, 3100, 0.19},
        {1.5, 3250, 0.20},
        {2, 3550, 0.20},
        {2.5, 3650, 0.35},
        {3, 3750, 0.36},
        {4, 3900, 0.40},
        {5, 4150, 0.50},
        {6, 4300, 0.65} //upper placholder
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

package org.mort11.configs.constants;


public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees (straight up = 90 deg)
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 3000, 0.0},
        {0.5, 3000, 0.19},
        {1, 3150, 0.19},
        {1.5, 3300, 0.20},
        {2, 3650, 0.20},
        {2.5, 3800, 0.35},
        {3, 3950, 0.36},
        {3.5, 4200, 0.40},
        {4, 4200, 0.40},
        {5, 4350, 0.50},
        {6, 4550, 0.65} //upper placholder
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

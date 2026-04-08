package org.mort11.configs.constants;


public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees 
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 3700 - 800, 0.0},
        {0.5, 3700 - 800, 0.19},
        {1, 3850 - 800, 0.19},
        {1.5, 4000 - 800, 0.20},
        {2, 4350 -800, 0.20},
        {2.5, 4500 - 800, 0.35},
        {3, 4650 - 800, 0.36},
        {3.5, 4850-800, 0.40},
        {4, 4900-800, 0.40},
        {5, 5050-800, 0.50},
        {6, 5250-800, 0.65} //upper placholder
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

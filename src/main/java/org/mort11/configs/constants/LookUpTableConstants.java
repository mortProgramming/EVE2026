package org.mort11.configs.constants;


public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees 
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 4000, 0.0},
        {0.5, 4000, 0.19},
        {1, 4150, 0.19},
        {1.5, 4300, 0.20},
        {2, 4650, 0.20},
        {2.5, 4800, 0.35},
        {3, 4950, 0.36},
        {3.5, 5200, 0.40},
        {4, 5200, 0.40},
        {5, 5350, 0.50},
        {6, 5550, 0.65} //upper placholder
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

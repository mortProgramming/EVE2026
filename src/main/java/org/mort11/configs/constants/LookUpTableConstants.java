package org.mort11.configs.constants;


public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees 
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {0, 2900, 0.0}, //lower placeholder 
        {0.5, 3000, 0.05},
        {1, 3000, 0.07},//up against the hub
        {1.5, 3150, 0.09},
        {2, 3350, 0.20}, 
        {2.5, 3600, 0.35},
        {3, 3830, 0.33}, // distance the robot is when it is at the trench
        {3.5, 3850, 0.33},
        {4, 4400, 0.35},
        {4.5, 4750, 0.45}, 
        {5, 4800, 0.45}, //up against the drive station
        {5.5, 4850, 0.45}, 
        {6, 4850, 0.46} //upper placholder

        //old look up table

    //      {0, 2900, 0.0},
    //     {0.5, 2980, 0.05},
    //     {1, 3100, 0.07},
    //     {1.5, 3280, 0.10},
    //     {2, 3450, 0.20},
    //     {2.5, 3600, 0.35},
    //     {3, 3700, 0.36}, // distance the robot is when it is at the trench
    //     {3.5, 3800, 0.40},
    //     {4, 4300, 0.40},
    //     {4.5, 4700, 0.40}, 
    //     {5, 4750, 0.35}, //up against
    //     {5.5, 4800, 0.35}, 
    //     {6, 4850, 0.40} //upper placholder
    // };
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

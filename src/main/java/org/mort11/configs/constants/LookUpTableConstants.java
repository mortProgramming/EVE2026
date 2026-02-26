package org.mort11.configs.constants;

import edu.wpi.first.math.util.Units;

public final class LookUpTableConstants {

    //distance in meters, shooter rpm, hood angle degrees (straight up = 90 deg)
    public static final double[][] SHOOTER_SUPERSYSTEM = {
        {Units.inchesToMeters(0), 1750, 73}, //lower placeholder
        // {Units.inchesToMeters(71), 2000, 73}, //found in mill
        // {Units.inchesToMeters(107), 2500, 73},
        // {Units.inchesToMeters(189), 3000, 73},
        // {Units.inchesToMeters(238), 3500, 73},
        {Units.inchesToMeters(80), 2150, 73},
        {Units.inchesToMeters(120), 2450, 73},
        {Units.inchesToMeters(160), 2750, 73},
        {Units.inchesToMeters(238), 3500, 73},
        {Units.inchesToMeters(720), 6000, 50} //upper placholder
    };

    //distance in meters, time in air seconds
    public static final double[][] timeInAir = {
        {0, 0},
        {10, 10}
    };
}

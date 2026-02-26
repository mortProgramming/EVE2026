package org.mort11.configs;

import static org.mort11.configs.constants.LookUpTableConstants.*;

public class LookUpTable {
    public double table[][];

    private static final LookUpTable shooterSupersystemTable = new LookUpTable(SHOOTER_SUPERSYSTEM);

    public LookUpTable(double[][] table) {
        this.table = table;
    }

    public double[] lowerBounds(double independent) {
        int currentSpot = 0;

        // for(int i = 0; independent < table[i][0]; i++) {
        //     System.out.println(independent);
        //     currentSpot = i;
        // }
        try{
            while(independent > table[currentSpot][0]) {
                currentSpot++;
            }
         } catch(Exception e){
            currentSpot=0;
        }
        if (currentSpot > 0) {
            currentSpot--;
        }
        //These ifs may not be neccesary - Adam
        if (currentSpot>=table.length){
            currentSpot=table.length-1;
        }
        return table[currentSpot];
    }

    public double[] upperBounds(double independent) {
        int currentSpot = 0;
        try{
            while(independent > table[currentSpot][0]) {
                currentSpot++;
            }
        } catch(Exception e){
            currentSpot=0;
        }
        //These ifs may not be neccesary - Adam
        if (currentSpot>=table.length){
            currentSpot=table.length-1;
        }
         if(currentSpot<0){
            currentSpot=0;
        }
        return table[currentSpot];
    }

    public double[] linearInterpolation(double independentValue) {
        double[] output = new double[table[0].length];

        double[] lowerBounds = lowerBounds(independentValue);
        double[] upperBounds = upperBounds(independentValue);

        for(int i = 0; i < table[0].length; i++) {
            output[i] = map(independentValue, lowerBounds[0], upperBounds[0], lowerBounds[i], upperBounds[i]);
        }

        return output;
    }

    public static double map(double input, double minIn, double maxIn, double minOut, double maxOut) {
        double inRange = maxIn - minIn;
        double outRange = maxOut - minOut;
        //I assume that maxOut is the speed/angle we need to set the motor to at when distance is exactly on upper bounds.
        //This if statement checks to see if the lower and upper bounds are the same, as the only time inRange or outRange would be zero is if the 2 bounds are thhe same - Adam
        if(inRange==0 && outRange==0){
            return maxOut;
        }
        
        return (((input - minIn) / inRange) * outRange) + minOut;
    }
    //
    public static double limitedMap(double input, double minIn, double maxIn, double minOut, double maxOut) {
        return clamp(minOut, map(input, minIn, maxIn, minOut, maxOut), maxOut);
    }
    /*Checks to see if data is within the max, and thne min parameters. If higher than max, sets to max. 
     * If lower than min, sets to min. Then it returns the data
     */ 
    public static double clamp(double min, double data, double max) {
        return Math.max(Math.min(data, max), min);
    }

    public static double getNeededShooterRPM(double meters) {
        return shooterSupersystemTable.linearInterpolation(meters)[1];
    }

    public static double getNeededHoodAngle(double meters) {
        return shooterSupersystemTable.linearInterpolation(meters)[2];
    }
}

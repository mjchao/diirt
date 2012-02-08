/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class RangeUtil {
    
    public static double[] ticksForRange(double min, double max, int nTicks) {
        double magnitude = Math.pow(10.0, Math.floor(Math.log10(max)));
        int ticks = countTicks(min, max, magnitude);
        if (ticks > nTicks) {
            if (ticks / 2 < nTicks) {
                int newTicks = countTicks(min, max, magnitude * 5);
                if (newTicks > 2 && newTicks <= nTicks) {
                    return createTicks(min, max, magnitude * 5);
                }
            }
            
            if (ticks / 5 < nTicks) {
                int newTicks = countTicks(min, max, magnitude * 2);
                if (newTicks > 2 && newTicks <= nTicks) {
                    return createTicks(min, max, magnitude * 2);
                }
            }
            
            return new double[] {min, max};
        } else {
            double increment = magnitude;
            // Refine if there is still space to refine
            while (countTicks(min, max, increment / 2) <= nTicks) {
                if (countTicks(min, max, increment / 10) <= nTicks) {
                    increment /= 10;
                } else if (countTicks(min, max, increment / 5) <= nTicks) {
                    return createTicks(min, max, increment / 5);
                } else {
                    return createTicks(min, max, increment / 2);
                }
            }
            return createTicks(min, max, increment);
        }
    }
    
    static int countTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        return end - start + 1;
    }
    
    static double[] createTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        double[] ticks = new double[end-start+1];
        for (int i = 0; i < ticks.length; i++) {
            ticks[i] = (i + start) * increment;
        }
        return ticks;
    }
}

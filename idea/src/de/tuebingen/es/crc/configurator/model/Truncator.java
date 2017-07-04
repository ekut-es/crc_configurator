package de.tuebingen.es.crc.configurator.model;

/**
 * Created by luebeck on 7/4/17.
 */
public class Truncator {
    public static long truncateNumber(long number, int dataWidth) {
        // generate bit mask
        long bitMask = 0;


        for(int i = 0; i < dataWidth; i++) {
            long bit = (1 << i);
            bitMask = bitMask | bit;
        }

        return (number & bitMask);
    }
}

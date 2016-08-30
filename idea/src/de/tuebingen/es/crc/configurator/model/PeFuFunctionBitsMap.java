package de.tuebingen.es.crc.configurator.model;

/**
 * Created by Konstantin (Konze) Lübeck on 30/08/16.
 */
public class PeFuFunctionBitsMap {

    public static String getBits(PE.FUFunction fuFunction) {
        switch (fuFunction) {
            case add:
                return "00000";
            case sub:
                return "00001";
            case mul:
                return "00010";
            case div:
                return "00011";
            case and:
                return "00100";
            case or:
                return "00101";
            case xor:
                return "00111";
            case shift_left:
                return "01000";
            case shift_right:
                return "01001";
            case compare_eq:
                return "01010";
            case compare_neq:
                return "01011";
            case compare_lt:
                return "01100";
            case compare_gt:
                return "01101";
            case compare_leq:
                return "01110";
            case compare_geq:
                return "01111";
            case mux_0:
                return "10000";
            case mux_1:
                return "10001";
            default:
                return "11111";
        }
    }
}

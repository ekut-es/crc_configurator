package de.tuebingen.es.crc.configurator.view;


import de.tuebingen.es.crc.configurator.model.PE;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class FuFunctionStringMap {

    public String getString(PE.FUFunction fuFunction) {

        switch (fuFunction) {
            case add:
                return "+";
            case sub:
                return "−";
            case mul:
                return "×";
            case div:
                return "÷";
            case and:
                return "AND";
            case or:
                return "OR";
            case xor:
                return "XOR";
            case not:
                return "NOT";
            case shift_left:
                return "<<";
            case shift_right:
                return ">>";
            case compare_eq:
                return "==";
            case compare_neq:
                return "!=";
            case compare_lt:
                return "<";
            case compare_gt:
                return ">";
            case compare_leq:
                return "<=";
            case compare_geq:
                return ">=";
            case mux_0:
                return "MUX 0";
            case mux_1:
                return "MUX 1";
            default:
                return "NOP";
        }
    }
}

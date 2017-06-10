package de.tuebingen.es.crc.configurator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class FU {

    public enum FuMode {
        add, sub, mul, div, and, or, xor, not, shift_left, shift_right, compare, multiplex
    };

    public enum FuFunction {
        none, add, sub, mul, div, and, or, xor, not, shift_left, shift_right, compare_eq, compare_neq, compare_lt, compare_gt, compare_leq, compare_geq, mux_0, mux_1
    }

    /*
    public static final HashMap<String, FuMode> nameToFuMode = new HashMap<String, FuMode>() {{
        put("add", FuMode.add);
        put("sub", FuMode.sub);
        put("mul", FuMode.mul);
        put("div", FuMode.div);
        put("and", FuMode.and);
        put("or", FuMode.or);
        put("xor", FuMode.xor);
        put("not", FuMode.not);
        put("shift_left", FuMode.shift_left);
        put("shift_right", FuMode.shift_right);
        put("compare", FuMode.compare);
        put("multiplex", FuMode.multiplex);
    }};
    */

    public static final HashMap<FuMode, String> fuModeToName = new HashMap<FuMode, String>() {{
        put(FuMode.add, "add");
        put(FuMode.sub, "sub");
        put(FuMode.mul, "mul");
        put(FuMode.div, "div");
        put(FuMode.and, "and");
        put(FuMode.or, "or");
        put(FuMode.xor, "xor");
        put(FuMode.not, "not");
        put(FuMode.shift_left, "shift_left");
        put(FuMode.shift_right, "shift_right");
        put(FuMode.compare, "compare");
        put(FuMode.multiplex, "multiplex");
    }};

    public static final HashMap<FuMode, HashSet<FuFunction>> fuFunctionsOfFuMode = new HashMap<FuMode, HashSet<FuFunction>>() {{
        put(FuMode.add, new HashSet<FuFunction>(){{ add(FuFunction.add); }});
        put(FuMode.sub, new HashSet<FuFunction>(){{ add(FuFunction.sub); }});
        put(FuMode.mul, new HashSet<FuFunction>(){{ add(FuFunction.mul); }});
        put(FuMode.div, new HashSet<FuFunction>(){{ add(FuFunction.div); }});
        put(FuMode.and, new HashSet<FuFunction>(){{ add(FuFunction.and); }});
        put(FuMode.or, new HashSet<FuFunction>(){{ add(FuFunction.or); }});
        put(FuMode.xor, new HashSet<FuFunction>(){{ add(FuFunction.xor); }});
        put(FuMode.not, new HashSet<FuFunction>(){{ add(FuFunction.not); }});
        put(FuMode.shift_left, new HashSet<FuFunction>(){{ add(FuFunction.shift_left); }});
        put(FuMode.shift_right, new HashSet<FuFunction>(){{ add(FuFunction.shift_right); }});
        put(FuMode.compare, new HashSet<FuFunction>(){{
            add(FuFunction.compare_eq);
            add(FuFunction.compare_neq);
            add(FuFunction.compare_lt);
            add(FuFunction.compare_gt);
            add(FuFunction.compare_leq);
            add(FuFunction.compare_geq);
        }});
        put(FuMode.multiplex, new HashSet<FuFunction>(){{
            add(FuFunction.mux_0);
            add(FuFunction.mux_1);
        }});
    }};

    public static final HashMap<FuFunction, FuMode> fuModeOfFuFunction = new HashMap<FuFunction, FuMode>() {{
        put(FuFunction.add, FuMode.add);
        put(FuFunction.sub, FuMode.sub);
        put(FuFunction.mul, FuMode.mul);
        put(FuFunction.div, FuMode.div);
        put(FuFunction.and, FuMode.and);
        put(FuFunction.or, FuMode.or);
        put(FuFunction.xor, FuMode.xor);
        put(FuFunction.not, FuMode.not);
        put(FuFunction.shift_left, FuMode.shift_left);
        put(FuFunction.shift_right, FuMode.shift_right);
        put(FuFunction.compare_eq, FuMode.compare);
        put(FuFunction.compare_neq, FuMode.compare);
        put(FuFunction.compare_lt, FuMode.compare);
        put(FuFunction.compare_gt, FuMode.compare);
        put(FuFunction.compare_leq, FuMode.compare);
        put(FuFunction.compare_geq, FuMode.compare);
        put(FuFunction.mux_0, FuMode.multiplex);
        put(FuFunction.mux_1, FuMode.multiplex);
    }};

    public static final HashMap<FuFunction, String> fuFunctionToName = new HashMap<FuFunction, String>() {{
        put(FuFunction.none, "nop");
        put(FuFunction.add, "add");
        put(FuFunction.sub, "sub");
        put(FuFunction.mul, "mul");
        put(FuFunction.div, "div");
        put(FuFunction.and, "and");
        put(FuFunction.or, "or");
        put(FuFunction.xor, "xor");
        put(FuFunction.not, "not");
        put(FuFunction.shift_left, "shift_left");
        put(FuFunction.shift_right, "shift_right");
        put(FuFunction.compare_eq, "compare_eq");
        put(FuFunction.compare_neq, "compare_neq");
        put(FuFunction.compare_lt, "compare_lt");
        put(FuFunction.compare_gt, "compare_gt");
        put(FuFunction.compare_leq, "compare_leq");
        put(FuFunction.compare_geq, "compare_geq");
        put(FuFunction.mux_0, "mux_0");
        put(FuFunction.mux_1, "mux_1");
    }};

    public static final HashMap<FuFunction, String> fuFunctionToSign = new HashMap<FuFunction, String>() {{
        put(FuFunction.none, "nop");
        put(FuFunction.add, "+");
        put(FuFunction.sub, "-");
        put(FuFunction.mul, "×");
        put(FuFunction.div, "÷");
        put(FuFunction.and, "and");
        put(FuFunction.or, "or");
        put(FuFunction.xor, "xor");
        put(FuFunction.not, "not");
        put(FuFunction.shift_left, "<<");
        put(FuFunction.shift_right, ">>");
        put(FuFunction.compare_eq, "==");
        put(FuFunction.compare_neq, "!=");
        put(FuFunction.compare_lt, "<");
        put(FuFunction.compare_gt, ">");
        put(FuFunction.compare_leq, "<=");
        put(FuFunction.compare_geq, ">=");
        put(FuFunction.mux_0, "mux 0");
        put(FuFunction.mux_1, "mux 1");
    }};

    public static final HashMap<FuFunction, String> getFuFunctionToBits = new HashMap<FuFunction, String>() {{
        put(FuFunction.none, "11111");
        put(FuFunction.add, "00000");
        put(FuFunction.sub, "00001");
        put(FuFunction.mul, "00010");
        put(FuFunction.div, "00011");
        put(FuFunction.and, "00100");
        put(FuFunction.or, "00101");
        put(FuFunction.xor, "00110");
        put(FuFunction.not, "00111");
        put(FuFunction.shift_left, "01000");
        put(FuFunction.shift_right, "01001");
        put(FuFunction.compare_eq, "01010");
        put(FuFunction.compare_neq, "01011");
        put(FuFunction.compare_lt, "01100");
        put(FuFunction.compare_gt, "01101");
        put(FuFunction.compare_leq, "01110");
        put(FuFunction.compare_geq, "01111");
        put(FuFunction.mux_0, "10000");
        put(FuFunction.mux_1, "10001");
    }};

    private LinkedHashMap<FuMode, Boolean> availableModes;
    private final CRC crc;

    public FU(CRC crc) {

        this.crc = crc;

        availableModes = new LinkedHashMap<>();

        for (FuMode fuMode : FuMode.values()) {
            availableModes.put(fuMode, false);
        }

    }

    public FU(CRC crc, FU fu) {
        this.crc = crc;

        this.availableModes = new LinkedHashMap<>();

        for (FuMode fuMode : FuMode.values()) {
            availableModes.put(fuMode, fu.getAvailableModes().get(fuMode));
        }
    }

    public LinkedHashMap<FuMode, Boolean> getAvailableModes() {
        return availableModes;
    }

    /**
     * set a FU function to true or false
     * @param key
     * @param value
     */
    @SuppressWarnings("SameParameterValue")
    public void setMode(FuMode key, boolean value) {
        this.availableModes.replace(key, value);
    }

    /**
     * sets a FU availableModes at once by copying from availableModes
     * @param availableModes
     */
    public void setAvailableModes(LinkedHashMap<FuMode, Boolean> availableModes) {
        this.availableModes = new LinkedHashMap<>();
        this.availableModes.putAll(availableModes);
        crc.notifyAllObservers();
    }
}

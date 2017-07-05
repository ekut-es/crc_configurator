package de.tuebingen.es.crc.configurator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class FU {

    public enum FuMode {
        add, sub, mul, div, and, or, xor, not, shift_left, shift_right, compare, multiplex, dsp48, lut_8bit, max, min
    }

    public enum FuFunction {
        none,
        add,
        sub,
        mul,
        div,
        and,
        or,
        xor,
        not,
        shift_left,
        shift_right,
        compare_eq,
        compare_neq,
        compare_lt,
        compare_gt,
        compare_leq,
        compare_geq,
        mux_0,
        mux_1,
        dsp_add,
        dsp_mul,
        dsp_mula,
        lut_8bit,
        max,
        min
    }

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
        put(FuMode.dsp48, "dsp48");
        put(FuMode.lut_8bit, "lut_8bit");
        put(FuMode.max, "max");
        put(FuMode.min, "min");
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
        put(FuMode.dsp48, new HashSet<FuFunction>(){{
            add(FuFunction.dsp_add);
            add(FuFunction.dsp_mul);
            add(FuFunction.dsp_mula);
        }});
        put(FuMode.lut_8bit, new HashSet<FuFunction>(){{ add(FuFunction.lut_8bit); }});
        put(FuMode.max, new HashSet<FuFunction>(){{ add(FuFunction.max); }});
        put(FuMode.min, new HashSet<FuFunction>(){{ add(FuFunction.min); }});
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
        put(FuFunction.dsp_add, FuMode.dsp48);
        put(FuFunction.dsp_mul, FuMode.dsp48);
        put(FuFunction.dsp_mula, FuMode.dsp48);
        put(FuFunction.lut_8bit, FuMode.lut_8bit);
        put(FuFunction.max, FuMode.max);
        put(FuFunction.min, FuMode.min);
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
        put(FuFunction.dsp_add, "dsp_add");
        put(FuFunction.dsp_mul, "dsp_mul");
        put(FuFunction.dsp_mula, "dsp_mula");
        put(FuFunction.lut_8bit, "lut_8bit");
        put(FuFunction.max, "max");
        put(FuFunction.min, "min");
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
        put(FuFunction.mux_0, "mux0");
        put(FuFunction.mux_1, "mux1");
        put(FuFunction.dsp_add, "dsp+");
        put(FuFunction.dsp_mul, "dsp×");
        put(FuFunction.dsp_mula, "dsp×+");
        put(FuFunction.lut_8bit, "lut8");
        put(FuFunction.max, "max");
        put(FuFunction.min, "min");
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
        put(FuFunction.dsp_add, "10010");
        put(FuFunction.dsp_mul, "10011");
        put(FuFunction.dsp_mula, "10100");
        put(FuFunction.lut_8bit, "10101");
        put(FuFunction.max, "10110");
        put(FuFunction.min, "10111");
    }};

    private LinkedHashMap<FuMode, Boolean> availableModes;
    private final CRC crc;

    private String lut8BitContentHexString;

    public FU(CRC crc) {

        this.crc = crc;

        availableModes = new LinkedHashMap<>();

        for (FuMode fuMode : FuMode.values()) {
            availableModes.put(fuMode, false);
        }

        this.lut8BitContentHexString = "fffefdfcfbfaf9f8f7f6f5f4f3f2f1f0efeeedecebeae9e8e7e6e5e4e3e2e1e0dfdedddcdbdad9d8d7d6d5d4d3d2d1d0cfcecdcccbcac9c8c7c6c5c4c3c2c1c0bfbebdbcbbbab9b8b7b6b5b4b3b2b1b0afaeadacabaaa9a8a7a6a5a4a3a2a1a09f9e9d9c9b9a999897969594939291908f8e8d8c8b8a898887868584838281807f7e7d7c7b7a797877767574737271706f6e6d6c6b6a696867666564636261605f5e5d5c5b5a595857565554535251504f4e4d4c4b4a494847464544434241403f3e3d3c3b3a393837363534333231302f2e2d2c2b2a292827262524232221201f1e1d1c1b1a191817161514131211100f0e0d0c0b0a09080706050403020100";
    }

    public FU(CRC crc, FU fu) {
        this.crc = crc;

        this.availableModes = new LinkedHashMap<>();

        for (FuMode fuMode : FuMode.values()) {
            availableModes.put(fuMode, fu.getAvailableModes().get(fuMode));
        }

        this.lut8BitContentHexString = fu.getLut8BitContentHexString();
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

    public String getLut8BitContentHexString() {
        return lut8BitContentHexString;
    }

    public void setLut8BitContentHexString(String lut8BitContentHexString) {
        if(lut8BitContentHexString.length() > 512) {
            this.lut8BitContentHexString = lut8BitContentHexString.substring(lut8BitContentHexString.length()-512);
        } else if(lut8BitContentHexString.length() < 512) {
            this.lut8BitContentHexString = String.format("%128s", lut8BitContentHexString).replace(' ', '0');
        } else {
            this.lut8BitContentHexString = lut8BitContentHexString;
        }
    }
}

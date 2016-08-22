package de.tuebingen.es.crc.configurator.model;

import java.util.LinkedHashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class FU {
    private LinkedHashMap<String, Boolean> functions;
    private CRC crc;

    public FU(CRC crc) {

        this.crc = crc;

        functions = new LinkedHashMap<>();

        functions.put("add", false);
        functions.put("sub", false);
        functions.put("mul", false);
        functions.put("div", false);
        functions.put("and", false);
        functions.put("or", false);
        functions.put("xor", false);
        functions.put("not", false);
        functions.put("shift_left", false);
        functions.put("shift_right", false);
        functions.put("compare", false);
        functions.put("multiplex", false);
    }

    public FU(CRC crc, FU fu) {
        this.crc = crc;

        this.functions = new LinkedHashMap<>();

        LinkedHashMap<String, Boolean> fuFunctions = fu.getFunctions();

        this.functions.put("add", fuFunctions.get("add"));
        this.functions.put("sub", fuFunctions.get("sub"));
        this.functions.put("mul", fuFunctions.get("mul"));
        this.functions.put("div", fuFunctions.get("div"));
        this.functions.put("and", fuFunctions.get("and"));
        this.functions.put("or", fuFunctions.get("or"));
        this.functions.put("xor", fuFunctions.get("xor"));
        this.functions.put("not", fuFunctions.get("not"));
        this.functions.put("shift_left", fuFunctions.get("shift_left"));
        this.functions.put("shift_right", fuFunctions.get("shift_right"));
        this.functions.put("compare", fuFunctions.get("compare"));
        this.functions.put("multiplex", fuFunctions.get("multiplex"));
    }

    public LinkedHashMap<String, Boolean> getFunctions() {
        return functions;
    }

    /**
     * set a FU function to true or false
     * @param key
     * @param value
     */
    public void setFunction(String key, boolean value) {
        this.functions.replace(key, value);
    }

    /**
     * sets a FU functions at once by copying from functions
     * @param functions
     */
    public void setFunctions(LinkedHashMap<String, Boolean> functions) {
        this.functions = new LinkedHashMap<String, Boolean>();
        this.functions.putAll(functions);
        crc.notifyAllObservers();
    }
}

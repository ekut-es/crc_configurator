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

package de.tuebingen.es.crc.configurator.model;

import java.util.LinkedHashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class FU {
    private LinkedHashMap<String, Boolean> functions;

    public FU() {
        this.functions = new LinkedHashMap<>();

        this.functions.put("add", false);
        this.functions.put("sub", false);
        this.functions.put("mul", false);
        this.functions.put("div", false);
        this.functions.put("and", false);
        this.functions.put("or", false);
        this.functions.put("xor", false);
        this.functions.put("not", false);
        this.functions.put("shift_left", false);
        this.functions.put("shift_right", false);
        this.functions.put("compare", false);
        this.functions.put("multiplex", false);
    }

    public void setFunction(String key, boolean value) {
        this.functions.replace(key, value);
    }

    public LinkedHashMap<String, Boolean> getFunctions() {
        return this.functions;
    }
}

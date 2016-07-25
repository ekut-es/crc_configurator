package de.tuebingen.es.crc.configurator.model;

import java.util.HashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class FU {
    private HashMap<String, Boolean> functions;

    public FU() {
        this.functions = new HashMap<>();

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
}

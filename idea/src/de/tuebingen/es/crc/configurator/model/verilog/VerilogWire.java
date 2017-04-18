package de.tuebingen.es.crc.configurator.model.verilog;

/**
 * Created by Konstantin (Konze) Lübeck on 27/02/2017.
 */
public class VerilogWire implements VerilogModule {
    public String name;
    public String width;

    public VerilogWire(String name) {
        this.name = name;
        this.width = "1";
    }

    public VerilogWire(String name, String width) {
        this.name = name;
        this.width = width;
    }

    public String getDeclaration() {
        if(width.equals("1")) {
            return "wire " + name + ";";
        } else {
            return "wire [" + width + "-1:0] " + name + ";";
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

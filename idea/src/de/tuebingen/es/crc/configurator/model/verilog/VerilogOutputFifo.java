package de.tuebingen.es.crc.configurator.model.verilog;

/**
 * Created by Konstantin (Konze) Lübeck on 03/03/2017.
 */
public class VerilogOutputFifo implements VerilogModule {
    public String name;

    public int length = 2;

    public String data_in;
    public String flag_in;
    public String valid_bit_in;
    public String config_select_in;
    public String read;
    public String data_out;
    public String flag_out;
    public String valid_bit_out;
    public String config_select_out;
    public String full;

    public VerilogOutputFifo(String name) {
        this.name = name;
    }

    public String getDeclaration() {
        return  "    OUTPUT_FIFO #(\n" +
                "        .LENGTH(" + this.length + ")\n" +
                "    ) " + this.name + " (\n" +
                "        .clk(clk),\n" +
                "        .reset(reset),\n" +
                "\n" +
                "        .data_in(" + this.data_in + "),\n" +
                "        .flag_in(" + this.flag_in + "),\n" +
                "        .valid_bit_in(" + this.valid_bit_in + "),\n" +
                "        .config_select_in(" + this.config_select_in + "),\n" +
                "\n" +
                "        .read(" + this.read + "),\n" +
                "\n" +
                "        .data_out(" + this.data_out + "),\n" +
                "        .flag_out(" + this.flag_out + "),\n" +
                "        .valid_bit_out(" + this.valid_bit_out + "),\n" +
                "        .config_select_out(" + this.config_select_out + "),\n" +
                "\n" +
                "        .full(" + this.full + ")\n" +
                "    );\n";
    }
}

package de.tuebingen.es.crc.configurator.model.verilog;

/**
 * Created by Konstantin (Konze) Lübeck on 28/02/2017.
 */
public class VerilogPE implements VerilogModule {
    public String name;

    // parameters
    public boolean op_add;
    public boolean op_sub;
    public boolean op_mul;
    public boolean op_div;
    public boolean op_and;
    public boolean op_or;
    public boolean op_xor;
    public boolean op_not;
    public boolean op_shift_left;
    public boolean op_shift_right;
    public boolean op_compare;
    public boolean op_multiplex;
    public boolean op_dsp48;
    public boolean op_lut_8bit;
    public boolean op_max;
    public boolean op_min;
    public String static_config_content;
    public String static_const_reg_content;
    public String lut_8bit_content;
    public boolean regs_in_north;
    public boolean regs_in_south;

    public String enable_config_read;
    public String enable_const_reg_read;

    public String flag_exception;

    public String config_in;
    public String config_load_select;
    public String config_select;

    public String config_select_in_N_0;
    public String config_select_in_N_1;
    public String config_select_in_E_0;
    public String config_select_in_E_1;
    public String config_select_in_S_0;
    public String config_select_in_S_1;
    public String config_select_in_W_0;
    public String config_select_in_W_1;

    public String config_select_out_N_0;
    public String config_select_out_N_1;
    public String config_select_out_E_0;
    public String config_select_out_E_1;
    public String config_select_out_S_0;
    public String config_select_out_S_1;
    public String config_select_out_W_0;
    public String config_select_out_W_1;

    // data input from the north
    public String data_in_N_0;
    public String flag_in_N_0;
    public String valid_bit_in_N_0;
    public String input_ready_N_0;

    public String data_in_N_1;
    public String flag_in_N_1;
    public String valid_bit_in_N_1;
    public String input_ready_N_1;

    // data input from the south
    public String data_in_S_0;
    public String flag_in_S_0;
    public String valid_bit_in_S_0;
    public String input_ready_S_0;

    public String data_in_S_1;
    public String flag_in_S_1;
    public String valid_bit_in_S_1;
    public String input_ready_S_1;

    // data input from the west
    public String data_in_W_0;
    public String flag_in_W_0;
    public String valid_bit_in_W_0;
    public String input_ready_W_0;

    public String data_in_W_1;
    public String flag_in_W_1;
    public String valid_bit_in_W_1;
    public String input_ready_W_1;

    // data output to the north
    public String data_out_N_0;
    public String flag_out_N_0;
    public String valid_bit_out_N_0;
    public String ext_input_ready_N_0;

    public String data_out_N_1;
    public String flag_out_N_1;
    public String valid_bit_out_N_1;
    public String ext_input_ready_N_1;

    // data output to the east
    public String data_out_E_0;
    public String flag_out_E_0;
    public String valid_bit_out_E_0;
    public String ext_input_ready_E_0;

    public String data_out_E_1;
    public String flag_out_E_1;
    public String valid_bit_out_E_1;
    public String ext_input_ready_E_1;

    // data output to the south
    public String data_out_S_0;
    public String flag_out_S_0;
    public String valid_bit_out_S_0;
    public String ext_input_ready_S_0;

    public String data_out_S_1;
    public String flag_out_S_1;
    public String valid_bit_out_S_1;
    public String ext_input_ready_S_1;

    public VerilogPE(String name) {
        this.name = name;
    }

    public String getDeclaration() {
        String peModule =
                "    PE #(\n" +
                "        .op_add(" + (this.op_add ? "1'b1" : "1'b0") + "),\n" +
                "        .op_sub(" + (this.op_sub ? "1'b1" : "1'b0") + "),\n" +
                "        .op_mul(" + (this.op_mul ? "1'b1" : "1'b0") + "),\n" +
                "        .op_div(" + (this.op_div ? "1'b1" : "1'b0") + "),\n" +
                "        .op_and(" + (this.op_and ? "1'b1" : "1'b0") + "),\n" +
                "        .op_or(" + (this.op_or ? "1'b1" : "1'b0") + "),\n" +
                "        .op_xor(" + (this.op_xor ? "1'b1" : "1'b0") + "),\n" +
                "        .op_not(" + (this.op_not ? "1'b1" : "1'b0") + "),\n" +
                "        .op_shift_left(" + (this.op_shift_left ? "1'b1" : "1'b0") + "),\n" +
                "        .op_shift_right(" + (this.op_shift_right ? "1'b1" : "1'b0") + "),\n" +
                "        .op_compare(" + (this.op_compare ? "1'b1" : "1'b0") + "),\n" +
                "        .op_multiplex(" + (this.op_multiplex ? "1'b1" : "1'b0") + "),\n" +
                "        .op_dsp48(" + (this.op_dsp48 ? "1'b1" : "1'b0") + "),\n" +
                "        .op_lut_8bit(" + (this.op_lut_8bit ? "1'b1" : "1'b0") + "),\n" +
                "        .op_max(" + (this.op_max ? "1'b1" : "1'b0") + "),\n" +
                "        .op_min(" + (this.op_min ? "1'b1" : "1'b0") + "),\n";

        peModule += (this.static_config_content != null) ? "        .static_config_content(" + this.static_config_content + "),\n" : "";
        peModule += (this.static_const_reg_content != null) ? "        .static_const_reg_content(" + this.static_const_reg_content + "),\n" : "";
        peModule += (this.op_lut_8bit && this.lut_8bit_content != null) ? "        .lut_8bit_content(" + this.lut_8bit_content + "),\n" : "";

        peModule +=
                "        .regs_in_north(" + (this.regs_in_north ? "1'b1" : "1'b0") + "),\n" +
                "        .regs_in_south(" + (this.regs_in_south ? "1'b1" : "1'b0") + ")\n" +
                "    ) " + this.name + "(\n" +
                "        .clk(clk),\n" +
                "        .reset(reset),\n" +
                "        .enable_config_read(" + this.enable_config_read + "),\n" +
                "        .enable_const_reg_read(" + this.enable_const_reg_read + "),\n" +
                "        .flag_exception(" + this.flag_exception + "),\n" +
                "        .config_in(" + this.config_in + "),\n" +
                "        .config_select(" + this.config_select + "),\n" +
                "        .config_load_select(" + this.config_load_select + "),\n";

        peModule += "        .config_select_in_N_0(" + ((this.config_select_in_N_0 != null) ? this.config_select_in_N_0 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_N_1(" + ((this.config_select_in_N_1 != null) ? this.config_select_in_N_1 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_E_0(" + ((this.config_select_in_E_0 != null) ? this.config_select_in_E_0 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_E_1(" + ((this.config_select_in_E_1 != null) ? this.config_select_in_E_1 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_S_0(" + ((this.config_select_in_S_0 != null) ? this.config_select_in_S_0 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_S_1(" + ((this.config_select_in_S_1 != null) ? this.config_select_in_S_1 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_W_0(" + ((this.config_select_in_W_0 != null) ? this.config_select_in_W_0 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";
        peModule += "        .config_select_in_W_1(" + ((this.config_select_in_W_1 != null) ? this.config_select_in_W_1 : "{`CONFIG_SELECT_WIDTH{1'b0}}") + "),\n";

        peModule += (this.config_select_out_N_0 != null) ? "        .config_select_out_N_0(" + this.config_select_out_N_0 + "),\n" : "";
        peModule += (this.config_select_out_N_1 != null) ? "        .config_select_out_N_1(" + this.config_select_out_N_1 + "),\n" : "";
        peModule += (this.config_select_out_E_0 != null) ? "        .config_select_out_E_0(" + this.config_select_out_E_0 + "),\n" : "";
        peModule += (this.config_select_out_E_1 != null) ? "        .config_select_out_E_1(" + this.config_select_out_E_1 + "),\n" : "";
        peModule += (this.config_select_out_S_0 != null) ? "        .config_select_out_S_0(" + this.config_select_out_S_0 + "),\n" : "";
        peModule += (this.config_select_out_S_1 != null) ? "        .config_select_out_S_1(" + this.config_select_out_S_1 + "),\n" : "";
        peModule += (this.config_select_out_W_0 != null) ? "        .config_select_out_W_0(" + this.config_select_out_W_0 + "),\n" : "";
        peModule += (this.config_select_out_W_1 != null) ? "        .config_select_out_W_1(" + this.config_select_out_W_1 + "),\n" : "";

        // data input from the north
        peModule += "        .data_in_N_0(" + ((this.data_in_N_0 != null) ? this.data_in_N_0 : "{`DATA_WIDTH{1'b0}}") + "),\n";
        peModule += "        .flag_in_N_0(" + ((this.flag_in_N_0 != null) ? this.flag_in_N_0 : "1'b0") + "),\n";
        peModule += "        .valid_bit_in_N_0(" + ((this.valid_bit_in_N_0 != null) ? this.valid_bit_in_N_0 : "1'b0") + "),\n";
        peModule += (this.input_ready_N_0 != null) ? "        .input_ready_N_0(" + this.input_ready_N_0 + "),\n" : "";

        peModule += "        .data_in_N_1(" + ((this.data_in_N_1 != null) ? this.data_in_N_1 : "{`DATA_WIDTH{1'b0}}") + "),\n";
        peModule += "        .flag_in_N_1(" + ((this.flag_in_N_1 != null) ? this.flag_in_N_1 : "1'b0") + "),\n";
        peModule += "        .valid_bit_in_N_1(" + ((this.valid_bit_in_N_1 != null) ? this.valid_bit_in_N_1 : "1'b0") + "),\n";
        peModule += (this.input_ready_N_1 != null) ? "        .input_ready_N_1(" + this.input_ready_N_1 + "),\n" : "";

        // data input from the south
        peModule += "        .data_in_S_0(" + ((this.data_in_S_0 != null) ? this.data_in_S_0 : "{`DATA_WIDTH{1'b0}}") + "),\n";
        peModule += "        .flag_in_S_0(" + ((this.flag_in_S_0 != null) ? this.flag_in_S_0 : "1'b0") + "),\n";
        peModule += "        .valid_bit_in_S_0(" + ((this.valid_bit_in_S_0 != null) ? this.valid_bit_in_S_0 : "1'b0") + "),\n";
        peModule += (this.input_ready_S_0 != null) ? "        .input_ready_S_0(" + this.input_ready_S_0 + "),\n" : "";

        peModule += "        .data_in_S_1(" + ((this.data_in_S_1 != null) ? this.data_in_S_1 : "{`DATA_WIDTH{1'b0}}") + "),\n";
        peModule += "        .flag_in_S_1(" + ((this.flag_in_S_1 != null) ? this.flag_in_S_1 : "1'b0") + "),\n";
        peModule += "        .valid_bit_in_S_1(" + ((this.valid_bit_in_S_1 != null) ? this.valid_bit_in_S_1 : "1'b0") + "),\n";
        peModule += (this.input_ready_S_1 != null) ? "        .input_ready_S_1(" + this.input_ready_S_1 + "),\n" : "";

        // data input from the west
        peModule += "        .data_in_W_0(" + ((this.data_in_W_0 != null) ? this.data_in_W_0 : "{`DATA_WIDTH{1'b0}}") + "),\n";
        peModule += "        .flag_in_W_0(" + ((this.flag_in_W_0 != null) ? this.flag_in_W_0 : "1'b0") + "),\n";
        peModule += "        .valid_bit_in_W_0(" + ((this.valid_bit_in_W_0 != null) ? this.valid_bit_in_W_0 : "1'b0") + "),\n";
        peModule += (this.input_ready_W_0 != null) ? "        .input_ready_W_0(" + this.input_ready_W_0 + "),\n" : "";

        peModule += "        .data_in_W_1(" + ((this.data_in_W_1 != null) ? this.data_in_W_1 : "{`DATA_WIDTH{1'b0}}") + "),\n";
        peModule += "        .flag_in_W_1(" + ((this.flag_in_W_1 != null) ? this.flag_in_W_1 : "1'b0") + "),\n";
        peModule += "        .valid_bit_in_W_1(" + ((this.valid_bit_in_W_1 != null) ? this.valid_bit_in_W_1 : "1'b0") + "),\n";
        peModule += (this.input_ready_W_1 != null) ? "        .input_ready_W_1(" + this.input_ready_W_1 + "),\n" : "";

        // data output to the north
        peModule += (this.data_out_N_0 != null) ? "        .data_out_N_0(" + this.data_out_N_0 + "),\n" : "";
        peModule += (this.flag_out_N_0 != null) ? "        .flag_out_N_0(" + this.flag_out_N_0 + "),\n" : "";
        peModule += (this.valid_bit_out_N_0 != null) ? "        .valid_bit_out_N_0(" + this.valid_bit_out_N_0 + "),\n" : "";
        peModule += "        .ext_input_ready_N_0(" + ((this.ext_input_ready_N_0 != null) ? this.ext_input_ready_N_0 : "1'b0") + "),\n";

        peModule += (this.data_out_N_1 != null) ? "        .data_out_N_1(" + this.data_out_N_1 + "),\n" : "";
        peModule += (this.flag_out_N_1 != null) ? "        .flag_out_N_1(" + this.flag_out_N_1 + "),\n" : "";
        peModule += (this.valid_bit_out_N_1 != null) ? "        .valid_bit_out_N_1(" + this.valid_bit_out_N_1 + "),\n" : "";
        peModule += "        .ext_input_ready_N_1(" + ((this.ext_input_ready_N_1 != null) ? this.ext_input_ready_N_1 : "1'b0") + "),\n";

        // data output to the east
        peModule += (this.data_out_E_0 != null) ? "        .data_out_E_0(" + this.data_out_E_0 + "),\n" : "";
        peModule += (this.flag_out_E_0 != null) ? "        .flag_out_E_0(" + this.flag_out_E_0 + "),\n" : "";
        peModule += (this.valid_bit_out_E_0 != null) ? "        .valid_bit_out_E_0(" + this.valid_bit_out_E_0 + "),\n" : "";
        peModule += "        .ext_input_ready_E_0(" + ((this.ext_input_ready_E_0 != null) ? this.ext_input_ready_E_0 : "1'b0") + "),\n";

        peModule += (this.data_out_E_1 != null) ? "        .data_out_E_1(" + this.data_out_E_1 + "),\n" : "";
        peModule += (this.flag_out_E_1 != null) ? "        .flag_out_E_1(" + this.flag_out_E_1 + "),\n" : "";
        peModule += (this.valid_bit_out_E_1 != null) ? "        .valid_bit_out_E_1(" + this.valid_bit_out_E_1 + "),\n" : "";
        peModule += "        .ext_input_ready_E_1(" + ((this.ext_input_ready_E_1 != null) ? this.ext_input_ready_E_1 : "1'b0") + "),\n";

        // data output to the south
        peModule += (this.data_out_S_0 != null) ? "        .data_out_S_0(" + this.data_out_S_0 + "),\n" : "";
        peModule += (this.flag_out_S_0 != null) ? "        .flag_out_S_0(" + this.flag_out_S_0 + "),\n" : "";
        peModule += (this.valid_bit_out_S_0 != null) ? "        .valid_bit_out_S_0(" + this.valid_bit_out_S_0 + "),\n" : "";
        peModule += "        .ext_input_ready_S_0(" + ((this.ext_input_ready_S_0 != null) ? this.ext_input_ready_S_0 : "1'b0") + "),\n";

        peModule += (this.data_out_S_1 != null) ? "        .data_out_S_1(" + this.data_out_S_1 + "),\n" : "";
        peModule += (this.flag_out_S_1 != null) ? "        .flag_out_S_1(" + this.flag_out_S_1 + "),\n" : "";
        peModule += (this.valid_bit_out_S_1 != null) ? "        .valid_bit_out_S_1(" + this.valid_bit_out_S_1 + "),\n" : "";
        peModule += "        .ext_input_ready_S_1(" + ((this.ext_input_ready_S_1 != null) ? this.ext_input_ready_S_1 : "1'b0") + ")\n";

        //peModule = peModule.substring(0, peModule.length()-1);
        peModule += "    );\n";

        return peModule;
    }
}

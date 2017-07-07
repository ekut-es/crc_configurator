package de.tuebingen.es.crc.configurator.model.verilog;

import de.tuebingen.es.crc.configurator.model.CRC;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Konstantin (Konze) Lübeck on 02/03/2017.
 */
public class CRCVerilogTestBenchGenerator {

    private CRC crc;

    public CRCVerilogTestBenchGenerator(CRC crc) {
        this.crc = crc;
    }

    public String generate() {

        Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        // header
        String header = "/* -------------------------------------------------------------------------\n" +
                        " * This file was generated with the CRC Configurator on:\n" +
                        " * " + formatter.format(Calendar.getInstance().getTime()) + "\n" +
                        " * ------------------------------------------------------------------------- */\n\n" +
                        "`include \"preprocessor.v\"\n\n";

        // macros for data inputs
        String macros = "";

        for(int row = 0; row < crc.getRows(); row++) {
            int i = 2*row;
            macros += "`define DATA_IN_W_" + i + " data_in_W[" + (i+1) + "*`DATA_WIDTH-1:" + i + "*`DATA_WIDTH]\n";
            macros += "`define FLAG_IN_W_" + i + " flag_in_W[" + i + ":" + i + "]\n";
            macros += "`define VALID_BIT_IN_W_" + i + "  valid_bit_in_W[" + i + ":" + i + "]\n\n";

            i = 2*row+1;
            macros += "`define DATA_IN_W_" + i + " data_in_W[" + (i+1) + "*`DATA_WIDTH-1:" + i + "*`DATA_WIDTH]\n";
            macros += "`define FLAG_IN_W_" + i + " flag_in_W[" + i + ":" + i + "]\n";
            macros += "`define VALID_BIT_IN_W_" + i + "  valid_bit_in_W[" + i + ":" + i + "]\n\n";
        }

        if(this.crc.areInputsNorth()) {
            for(int column = 0; column < crc.getColumns(); column++) {
                int i = 2*column;
                macros += "`define DATA_IN_N_" + i + " data_in_N[" + (i+1) + "*`DATA_WIDTH-1:" + i + "*`DATA_WIDTH]\n";
                macros += "`define FLAG_IN_N_" + i + " flag_in_N[" + i + ":" + i + "]\n";
                macros += "`define VALID_BIT_IN_N_" + i + "  valid_bit_in_N[" + i + ":" + i + "]\n\n";

                i = 2*column+1;
                macros += "`define DATA_IN_N_" + i + " data_in_N[" + (i+1) + "*`DATA_WIDTH-1:" + i + "*`DATA_WIDTH]\n";
                macros += "`define FLAG_IN_N_" + i + " flag_in_N[" + i + ":" + i + "]\n";
                macros += "`define VALID_BIT_IN_N_" + i + "  valid_bit_in_N[" + i + ":" + i + "]\n\n";
            }
        }

        // macros for data outputs
        for(int row = 0; row < crc.getRows(); row++) {
            int i = 2*row;
            macros += "`define DATA_OUT_E" + i + " data_out_E[" + (i+1) + "*`DATA_WIDTH-1:" + i + "*`DATA_WIDTH]\n";
            macros += "`define FLAG_OUT_E" + i + " flag_out_E[" + i + ":" + i + "]\n";
            macros += "`define VALID_BIT_OUT_E" + i + "  valid_bit_out_E[" + i + ":" + i + "]\n\n";

            i = 2*row+1;
            macros += "`define DATA_OUT_E" + i + " data_out_E[" + (i+1) + "*`DATA_WIDTH-1:" + i + "*`DATA_WIDTH]\n";
            macros += "`define FLAG_OUT_E" + i + " flag_out_E[" + i + ":" + i + "]\n";
            macros += "`define VALID_BIT_OUT_E" + i + "  valid_bit_out_E[" + i + ":" + i + "]\n\n";
        }

        // module definition
        String module = "module CRC_CORE_W_INPUT_OUTPUT_FIFOS_tb();\n\n" +
                        "/* -------------------------------------------------------------------------\n" +
                        " *               connection to CRC_CORE_W_INPUT_OUTPUT_FIFOS\n" +
                        " * ------------------------------------------------------------------------- */\n" +
                        "\n" +
                        "    reg clk;\n" +
                        "    reg reset;\n" +
                        "    reg [" + (crc.getRows()*crc.getColumns()) + "-1:0] enable_config_read;\n" +
                        "\n" +
                        "    reg [(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_WIDTH)-1:0] config_in;\n" +
                        "    reg [(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH)-1:0] config_load_select;\n" +
                        "    reg [(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select;\n" +
                        "\n" +
                        "    reg [(" + (crc.getRows()*2) + "*`DATA_WIDTH)-1:0] data_in_W;\n" +
                        "    reg [" + (crc.getRows()*2) + "-1:0] flag_in_W;\n" +
                        "    reg [" + (crc.getRows()*2) + "-1:0] valid_bit_in_W;\n" +
                        "    reg [(" + (crc.getRows()*2) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select_in_W;\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] input_fifo_full_W;\n" +
                        "\n";

        if(crc.areInputsNorth()) {
            module +=   "    reg [(" + (crc.getColumns()*2) + "*`DATA_WIDTH)-1:0] data_in_N;\n" +
                        "    reg [" + (crc.getColumns()*2) + "-1:0] flag_in_N;\n" +
                        "    reg [" + (crc.getColumns()*2) + "-1:0] valid_bit_in_N;\n" +
                        "    reg [(" + (crc.getColumns()*2) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select_in_N;\n" +
                        "    wire [" + (crc.getColumns()*2) + "-1:0] input_fifo_full_N;\n" +
                        "\n";
        }

        module +=       "    reg [" + (crc.getRows()*2) + "-1:0] output_fifo_read_E;\n" +
                        "\n" +
                        "\n" +
                        "    wire [(" + (crc.getRows()*2) + "*`DATA_WIDTH)-1:0] data_out_E;\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] flag_out_E;\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] valid_bit_out_E;\n" +
                        "    wire [(" + (crc.getRows()*2) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select_out_E;\n" +
                        "\n" +
                        "    wire [" + (crc.getRows()*crc.getColumns()) + "-1:0] flag_exception;\n" +
                        "\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] output_fifo_full_E;\n\n";

        // task which sets all inputs to 0
        module +=       "    task setAllInputsTo0();\n" +
                        "        enable_config_read <= {" + (crc.getRows()*crc.getColumns()) + "{1'b0}};\n" +
                        "\n" +
                        "        config_in <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_WIDTH){1'b0}};\n" +
                        "        config_load_select <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH){1'b0}};\n" +
                        "        config_select <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH){1'b0}};\n" +
                        "\n" +
                        "        data_in_W <= {(" + crc.getRows() + "*`DATA_WIDTH*2){1'b0}};\n" +
                        "        flag_in_W <= {(" + crc.getRows() + "*2){1'b0}};\n" +
                        "        valid_bit_in_W <= {(" + crc.getRows() + "*2){1'b0}};\n" +
                        "        config_select_in_W <= {(" + crc.getRows() + "*`CONFIG_SELECT_WIDTH*2){1'b0}};\n" +
                        "\n";

        if(crc.areInputsNorth()) {
            module +=   "        data_in_N <= {(" + crc.getColumns() + "*`DATA_WIDTH*2){1'b0}};\n" +
                        "        flag_in_N <= {(" + crc.getColumns() + "*2){1'b0}};\n" +
                        "        valid_bit_in_N <= {(" + crc.getColumns() + "*2){1'b0}};\n" +
                        "        config_select_in_N <= {(" + crc.getColumns() + "*`CONFIG_SELECT_WIDTH*2){1'b0}};\n" +
                        "\n";
        }

        module +=       "        output_fifo_read_E <= {(" + crc.getRows() + "*2){1'b0}};\n" +
                        "    endtask\n\n";

        // task which resets CRC
        module +=       "    task doReset();\n" +
                        "        reset = 1'b1;\n" +
                        "        `TICK\n" +
                        "        `TICK\n" +
                        "        reset = 1'b0;\n" +
                        "    endtask\n\n";

        // task which sets config selects
        module +=       "    task setConfigSelects(reg [`CONFIG_SELECT_WIDTH-1:0] config_select_value);\n";

        String configSelectIns = "";
        String configLoadSelects = "";
        String configSelects = "";

        for(int row = 0; row < crc.getRows(); row++) {

            configSelectIns += "        config_select_in_W[`CONFIG_SELECT_WIDTH*" + (2*row+1) + "-1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
            configSelectIns += "        config_select_in_W[`CONFIG_SELECT_WIDTH*" + (2*row+2) + "-1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";

            for(int column = 0; column < crc.getColumns(); column++) {
                configLoadSelects += "        config_load_select[(`CONFIG_SELECT_WIDTH*" + (row*crc.getColumns()+column+1) + ") - 1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
                configSelects += "        config_select[(`CONFIG_SELECT_WIDTH*" + (row*crc.getColumns()+column+1) + ") - 1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
            }
        }

        for(int column = 0; column < crc.getColumns(); column++) {
            configSelectIns += "        config_select_in_N[`CONFIG_SELECT_WIDTH*" + (2*column+1) + "-1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
            configSelectIns += "        config_select_in_N[`CONFIG_SELECT_WIDTH*" + (2*column+2) + "-1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
        }

        module += configSelectIns + "\n";
        module += configLoadSelects + "\n";
        module += configSelects + "\n";

        module +=       "    endtask\n\n";

        // task which loads a config line into a PE
        module +=       "    task doConfiguration(integer row, integer column, reg [`CONFIG_WIDTH-1:0] config_data);\n";
        module +=       "        enable_config_read[((row * " + crc.getColumns()+ ") + column +1) -1 -: 1] = 1'b1;\n";
        module +=       "        config_in[(`CONFIG_WIDTH*((row * " + crc.getColumns() + ") + column +1)) -1 -: `CONFIG_WIDTH] = config_data;\n";
        module +=       "        `TICK\n";
        module +=       "        `TICK\n";
        module +=       "        enable_config_read[((row * " + crc.getColumns()+ ") + column +1) -1 -: 1] = 1'b0;\n";
        module +=       "    endtask\n\n";


        for(int dynamicConfigurationNumber = 0; dynamicConfigurationNumber < crc.getDynamicConfigLines(); dynamicConfigurationNumber++) {

            // task which loads a dynamic configuration
            module +=       "    task loadDynamicConfiguration" + dynamicConfigurationNumber+ "();\n";

            for(int row = 0; row < crc.getRows(); row++) {
                for(int column = 0; column < crc.getColumns(); column++) {
                    module += "        `TICK\n";
                    module += "        doConfiguration(" + row + ", " + column + ", " + crc.getPeDynamicConfigParameterBits(row, column, dynamicConfigurationNumber).length() + "'b" + crc.getPeDynamicConfigParameterBits(row, column, dynamicConfigurationNumber) + ");\n";
                }
            }

            module +=       "    endtask\n\n";
        }

        // task which displays the data/flag output
        module +=       "    task displayDataFlagOutput();\n";
        module +=       "        $display(\"OUTPUT_DATA_E\");\n";
        module +=       "        $display(\"\\tv f          d\");\n";

        for(int row = 0; row < crc.getRows(); row++) {
            module +=   "        $display(\"ROW " + row + "\");\n";
            module +=   "        $display(\"" + (2*row) + "\\t%b %b %d\", valid_bit_out_E[" + (2*row) + ":" + (2*row) + "], flag_out_E[" + (2*row) + ":" + (2*row) + "], data_out_E[(`DATA_WIDTH*" + (2*row+1) + ")-1 -:`DATA_WIDTH]);\n";
            module +=   "        $display(\"" + (2*row+1) + "\\t%b %b %d\", valid_bit_out_E[" + (2*row+1) + ":" + (2*row+1) + "], flag_out_E[" + (2*row+1) + ":" + (2*row+1) + "], data_out_E[(`DATA_WIDTH*" + (2*row+2) + ")-1 -:`DATA_WIDTH]);\n";
        }

        module +=       "    endtask\n\n";

        // initial begin and clk
        module +=       "    initial begin\n" +
                        "        $display(\"START CRC_CORE_W_INPUT_OUTPUT_FIFOS testbench\");\n" +
                        "\n" +
                        "        /**\n" +
                        "         * Macros for data inputs and outputs\n" +
                        "         * data_{IN,OUT}_{N,E,S,W}_*:\n" +
                        "         * `VALID_BIT_{IN,OUT}_{N,E,S,W}_*\n" +
                        "         * `FLAG_{IN,OUT}_{N,E,S,W}_*\n" +
                        "         * `DATA_{IN,OUT}_{N,E,S,W}_*\n" +
                        "         */\n" +
                        "\n" +
                        "        clk = 1;\n" +
                        "        reset = 0;\n" +
                        "\n" +
                        "        setAllInputsTo0();\n" +
                        "        doReset();\n" +
                        "        setConfigSelects(3'b000);\n" +
                        "\n" +
                        "        `TICK\n" +
                        "\n" +
                        "        $display(\"STOP CRC_CORE_W_INPUT_OUTPUT_FIFOS testbench\");\n" +
                        "        $stop;\n" +
                        "    end\n" +
                        "\n" +
                        "    always begin\n" +
                        "        `HALF_TICK clk = !clk;\n" +
                        "    end\n\n";

        // DUT
        module +=       "    CRC_CORE_W_INPUT_OUTPUT_FIFOS DUT(\n" +
                        "        .clk(clk),\n" +
                        "        .reset(reset),\n" +
                        "        .enable_config_read(enable_config_read),\n" +
                        "        .flag_exception(flag_exception),\n" +
                        "\n" +
                        "        .config_in(config_in),\n" +
                        "        .config_load_select(config_load_select),\n" +
                        "        .config_select(config_select),\n" +
                        "\n" +
                        "        .data_in_W(data_in_W),\n" +
                        "        .flag_in_W(flag_in_W),\n" +
                        "        .valid_bit_in_W(valid_bit_in_W),\n" +
                        "        .config_select_in_W(config_select_in_W),\n" +
                        "        .input_fifo_full_W(input_fifo_full_W),\n" +
                        "\n";
        if(crc.areInputsNorth()) {
            module +=   "        .data_in_N(data_in_N),\n" +
                        "        .flag_in_N(flag_in_N),\n" +
                        "        .valid_bit_in_N(valid_bit_in_N),\n" +
                        "        .config_select_in_N(config_select_in_N),\n" +
                        "        .input_fifo_full_N(input_fifo_full_N),\n" +
                        "\n";
        }

        module +=       "        .data_out_E(data_out_E),\n" +
                        "        .flag_out_E(flag_out_E),\n" +
                        "        .valid_bit_out_E(valid_bit_out_E),\n" +
                        "        .config_select_out_E(config_select_out_E),\n" +
                        "\n" +
                        "        .output_fifo_full_E(output_fifo_full_E),\n" +
                        "        .output_fifo_read_E(output_fifo_read_E)\n" +
                        "    );\n\n";

        module += "endmodule\n";

        String file = header;
        file += macros;
        file += module;

        return file;
    }
}

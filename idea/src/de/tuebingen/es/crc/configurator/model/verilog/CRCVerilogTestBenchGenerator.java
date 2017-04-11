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
                        "    reg [(" + (crc.getRows()*2) + "*`DATA_WIDTH)-1:0] data_in;\n" +
                        "    reg [" + (crc.getRows()*2) + "-1:0] flag_in;\n" +
                        "    reg [" + (crc.getRows()*2) + "-1:0] valid_bit_in;\n" +
                        "    reg [(" + (crc.getRows()*2) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select_in;\n" +
                        "\n" +
                        "    reg [" + (crc.getRows()*2) + "-1:0] output_fifo_read;\n" +
                        "\n" +
                        "\n" +
                        "    wire [(" + (crc.getRows()*2) + "*`DATA_WIDTH)-1:0] data_out;\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] flag_out;\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] valid_bit_out;\n" +
                        "    wire [(" + (crc.getRows()*2) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select_out;\n" +
                        "\n" +
                        "    wire [" + (crc.getRows()*crc.getColumns()) + "-1:0] flag_exception;\n" +
                        "\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] input_fifo_full;\n" +
                        "    wire [" + (crc.getRows()*2) + "-1:0] output_fifo_full;\n\n";

        // task which sets all inputs to 0
        module +=       "    task setAllInputsTo0();\n" +
                        "        enable_config_read <= {" + (crc.getRows()*crc.getColumns()) + "{1'b0}};\n" +
                        "\n" +
                        "        config_in <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_WIDTH){1'b0}};\n" +
                        "        config_load_select <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH){1'b0}};\n" +
                        "        config_select <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH){1'b0}};\n" +
                        "\n" +
                        "        data_in <= {(" + crc.getRows() + "*`DATA_WIDTH*2){1'b0}};\n" +
                        "        flag_in <= {(" + crc.getRows() + "*2){1'b0}};\n" +
                        "        valid_bit_in <= {(" + crc.getRows() + "*2){1'b0}};\n" +
                        "        config_select_in <= {(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH){1'b0}};\n" +
                        "\n" +
                        "        output_fifo_read <= {(" + crc.getRows() + "*2){1'b0}};\n" +
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

            configSelectIns += "        config_select_in[`CONFIG_SELECT_WIDTH*" + (2*row+1) + "-1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
            configSelectIns += "        config_select_in[`CONFIG_SELECT_WIDTH*" + (2*row+2) + "-1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";

            for(int column = 0; column < crc.getColumns(); column++) {
                configLoadSelects += "        config_load_select[(`CONFIG_SELECT_WIDTH*" + (row*crc.getColumns()+column+1) + ") - 1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
                configSelects += "        config_select[(`CONFIG_SELECT_WIDTH*" + (row*crc.getColumns()+column+1) + ") - 1 -: `CONFIG_SELECT_WIDTH] = config_select_value;\n";
            }
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
        module +=       "        $display(\"OUTPUT_DATA\");\n";
        module +=       "        $display(\"\\tv f          d\");\n";

        for(int row = 0; row < crc.getRows(); row++) {
            module +=   "        $display(\"ROW " + row + "\");\n";
            module +=   "        $display(\"" + (2*row) + "\\t%b %b %d\", valid_bit_out[" + (2*row) + ":" + (2*row) + "], flag_out[" + (2*row) + ":" + (2*row) + "], data_out[(`DATA_WIDTH*" + (2*row+1) + ")-1 -:`DATA_WIDTH]);\n";
            module +=   "        $display(\"" + (2*row+1) + "\\t%b %b %d\", valid_bit_out[" + (2*row+1) + ":" + (2*row+1) + "], flag_out[" + (2*row+1) + ":" + (2*row+1) + "], data_out[(`DATA_WIDTH*" + (2*row+2) + ")-1 -:`DATA_WIDTH]);\n";
        }

        module +=       "    endtask\n\n";

        // initial begin and clk
        module +=       "    initial begin\n" +
                        "        $display(\"START CRC_CORE_W_INPUT_OUTPUT_FIFOS testbench\");\n" +
                        "\n" +
                        "        clk = 1;\n" +
                        "        reset = 0;\n" +
                        "\n" +
                        "        `TICK\n" +
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
                        "        .data_in(data_in),\n" +
                        "        .flag_in(flag_in),\n" +
                        "        .valid_bit_in(valid_bit_in),\n" +
                        "        .config_select_in(config_select_in),\n" +
                        "\n" +
                        "        .data_out(data_out),\n" +
                        "        .flag_out(flag_out),\n" +
                        "        .valid_bit_out(valid_bit_out),\n" +
                        "        .config_select_out(config_select_out),\n" +
                        "\n" +
                        "        .input_fifo_full(input_fifo_full),\n" +
                        "        .output_fifo_full(output_fifo_full),\n" +
                        "        .output_fifo_read(output_fifo_read)\n" +
                        "    );\n\n";

        module += "endmodule\n";

        String file = header;
        file += module;

        return file;
    }
}

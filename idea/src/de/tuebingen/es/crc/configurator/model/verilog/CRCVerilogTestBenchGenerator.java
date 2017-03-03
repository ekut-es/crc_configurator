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

        String header = "/* -------------------------------------------------------------------------\n" +
                        " * This file was generated with the CRC Configurator on:\n" +
                        " * " + formatter.format(Calendar.getInstance().getTime()) + "\n" +
                        " * ------------------------------------------------------------------------- */\n\n" +
                        "`include \"preprocessor.v\"\n\n";

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

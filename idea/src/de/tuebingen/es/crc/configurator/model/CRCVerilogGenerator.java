package de.tuebingen.es.crc.configurator.model;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Konstantin (Konze) Lübeck on 27/02/2017.
 */
public class CRCVerilogGenerator {

    private CRC crc;
    private boolean fifosBetweenPes;

    CRCVerilogGenerator(CRC crc, boolean fifosBetweenPes) {
        this.crc = crc;
        this.fifosBetweenPes = fifosBetweenPes;
    }

    public String generate() {

        Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String header = "/* -------------------------------------------------------------------------\n" +
                        " * This file was generated with the CRC Configurator on:\n" +
                        " * " + formatter.format(Calendar.getInstance().getTime()) + "\n" +
                        " * ------------------------------------------------------------------------- */\n\n" +
                        "`include \"preprocessor.v\"\n\n";

        String module = "module CRC_CORE_W_INPUT_OUTPUT_FIFOS(\n" +
                        "    clk,\n" +
                        "    reset,\n" +
                        "    enable_config_read,\n" +
                        "    flag_exception,\n" +
                        "\n" +
                        "    config_in,\n" +
                        "    config_load_select,\n" +
                        "    config_select,\n" +
                        "\n" +
                        "    data_in,\n" +
                        "    flag_in,\n" +
                        "    valid_bit_in,\n" +
                        "    config_select_in,\n" +
                        "\n" +
                        "    data_out,\n" +
                        "    flag_out,\n" +
                        "    valid_bit_out,\n" +
                        "    config_select_out,\n" +
                        "\n" +
                        "    input_fifo_full,\n" +
                        "    output_fifo_full,\n" +
                        "    output_fifos_read\n" +
                        ");\n\n";

        module += "endmodule\n";

        String verilogCode = header + module;

        return verilogCode;
    }
}

package de.tuebingen.es.crc.configurator.model.verilog;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Konstantin (Konze) Lübeck on 02/03/2017.
 */
public class CRCVerilogQuestaSimScriptGenerator {

    private String verilogFileName;
    private String testBenchFileName;

    public CRCVerilogQuestaSimScriptGenerator(String verilogFileName, String testBenchFileName) {
        this.verilogFileName = verilogFileName;
        this.testBenchFileName = testBenchFileName;
    }

    public String generate() {

        Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String script = "# This file was generated with the CRC Configurator on:\n" +
                        "# " + formatter.format(Calendar.getInstance().getTime()) + "\n\n";

        script +=       "quit -sim\n" +
                        "\n" +
                        "# Create the library.\n" +
                        "if [file exists work] {\n" +
                        "   vdel -all\n" +
                        "}\n" +
                        "vlib work\n" +
                        "\n" +
                        "## in .bashrc\n" +
                        "## export DESIGN_WARE_VERILOG_DIR_PATH=\"/path/to/design/ware/verilog/files\"\n" +
                        "## export CRC_VERILOG_DIR_PATH=\"/path/to/design/ware/verilog/files\"\n" +
                        "\n" +
                        "vlog -novopt $env(DESIGN_WARE_VERILOG_DIR_PATH)/DW01_add.v\n" +
                        "vlog -novopt $env(DESIGN_WARE_VERILOG_DIR_PATH)/DW01_sub.v\n" +
                        "vlog -novopt $env(DESIGN_WARE_VERILOG_DIR_PATH)/DW02_mult.v\n" +
                        "vlog -novopt $env(DESIGN_WARE_VERILOG_DIR_PATH)/DW_div.v\n" +
                        "vlog -novopt $env(DESIGN_WARE_VERILOG_DIR_PATH)/DW01_cmp6.v\n" +
                        "vlog -novopt $env(DESIGN_WARE_VERILOG_DIR_PATH)/DW01_mux_any.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_umula.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_signed_fixed_point_add.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_signed_fixed_point_sub.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_signed_fixed_point_mul.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_signed_fixed_point_mula.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_signed_fixed_point_max_2.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/dsp48_signed_fixed_point_max_3.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/lut_8bit.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/fu.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/fu_input_ready.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/ext_input_ready_fu_consumer.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/ext_input_ready_data_in_consumer.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/valid_bit_data_in_consumer.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/data_out_reg.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/config_line.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/const_reg.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/pe.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/input_fifo.v\n" +
                        "vlog -novopt $env(CRC_VERILOG_DIR_PATH)/output_fifo.v\n" +
                        "vlog -novopt " + this.verilogFileName + "\n" +
                        "vlog -novopt " + this.testBenchFileName + "\n" +
                        "vopt CRC_CORE_W_INPUT_OUTPUT_FIFOS_tb -debugdb -o tb\n" +
                        "vsim tb -debugdb -novopt -l rtl.log -wlf rtl.wlf -assertdebug +notimingchecks -sv_seed random\n" +
                        "\n" +
                        "#run 1000";

        return script;
    }
}

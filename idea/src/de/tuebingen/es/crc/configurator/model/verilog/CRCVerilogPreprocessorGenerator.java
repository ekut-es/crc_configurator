package de.tuebingen.es.crc.configurator.model.verilog;

import de.tuebingen.es.crc.configurator.model.CRC;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by luebeck on 7/6/17.
 */
public class CRCVerilogPreprocessorGenerator {

    private int staticConfigLines;
    private int dynamicConfigLines;
    private int dataWidth;
    private int clockCycle;

    static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    public CRCVerilogPreprocessorGenerator(CRC crc, int clockCycle) {
        this.dataWidth = crc.getDataWidth();
        this.staticConfigLines = crc.getStaticConfigLines();
        this.dynamicConfigLines = crc.getDynamicConfigLines();
        this.clockCycle = clockCycle;
    }

    public String generate() {

        Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String preprocessor =
                "/* -------------------------------------------------------------------------\n" +
                " * This file was generated with the CRC Configurator on:\n" +
                " * " + formatter.format(Calendar.getInstance().getTime()) + "\n" +
                " * ------------------------------------------------------------------------- */\n\n";

        // clock cycle
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * Set timescale for simulation\n" +
                " * TICK is a whole clock cycle\n" +
                " * HALF_TICK = TICK/2\n" +
                " * WAIT time after a value can be read after a falling clock edge\n" +
                " * TICK_MINUS_WAIT = TICK-WAIT\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define TICK #" + clockCycle+ "\n" +
                "`define HALF_TICK #" + (clockCycle/2) + "\n" +
                "`define WAIT #" + ((clockCycle/2)-1) + "\n" +
                "`define TICK_MINUS_WAIT #" + (clockCycle - ((clockCycle/2)-1))+ "\n\n";

        // data width
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * DATA_WIDTH defines the number of data bits the architecure can process\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define DATA_WIDTH " + dataWidth + "\n" +
                "\n" +
                "/* -------------------------------------------------------------------------\n" +
                " * HALF_DATA_WIDTH is the half of DATA_WIDTH (it is used in the FU to prevent\n" +
                " * overflow errors during multiplication)\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define HALF_DATA_WIDTH " + (dataWidth/2) + "\n" +
                "\n" +
                "/* -------------------------------------------------------------------------\n" +
                " * FU_SHIFT_WIDTH defines the number of LSBs of data_in_1 of the FU which will\n" +
                " * be considered for shifting in the FU\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define FU_SHIFT_WIDTH " + (log2(dataWidth-1)+1) + "\n\n";

        // config width
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * CONFIG_WIDTH defines of how many bits the configuration of a PE consists of \n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define CONFIG_WIDTH 34\n\n";

        // static config lines
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * STATIC_CONFIG_LINES definess how many static config lines all PEs have\n" +
                " * ------------------------------------------------------------------------- */\n";
        if(staticConfigLines != 0) {
            preprocessor +=
                "`define STATIC_CONFIG\n" +
                "`define STATIC_CONFIG_LINES " + staticConfigLines + "\n\n";
        } else {
            preprocessor +=
                "//`define STATIC_CONFIG\n" +
                "`define STATIC_CONFIG_LINES 0\n\n";
        }

        // dynamic config lines
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * DYNAMIC_CONFIG_LINES defines how many dynamic config lines all PEs have\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define DYNAMIC_CONFIG_LINES " + dynamicConfigLines + "\n\n";

        // config select width
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * CONFIG_SELECT_WIDTH defines how many bits are needed to select\n" +
                " * a configuration in the PEs\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define CONFIG_SELECT_WIDTH " + (log2(staticConfigLines + dynamicConfigLines - 1)+1) + "\n\n";

        // fixed point math (TODO: make decimal places an editable parameter)
        preprocessor +=
               "/* -------------------------------------------------------------------------\n" +
               " * Parameters for fixed point math\n" +
               " * ------------------------------------------------------------------------- */\n" +
               "`define DECIMAL_PLACES 10\n" +
               "`define FIX_ONE {{`DATA_WIDTH-`DECIMAL_PLACES-1{1'b0}},1'b1,{`DECIMAL_PLACES{1'b0}}}\n" +
               "`define PRECISION (1.0/(2.0 ** `DECIMAL_PLACES))" + "\n\n";

        // reset active low
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * if RESET_ACTIVE_LOW is defined the CRC will be reseted when reset == 0.\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "//`define RESET_ACTIVE_LOW\n\n";

        // fu functions
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * FU_FUNCTION_* are shortcuts for FU functions of the PE configuration\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define FU_FUNCTION_ADD 5'b00000\n" +
                "`define FU_FUNCTION_SUB 5'b00001\n" +
                "`define FU_FUNCTION_MUL 5'b00010\n" +
                "`define FU_FUNCTION_DIV 5'b00011\n" +
                "`define FU_FUNCTION_AND 5'b00100\n" +
                "`define FU_FUNCTION_OR  5'b00101\n" +
                "`define FU_FUNCTION_XOR 5'b00110\n" +
                "`define FU_FUNCTION_NOT 5'b00111\n" +
                "`define FU_FUNCTION_SHIFT_LEFT  5'b01000\n" +
                "`define FU_FUNCTION_SHIFT_RIGHT 5'b01001\n" +
                "`define FU_FUNCTION_COMPARE_EQ  5'b01010\n" +
                "`define FU_FUNCTION_COMPARE_NEQ 5'b01011\n" +
                "`define FU_FUNCTION_COMPARE_LT  5'b01100\n" +
                "`define FU_FUNCTION_COMPARE_GT  5'b01101\n" +
                "`define FU_FUNCTION_COMPARE_LEQ 5'b01110\n" +
                "`define FU_FUNCTION_COMPARE_GEQ 5'b01111\n" +
                "`define FU_FUNCTION_MUX_0       5'b10000\n" +
                "`define FU_FUNCTION_MUX_1       5'b10001\n" +
                "`define FU_FUNCTION_DSP_ADD     5'b10010\n" +
                "`define FU_FUNCTION_DSP_MUL     5'b10011\n" +
                "`define FU_FUNCTION_DSP_MULA    5'b10100\n" +
                "`define FU_FUNCTION_LUT_8BIT    5'b10101\n" +
                "`define FU_FUNCTION_MAX_2       5'b10110\n" +
                "`define FU_FUNCTION_MAX_3       5'b10111\n" +
                "`define FU_FUNCTION_MIN         5'b11000\n" +
                "`define FU_FUNCTION_EXCEPTION_7  5'b11001\n" +
                "`define FU_FUNCTION_EXCEPTION_8  5'b11010\n" +
                "`define FU_FUNCTION_EXCEPTION_9  5'b11011\n" +
                "`define FU_FUNCTION_EXCEPTION_10 5'b11100\n" +
                "`define FU_FUNCTION_EXCEPTION_11 5'b11101\n" +
                "`define FU_FUNCTION_EXCEPTION_12 5'b11110\n" +
                "`define FU_FUNCTION_EXCEPTION_13 5'b11111\n\n";

        // inputs
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * INPUT_* / FU_INPUT_* are shortcuts for the multiplexer selections of the PE\n" +
                " * outputs and the FU inputs of the PE configuration.\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define INPUT_N_0       3'b000\n" +
                "`define INPUT_N_1       3'b001\n" +
                "`define INPUT_S_0       3'b010\n" +
                "`define INPUT_S_1       3'b011\n" +
                "`define INPUT_W_0       3'b100\n" +
                "`define INPUT_W_1       3'b101\n" +
                "`define INPUT_FU        3'b110\n" +
                "`define INPUT_CONST_REG 3'b110\n" +
                "`define INPUT_NONE      3'b111\n" +
                "\n" +
                "`define FU_INPUT_NONE_0 3'b110\n" +
                "`define FU_INPUT_NONE_1 3'b111\n\n";

        // outputs
        preprocessor +=
                "/* -------------------------------------------------------------------------\n" +
                " * OUTPUT_* are needed for the calculation of the valid bit outputs\n" +
                " * ------------------------------------------------------------------------- */\n" +
                "`define OUTPUT_N_0 3'b000\n" +
                "`define OUTPUT_N_1 3'b001\n" +
                "`define OUTPUT_E_0 3'b010\n" +
                "`define OUTPUT_E_1 3'b011\n" +
                "`define OUTPUT_S_0 3'b100\n" +
                "`define OUTPUT_S_1 3'b101\n" +
                "\n" +
                "`define INPUT_FU_0 3'b000\n" +
                "`define INPUT_FU_1 3'b001\n" +
                "`define INPUT_FU_MUX 3'b010\n";

        return preprocessor;
    }
}

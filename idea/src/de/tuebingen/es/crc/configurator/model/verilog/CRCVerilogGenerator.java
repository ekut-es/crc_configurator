package de.tuebingen.es.crc.configurator.model.verilog;

import de.tuebingen.es.crc.configurator.model.CRC;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 27/02/2017.
 */
public class CRCVerilogGenerator {

    private CRC crc;
    private boolean fifosBetweenPes;
    private ArrayList<VerilogWire> wires;
    private ArrayList<VerilogInputFifo> inputFifos;
    private ArrayList<VerilogPE> pes;
    private ArrayList<VerilogOutputFifo> outputFifos;

    public CRCVerilogGenerator(CRC crc, boolean fifosBetweenPes) {
        this.crc = crc;
        this.fifosBetweenPes = fifosBetweenPes;
        this.wires = new ArrayList<>();
        this.inputFifos = new ArrayList<>();
        this.pes = new ArrayList<>();
        this.outputFifos = new ArrayList<>();
    }

    private String getWireDeclarations() {
        String wireDeclarations = "";
        for (VerilogWire wire : wires) {
            wireDeclarations += "    " + wire.getDeclaration() + "\n";
        }
        return  wireDeclarations;
    }

    private String getInputFifoDeclarations() {
        String inputFifoDeclarations = "";
        for (VerilogInputFifo inputFifo : inputFifos) {
            inputFifoDeclarations += inputFifo.getDeclaration() + "\n";
        }
        return inputFifoDeclarations;
    }

    private String getPeDeclarations() {
        String peDeclarations = "";
        for(VerilogPE pe : pes) {
            peDeclarations += pe.getDeclaration() + "\n";
        }
        return peDeclarations;
    }

    private String getOutputFifoDeclarations() {
        String outputFifoDeclarations = "";
        for (VerilogOutputFifo outputFifo : outputFifos) {
            outputFifoDeclarations += outputFifo.getDeclaration() + "\n";
        }
        return outputFifoDeclarations;
    }

    private void connectPeDataFlagInNorth(VerilogPE pe, int row, int column) {
        if(!fifosBetweenPes) {
            // data input from the north 0
            pe.data_in_N_0 = "pe_" + (row-1) + "_" + column + "_data_out_S_0";
            pe.flag_in_N_0 = "pe_" + (row-1) + "_" + column + "_flag_out_S_0";
            pe.valid_bit_in_N_0 = "pe_" + (row-1) + "_" + column + "_valid_bit_out_S_0";
            pe.input_ready_N_0 = "pe_" + row + "_" + column + "_input_ready_N_0";

            // data input from the north 1
            pe.data_in_N_1 = "pe_" + (row-1) + "_" + column + "_data_out_S_1";
            pe.flag_in_N_1 = "pe_" + (row-1) + "_" + column + "_flag_out_S_1";
            pe.valid_bit_in_N_1 = "pe_" + (row-1) + "_" + column + "_valid_bit_out_S_1";
            pe.input_ready_N_1 = "pe_" + row + "_" + column + "_input_ready_N_1";
        }
    }

    private void connectPeDataFlagInSouth(VerilogPE pe, int row, int column) {
        if(!fifosBetweenPes) {
            // data input from the south 0
            VerilogWire peSouthDataOutN0 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_data_out_N_0", "`DATA_WIDTH");
            wires.add(peSouthDataOutN0);

            pe.data_in_S_0 = peSouthDataOutN0.name;

            VerilogWire peSouthFlagOutN0 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_flag_out_N_0");
            wires.add(peSouthFlagOutN0);

            pe.flag_in_S_0 = peSouthFlagOutN0.name;

            VerilogWire peSouthValidBitOutN0 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_valid_bit_out_N_0");
            wires.add(peSouthValidBitOutN0);

            pe.valid_bit_in_S_0 = peSouthValidBitOutN0.name;

            VerilogWire peInputReadyS0 = new VerilogWire("pe_" + row + "_" + column + "_input_ready_S_0");
            wires.add(peInputReadyS0);

            pe.input_ready_S_0 = peInputReadyS0.name;

            // data input from the south 1
            VerilogWire peSouthDataOutN1 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_data_out_N_1", "`DATA_WIDTH");
            wires.add(peSouthDataOutN1);

            pe.data_in_S_1 = peSouthDataOutN1.name;

            VerilogWire peSouthFlagOutN1 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_flag_out_N_1");
            wires.add(peSouthFlagOutN1);

            pe.flag_in_S_1 = peSouthFlagOutN1.name;

            VerilogWire peSouthValidBitOutN1 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_valid_bit_out_N_1");
            wires.add(peSouthValidBitOutN1);

            pe.valid_bit_in_S_1 = peSouthValidBitOutN1.name;

            VerilogWire peInputReadyS1 = new VerilogWire("pe_" + row + "_" + column + "_input_ready_S_1");
            wires.add(peInputReadyS1);

            pe.input_ready_S_1 = peInputReadyS1.name;
        }
    }

    private void connectPeDataFlagInWest(VerilogPE pe, int row, int column) {
        if(!fifosBetweenPes) {
            // data input from the west 0
            pe.data_in_W_0 = "pe_" + row + "_" + (column - 1) + "_data_out_E_0";
            pe.flag_in_W_0 = "pe_" + row + "_" + (column - 1) + "_flag_out_E_0";
            pe.valid_bit_in_W_0 = "pe_" + row + "_" + (column - 1) + "_valid_bit_out_E_0";
            pe.input_ready_W_0 = "pe_" + row + "_" + column + "_input_ready_W_0";

            // data input from the west 1
            pe.data_in_W_1 = "pe_" + row + "_" + (column - 1) + "_data_out_E_1";
            pe.flag_in_W_1 = "pe_" + row + "_" + (column - 1) + "_flag_out_E_1";
            pe.valid_bit_in_W_1 = "pe_" + row + "_" + (column - 1) + "_valid_bit_out_E_1";
            pe.input_ready_W_1 = "pe_" + row + "_" + column + "_input_ready_W_1";
        }
    }

    private void connectPeDataFlagInWestToFifos(VerilogPE pe, int row, int column) {
        pe.data_in_W_0 = "input_fifo_" + (2*row) + "_data_out";
        pe.flag_in_W_0 = "input_fifo_" + (2*row) + "_flag_out";
        pe.valid_bit_in_W_0 = "input_fifo_" + (2*row) + "_valid_bit_out";
        pe.input_ready_W_0 = "pe_" + row + "_" + column + "_input_ready_W_0";

        pe.data_in_W_1 = "input_fifo_" + (2*row+1) + "_data_out";
        pe.flag_in_W_1 = "input_fifo_" + (2*row+1) + "_flag_out";
        pe.valid_bit_in_W_1 = "input_fifo_" + (2*row+1) + "_valid_bit_out";
        pe.input_ready_W_1 = "pe_" + row + "_" + column + "_input_ready_W_1";
    }

    private void connectPeDataFlagOutNorth(VerilogPE pe, int row, int column) {
        // data output to the north 0
        pe.data_out_N_0 = "pe_" + row + "_" + column + "_data_out_N_0";
        pe.flag_out_N_0 = "pe_" + row + "_" + column + "_flag_out_N_0";
        pe.valid_bit_out_N_0 = "pe_" + row + "_" + column + "_valid_bit_out_N_0";
        pe.ext_input_ready_N_0 = "pe_" + (row-1) + "_" + column + "_input_ready_S_0";

        // data output to the north 1
        pe.data_out_N_1 = "pe_" + row + "_" + column + "_data_out_N_1";
        pe.flag_out_N_1 = "pe_" + row + "_" + column + "_flag_out_N_1";
        pe.valid_bit_out_N_1 = "pe_" + row + "_" + column + "_valid_bit_out_N_1";
        pe.ext_input_ready_N_1 = "pe_" + (row-1) + "_" + column + "_input_ready_S_1";
    }

    private void connectPeDataFlagOutEast(VerilogPE pe, int row, int column) {
        if(!fifosBetweenPes) {
            // data output to the east 0
            VerilogWire peDataOutE0 = new VerilogWire("pe_" + row + "_" + column + "_data_out_E_0", "`DATA_WIDTH");
            wires.add(peDataOutE0);

            pe.data_out_E_0 = peDataOutE0.name;

            VerilogWire peFlagOutE0 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_E_0");
            wires.add(peFlagOutE0);

            pe.flag_out_E_0 = peFlagOutE0.name;

            VerilogWire peValidBitOutE0 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_E_0");
            wires.add(peValidBitOutE0);

            pe.valid_bit_out_E_0 = peValidBitOutE0.name;

            VerilogWire peEastInputReadyW0 = new VerilogWire("pe_" + row + "_" + (column + 1) + "_input_ready_W_0");
            wires.add(peEastInputReadyW0);

            pe.ext_input_ready_E_0 = peEastInputReadyW0.name;


            // data output to the east 1
            VerilogWire peDataOutE1 = new VerilogWire("pe_" + row + "_" + column + "_data_out_E_1", "`DATA_WIDTH");
            wires.add(peDataOutE1);

            pe.data_out_E_1 = peDataOutE1.name;

            VerilogWire peFlagOutE1 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_E_1");
            wires.add(peFlagOutE1);

            pe.flag_out_E_1 = peFlagOutE1.name;

            VerilogWire peValidBitOutE1 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_E_1");
            wires.add(peValidBitOutE1);

            pe.valid_bit_out_E_1 = peValidBitOutE1.name;

            VerilogWire peEastInputReadyW1 = new VerilogWire("pe_" + row + "_" + (column + 1) + "_input_ready_W_1");
            wires.add(peEastInputReadyW1);

            pe.ext_input_ready_E_1 = peEastInputReadyW1.name;
        }
    }

    private void connectPeDataFlagOutEastToFifos(VerilogPE pe, int row, int column) {
        // data output to the east 0
        VerilogWire peDataOutE0 = new VerilogWire("pe_" + row + "_" + column + "_data_out_E_0", "`DATA_WIDTH");
        wires.add(peDataOutE0);

        pe.data_out_E_0 = peDataOutE0.name;

        VerilogWire peFlagOutE0 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_E_0");
        wires.add(peFlagOutE0);

        pe.flag_out_E_0 = peFlagOutE0.name;

        VerilogWire peValidBitOutE0 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_E_0");
        wires.add(peValidBitOutE0);

        pe.valid_bit_out_E_0 = peValidBitOutE0.name;

        VerilogWire outputFifo0Full = new VerilogWire("output_fifo_" + (row*2) + "_full");
        wires.add(outputFifo0Full);

        pe.ext_input_ready_E_0 = "!" + outputFifo0Full.name;

        // data output to the east 1
        VerilogWire peDataOutE1 = new VerilogWire("pe_" + row + "_" + column + "_data_out_E_1", "`DATA_WIDTH");
        wires.add(peDataOutE1);

        pe.data_out_E_1 = peDataOutE1.name;

        VerilogWire peFlagOutE1 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_E_1");
        wires.add(peFlagOutE1);

        pe.flag_out_E_1 = peFlagOutE1.name;

        VerilogWire peValidBitOutE1 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_E_1");
        wires.add(peValidBitOutE1);

        pe.valid_bit_out_E_1 = peValidBitOutE1.name;

        VerilogWire outputFifo1Full = new VerilogWire("output_fifo_" + (row*2+1) + "_full");
        wires.add(outputFifo1Full);

        pe.ext_input_ready_E_1 = "!" + outputFifo1Full.name;
    }

    private void connectPeDataFlagOutSouth(VerilogPE pe, int row, int column) {
        if(!fifosBetweenPes) {
            // data output to the south 0
            VerilogWire peDataOutS0 = new VerilogWire("pe_" + row + "_" + column + "_data_out_S_0", "`DATA_WIDTH");
            wires.add(peDataOutS0);

            pe.data_out_S_0 = peDataOutS0.name;

            VerilogWire peFlagOutS0 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_S_0");
            wires.add(peFlagOutS0);

            pe.flag_out_S_0 = peFlagOutS0.name;

            VerilogWire peValidBitOutS0 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_S_0");
            wires.add(peValidBitOutS0);

            pe.valid_bit_out_S_0 = peValidBitOutS0.name;

            VerilogWire peSouthInputReadyN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_input_ready_N_0");
            wires.add(peSouthInputReadyN0);

            pe.ext_input_ready_S_0 = peSouthInputReadyN0.name;

            // data output to the south 1
            VerilogWire peDataOutS1 = new VerilogWire("pe_" + row + "_" + column + "_data_out_S_1", "`DATA_WIDTH");
            wires.add(peDataOutS1);

            pe.data_out_S_1 = peDataOutS1.name;

            VerilogWire peFlagOutS1 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_S_1");
            wires.add(peFlagOutS1);

            pe.flag_out_S_1 = peFlagOutS1.name;

            VerilogWire peValidBitOutS1 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_S_1");
            wires.add(peValidBitOutS1);

            pe.valid_bit_out_S_1 = peValidBitOutS1.name;

            VerilogWire peSouthInputReadyN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_input_ready_N_1");
            wires.add(peSouthInputReadyN1);

            pe.ext_input_ready_S_1 = peSouthInputReadyN1.name;

        }
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
                        "    output_fifo_read\n" +
                        ");\n\n";

        String inputs = "/* -------------------------------------------------------------------------\n" +
                        " *                                   INPUTS\n" +
                        " * ------------------------------------------------------------------------- */\n" +
                        "    input wire clk;\n" +
                        "    input wire reset;\n" +
                        "    input wire [" + (crc.getRows()*crc.getColumns()) + "-1:0] enable_config_read;\n" +
                        "\n" +
                        "    input wire [(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_WIDTH)-1:0] config_in;\n" +
                        "    input wire [(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH)-1:0] config_load_select;\n" +
                        "    input wire [(" + (crc.getRows()*crc.getColumns()) + "*`CONFIG_SELECT_WIDTH)-1:0] config_select;\n" +
                        "\n" +
                        "    input wire [(" + crc.getRows() + "*`DATA_WIDTH*2)-1:0] data_in;\n" +
                        "    input wire [(" + crc.getRows() + "*2)-1:0] flag_in;\n" +
                        "    input wire [(" + crc.getRows() + "*2)-1:0] valid_bit_in;\n" +
                        "    input wire [(" + crc.getRows() + "*`CONFIG_SELECT_WIDTH*2)-1:0] config_select_in;\n" +
                        "\n" +
                        "    input wire [(" + crc.getRows() + "*2)-1:0] output_fifo_read;\n\n";

        String outputs = "/* -------------------------------------------------------------------------\n" +
                        " *                                   OUTPUTS\n" +
                        " * ------------------------------------------------------------------------- */\n" +
                        "    output wire [(" + crc.getRows() + "*`DATA_WIDTH*2)-1:0] data_out;\n" +
                        "    output wire [(" + crc.getRows() + "*2)-1:0] flag_out;\n" +
                        "    output wire [(" + crc.getRows() + "*2)-1:0] valid_bit_out;\n" +
                        "    output wire [(" + crc.getRows() + "*`CONFIG_SELECT_WIDTH*2)-1:0] config_select_out;\n" +
                        "\n" +
                        "    output wire [" + (crc.getRows()*crc.getColumns()) + "-1:0] flag_exception;\n" +
                        "\n" +
                        "    output wire [(" + crc.getRows() + "*2)-1:0] input_fifo_full;\n" +
                        "    output wire [(" + crc.getRows() + "*2)-1:0] output_fifo_full;\n\n";


        // generate input FIFOs
        for(int row = 0; row < crc.getRows(); row++) {

            // input FIFO for data_in_0
            int inputFifoNumber = 2*row;

            VerilogInputFifo inputFifo0 = new VerilogInputFifo("input_fifo_" + inputFifoNumber);
            inputFifo0.data_in = "data_in[`DATA_WIDTH*" + (inputFifoNumber+1) + "-1:`DATA_WIDTH*" + inputFifoNumber + "]";
            inputFifo0.flag_in = "flag_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo0.valid_bit_in = "valid_bit_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo0.config_select_in = "config_select_in[`CONFIG_SELECT_WIDTH*" + (inputFifoNumber+1) + "-1:`CONFIG_SELECT_WIDTH*" + inputFifoNumber + "]";

            VerilogWire peWestmostColumnInputReadyW0 = new VerilogWire("pe_" + row + "_0_input_ready_W_0");
            wires.add(peWestmostColumnInputReadyW0);

            inputFifo0.ext_input_ready = peWestmostColumnInputReadyW0.name;

            VerilogWire peWestmostColumnConfigSelectOutW = new VerilogWire("pe_" + row + "_0_config_select_out_W", "`CONFIG_SELECT_WIDTH");
            wires.add(peWestmostColumnConfigSelectOutW);

            inputFifo0.ext_config_select_in = peWestmostColumnConfigSelectOutW.name;

            VerilogWire inputFifo0DataOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_data_out", "`DATA_WIDTH");
            wires.add(inputFifo0DataOut);

            inputFifo0.data_out = inputFifo0DataOut.name;

            VerilogWire inputFifo0FlagOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_flag_out");
            wires.add(inputFifo0FlagOut);

            inputFifo0.flag_out = inputFifo0FlagOut.name;

            VerilogWire inputFifo0ValidBitOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_valid_bit_out");
            wires.add(inputFifo0ValidBitOut);

            inputFifo0.valid_bit_out = inputFifo0ValidBitOut.name;

            VerilogWire inputFifo0ConfigSelectOut = new  VerilogWire("input_fifo_" + inputFifoNumber + "_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(inputFifo0ConfigSelectOut);

            inputFifo0.config_select_out = inputFifo0ConfigSelectOut.name;

            inputFifo0.full = "input_fifo_full[" + inputFifoNumber + ":" + inputFifoNumber + "]";

            inputFifos.add(inputFifo0);

            // input FIFO for data_in_1
            inputFifoNumber = 2*row+1;

            VerilogInputFifo inputFifo1 = new VerilogInputFifo("input_fifo_" + inputFifoNumber);
            inputFifo1.data_in = "data_in[`DATA_WIDTH*" + (inputFifoNumber+1) + "-1:`DATA_WIDTH*" + inputFifoNumber + "]";
            inputFifo1.flag_in = "flag_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo1.valid_bit_in = "valid_bit_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo1.config_select_in = "config_select_in[`CONFIG_SELECT_WIDTH*" + (inputFifoNumber+1) + "-1:`CONFIG_SELECT_WIDTH*" + inputFifoNumber + "]";

            VerilogWire peWestmostColumnInputReadyW1 = new VerilogWire("pe_" + row + "_0_input_ready_W_1");
            wires.add(peWestmostColumnInputReadyW1);

            inputFifo1.ext_input_ready = peWestmostColumnInputReadyW1.name;

            inputFifo1.ext_config_select_in = peWestmostColumnConfigSelectOutW.name;

            VerilogWire inputFifo1DataOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_data_out", "`DATA_WIDTH");
            wires.add(inputFifo1DataOut);

            inputFifo1.data_out = inputFifo1DataOut.name;

            VerilogWire inputFifo1FlagOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_flag_out");
            wires.add(inputFifo1FlagOut);

            inputFifo1.flag_out = inputFifo1FlagOut.name;

            VerilogWire inputFifo1ValidBitOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_valid_bit_out");
            wires.add(inputFifo1ValidBitOut);

            inputFifo1.valid_bit_out = inputFifo1ValidBitOut.name;

            VerilogWire inputFifo1ConfigSelectOut = new  VerilogWire("input_fifo_" + inputFifoNumber + "_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(inputFifo1ConfigSelectOut);

            inputFifo1.config_select_out = inputFifo1ConfigSelectOut.name;

            inputFifo1.full = "input_fifo_full[" + inputFifoNumber + ":" + inputFifoNumber + "]";

            inputFifos.add(inputFifo1);
        }

        for(int row = 0; row < crc.getRows(); row++) {
            for(int column = 0; column < crc.getColumns(); column++) {

                VerilogPE pe = new VerilogPE("pe_" + row + "_" + column);

                LinkedHashMap<String, Boolean> fuFunctions = crc.getFu(row, column).getFunctions();

                pe.op_add = fuFunctions.get("add");
                pe.op_sub = fuFunctions.get("sub");
                pe.op_mul = fuFunctions.get("mul");
                pe.op_div = fuFunctions.get("div");
                pe.op_and = fuFunctions.get("and");
                pe.op_or = fuFunctions.get("or");
                pe.op_xor = fuFunctions.get("xor");
                pe.op_not = fuFunctions.get("not");
                pe.op_shift_left = fuFunctions.get("shift_left");
                pe.op_shift_right = fuFunctions.get("shift_right");
                pe.op_compare = fuFunctions.get("compare");
                pe.op_multiplex = fuFunctions.get("multiplex");

                pe.static_config_content = "" + crc.getPeStaticConfigParameterBits(row, column).length() + "'b" + crc.getPeStaticConfigParameterBits(row, column);

                pe.enable_config_read = "enable_config_read[" + (crc.getColumns()*row+column) + ":" + (crc.getColumns()*row+column) + "]";
                pe.flag_exception = "flag_exception[" + (crc.getColumns()*row+column) + ":" + (crc.getColumns()*row+column)+ "]";

                pe.config_in = "config_in[`CONFIG_WIDTH*" + ((row*crc.getColumns())+column+1) + "-1:`CONFIG_WIDTH*" + ((row*crc.getColumns())+column) + "]";
                pe.config_load_select = "config_load_select[`CONFIG_SELECT_WIDTH*" + ((row*crc.getColumns())+column+1) + "-1:`CONFIG_SELECT_WIDTH*" + ((row*crc.getColumns())+column) + "]";
                pe.config_select = "config_select[`CONFIG_SELECT_WIDTH*" + ((row*crc.getColumns())+column+1) + "-1:`CONFIG_SELECT_WIDTH*" + ((row*crc.getColumns())+column) + "]";

                // northwest corner PE
                if(row == 0 && column == 0) {
                    pe.regs_in_north = false;
                    pe.regs_in_south = true;

                    if(!fifosBetweenPes) {

                        // config select ins
                        VerilogWire peEastConfigSelectOutW = new VerilogWire("pe_" + row + "_" + (column + 1) + "_config_select_out_W", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW);

                        pe.config_select_in_E = peEastConfigSelectOutW.name;

                        VerilogWire peSouthConfigSelectOutN = new VerilogWire("pe_" + (row + 1) + "_" + column + "_config_select_out_N", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN);

                        pe.config_select_in_S = peSouthConfigSelectOutN.name;

                        pe.config_select_in_W = "input_fifo_0_config_select_out";

                        // config select outs
                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        VerilogWire peConfigSelectOutS = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS);

                        pe.config_select_out_S = peConfigSelectOutS.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data input from the south
                    this.connectPeDataFlagInSouth(pe, row, column);

                    // data input from the west (input fifos)
                    this.connectPeDataFlagInWestToFifos(pe, row, column);

                    // data output to the east
                    this.connectPeDataFlagOutEast(pe, row, column);

                    // data output to the south
                    this.connectPeDataFlagOutSouth(pe, row, column);
                }

                // nortmost row PEs
                else if(row == 0 && column != 0 && column != crc.getColumns()-1) {
                    pe.regs_in_north = false;
                    pe.regs_in_south = true;

                    if(!fifosBetweenPes) {
                        // config select ins
                        VerilogWire peEastConfigSelectOutW = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW);

                        pe.config_select_in_E = peEastConfigSelectOutW.name;

                        VerilogWire peSouthConfigSelectOutN = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN);

                        pe.config_select_in_S = peSouthConfigSelectOutN.name;

                        pe.config_select_in_W = "pe_" + row + "_" + (column-1) + "_config_select_out_E";

                        // config select outs
                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        VerilogWire peConfigSelectOutS = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS);

                        pe.config_select_out_S = peConfigSelectOutS.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data input from the south
                    this.connectPeDataFlagInSouth(pe, row, column);

                    // data input from the west
                    this.connectPeDataFlagInWest(pe, row, column);

                    // data output to the east
                    this.connectPeDataFlagOutEast(pe, row, column);

                    // data output to the south
                    this. connectPeDataFlagOutSouth(pe, row, column);

                }

                // northeast corner PE
                else if(row == 0 && column == crc.getColumns()-1) {
                    pe.regs_in_north = false;
                    pe.regs_in_south = true;

                    if(!fifosBetweenPes) {

                        // config select ins
                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_in_E = peConfigSelectOutE.name;

                        VerilogWire peSouthConfigSelectOutN = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN);

                        pe.config_select_in_S = peSouthConfigSelectOutN.name;

                        pe.config_select_in_W = "pe_" + row + "_" + (column-1) + "_config_select_out_E";

                        // config select outs
                        pe.config_select_out_E = peConfigSelectOutE.name;

                        VerilogWire peConfigSelectOutS = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS);

                        pe.config_select_out_S = peConfigSelectOutS.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data input from the south
                    this.connectPeDataFlagInSouth(pe, row, column);

                    // data input from the west
                    this.connectPeDataFlagInWest(pe, row, column);

                    // data output to the east to FIFOs
                    this.connectPeDataFlagOutEastToFifos(pe, row, column);

                    // data output to the south
                    this.connectPeDataFlagOutSouth(pe, row, column);
                }

                // westmost column PEs
                else if(row != 0 && row != crc.getRows()-1 && column == 0) {
                    if(!fifosBetweenPes) {
                        // config select ins
                        pe.config_select_in_N = "pe_" + (row-1) + "_" + column + "_config_select_out_S";

                        VerilogWire peEastConfigSelectOutW = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW);

                        pe.config_select_in_E = peEastConfigSelectOutW.name;

                        VerilogWire peSouthConfigSelectOutN = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN);

                        pe.config_select_in_S = peSouthConfigSelectOutN.name;

                        pe.config_select_in_W = "input_fifo_" + (2*row) + "_config_select_out";

                        // config select outs
                        pe.config_select_out_N = "pe_" + row + "_" + column + "_config_select_out_N";

                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        VerilogWire peConfigSelectOutS = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS);

                        pe.config_select_out_S = peConfigSelectOutS.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data input from the north
                    this.connectPeDataFlagInNorth(pe, row, column);

                    // data input from the south
                    this.connectPeDataFlagInSouth(pe, row, column);

                    // data input from the west
                    this.connectPeDataFlagInWestToFifos(pe, row, column);

                    // data output to the north
                    this.connectPeDataFlagOutNorth(pe, row, column);

                    // data output to the east
                    this.connectPeDataFlagOutEast(pe, row, column);

                    // data output to the south
                    this. connectPeDataFlagOutSouth(pe, row, column);

                }

                // eastmost column PEs
                else if(row != 0 && row != crc.getRows()-1 && column == crc.getColumns()-1) {
                    if(!fifosBetweenPes) {
                        // config select ins
                        pe.config_select_in_N = "pe_" + (row-1) + "_" + column + "_config_select_out_S";

                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_in_E = peConfigSelectOutE.name;

                        VerilogWire peSouthConfigSelectOutN = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN);

                        pe.config_select_in_S = peSouthConfigSelectOutN.name;

                        pe.config_select_in_W = "pe_" + row + "_" + (column-1) + "_config_select_out_E";

                        // config select outs
                        pe.config_select_out_N = "pe_" + row + "_" + column + "_config_select_out_N";

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        VerilogWire peConfigSelectOutS = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS);

                        pe.config_select_out_S = peConfigSelectOutS.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data in from the north
                    this.connectPeDataFlagInNorth(pe, row, column);

                    // data in from the south
                    this.connectPeDataFlagInSouth(pe, row, column);

                    // data in from the west
                    this.connectPeDataFlagInWest(pe, row, column);

                    // data out to the north
                    this.connectPeDataFlagOutNorth(pe, row, column);

                    // data out to the east to FIFOs
                    this.connectPeDataFlagOutEastToFifos(pe, row, column);

                    // data out to the south
                    this.connectPeDataFlagOutSouth(pe, row, column);
                }

                // southwest corner PE
                else if(row == crc.getRows()-1 && column == 0) {
                    if(!fifosBetweenPes) {
                        // config select ins
                        pe.config_select_in_N = "pe_" + (row-1) + "_" + column + "_config_select_out_S";

                        VerilogWire peEastConfigSelectOutWest = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutWest);

                        pe.config_select_in_E = peEastConfigSelectOutWest.name;

                        pe.config_select_in_W = "input_fifo_" + (row*2) + "_config_select_out";

                        // config select outs
                        pe.config_select_out_N = "pe_" + row + "_" + column + "_config_select_out_N";

                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data in from the north
                    this.connectPeDataFlagInNorth(pe, row, column);

                    // data in from the west
                    this.connectPeDataFlagInWestToFifos(pe, row, column);

                    // data out to the north
                    this.connectPeDataFlagOutNorth(pe, row, column);

                    // data out to the east
                    this.connectPeDataFlagOutEast(pe, row, column);
                }

                // southmost row PEs
                else if(row == crc.getRows()-1 && column != 0 && column != crc.getColumns()-1) {
                    if(!fifosBetweenPes) {
                        // config select ins
                        pe.config_select_in_N = "pe_" + (row-1) + "_" + column + "_config_select_out_S";

                        VerilogWire peEastConfigSelectOutWest = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutWest);

                        pe.config_select_in_E = peEastConfigSelectOutWest.name;

                        pe.config_select_in_W = "pe_" + row + "_" + (column-1) + "_config_select_out_E";

                        // config select outs
                        pe.config_select_out_N = "pe_" + row + "_" + column + "_config_select_out_N";

                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data in from the north
                    this.connectPeDataFlagInNorth(pe, row, column);

                    // data in from the west
                    this.connectPeDataFlagInWest(pe, row, column);

                    // data out to the north
                    this.connectPeDataFlagOutNorth(pe, row, column);

                    // data out to the east
                    this.connectPeDataFlagOutEast(pe, row, column);
                }

                // southeast corner PE
                else if(row == crc.getRows()-1 && column == crc.getColumns()-1) {
                    if(!fifosBetweenPes) {
                        // config select ins
                        pe.config_select_in_N = "pe_" + (row-1) + "_" + column + "_config_select_out_S";

                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_in_E = peConfigSelectOutE.name;

                        pe.config_select_in_W = "pe_" + row + "_" + (column-1) + "_config_select_out_E";

                        // config select outs
                        pe.config_select_out_N = "pe_" + row + "_" + column + "_config_select_out_N";


                        pe.config_select_out_E = peConfigSelectOutE.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data in from the north
                    this.connectPeDataFlagInNorth(pe, row, column);

                    // data in from the west
                    this.connectPeDataFlagInWest(pe, row, column);

                    // data out to the north
                    this.connectPeDataFlagOutNorth(pe, row, column);

                    // data out to the east
                    this.connectPeDataFlagOutEastToFifos(pe, row, column);
                }

                // center PE
                else {
                    if(!fifosBetweenPes) {
                        // config select ins
                        pe.config_select_in_N = "pe_" + (row-1) + "_" + column + "_config_select_out_S";

                        VerilogWire peEastConfigSelectOutW = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW);

                        pe.config_select_in_E = peEastConfigSelectOutW.name;

                        VerilogWire peSouthConfigSelectN = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectN);

                        pe.config_select_in_S = peSouthConfigSelectN.name;

                        pe.config_select_in_W = "pe_" + row + "_" + (column-1) + "_config_select_out_E";

                        // config select outs
                        pe.config_select_out_N = "pe_" + row + "_" + column + "_config_select_out_N";

                        VerilogWire peConfigSelectOutE = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE);

                        pe.config_select_out_E = peConfigSelectOutE.name;

                        VerilogWire peConfigSelectOutS = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS);

                        pe.config_select_out_S = peConfigSelectOutS.name;

                        pe.config_select_out_W = "pe_" + row + "_" + column + "_config_select_out_W";
                    }

                    // data in from the north
                    this.connectPeDataFlagInNorth(pe, row, column);

                    // data in from the south
                    this.connectPeDataFlagInSouth(pe, row, column);

                    // data in from the west
                    this.connectPeDataFlagInWest(pe, row, column);

                    // data out to the north
                    this.connectPeDataFlagOutNorth(pe, row, column);

                    // data out to the east
                    this.connectPeDataFlagOutEast(pe, row, column);

                    // data out to the south
                    this.connectPeDataFlagOutSouth(pe, row, column);
                }

                pes.add(pe);
            }
        }

        // generate output FIFOs
        for(int row = 0; row < crc.getRows(); row++) {

            // output FIFO for data_out_0
            int outputFifoNumber = 2 * row;

            VerilogOutputFifo outputFifo0 = new VerilogOutputFifo("output_fifo_" + outputFifoNumber);
            outputFifo0.data_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_data_out_E_0";
            outputFifo0.flag_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_flag_out_E_0";
            outputFifo0.valid_bit_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_valid_bit_out_E_0";
            outputFifo0.config_select_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_config_select_out_E";

            outputFifo0.read = "output_fifo_read[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifo0.data_out = "data_out[(" + (outputFifoNumber+1) + "*`DATA_WIDTH)-1:" + outputFifoNumber + "*`DATA_WIDTH]";
            outputFifo0.flag_out = "flag_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo0.valid_bit_out = "valid_bit_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo0.config_select_out = "config_select_out[(" + (outputFifoNumber+1) + "*`CONFIG_SELECT_WIDTH)-1:" + outputFifoNumber + "*`CONFIG_SELECT_WIDTH]";

            outputFifo0.full = "output_fifo_full[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifos.add(outputFifo0);

            // output FIFO for data_out_0
            outputFifoNumber = 2 * row + 1;

            VerilogOutputFifo outputFifo1 = new VerilogOutputFifo("output_fifo_" + outputFifoNumber);
            outputFifo1.data_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_data_out_E_1";
            outputFifo1.flag_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_flag_out_E_1";
            outputFifo1.valid_bit_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_valid_bit_out_E_1";
            outputFifo1.config_select_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_config_select_out_E";

            outputFifo1.read = "output_fifo_read[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifo1.data_out = "data_out[(" + (outputFifoNumber+1) + "*`DATA_WIDTH)-1:" + outputFifoNumber + "*`DATA_WIDTH]";
            outputFifo1.flag_out = "flag_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo1.valid_bit_out = "valid_bit_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo1.config_select_out = "config_select_out[(" + (outputFifoNumber+1) + "*`CONFIG_SELECT_WIDTH)-1:" + outputFifoNumber + "*`CONFIG_SELECT_WIDTH]";

            outputFifo1.full = "output_fifo_full[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifos.add(outputFifo1);
        }

        module += inputs;
        module += outputs;

        module += "/* -------------------------------------------------------------------------\n" +
                " *                                  INTERNAL\n" +
                " * ------------------------------------------------------------------------- */\n";
        module += this.getWireDeclarations();
        module += "\n\n";
        module += this.getInputFifoDeclarations();
        module += "\n\n";
        module += this.getPeDeclarations();
        module += "\n\n";
        module += this.getOutputFifoDeclarations();

        module += "endmodule\n";

        String verilogCode = header + module;

        return verilogCode;
    }
}

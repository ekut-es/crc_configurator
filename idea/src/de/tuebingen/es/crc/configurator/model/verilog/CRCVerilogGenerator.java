package de.tuebingen.es.crc.configurator.model.verilog;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.FU;

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
    private int interPeFifoLength;
    private int inputFifoLength;
    private int outputFifoLength;
    private ArrayList<VerilogWire> wires;
    private ArrayList<VerilogInputFifo> inputFifosWest;
    private ArrayList<VerilogPE> pes;
    private ArrayList<VerilogOutputFifo> outputFifosEast;
    private ArrayList<VerilogInputFifo> interPeFifos;

    public CRCVerilogGenerator(CRC crc, boolean fifosBetweenPes, int interPeFifoLength, int inputFifoLength, int outputFifoLength) {
        this.crc = crc;

        this.fifosBetweenPes = fifosBetweenPes;
        this.interPeFifoLength = interPeFifoLength;
        this.inputFifoLength = inputFifoLength;
        this.outputFifoLength = outputFifoLength;

        this.wires = new ArrayList<>();
        this.inputFifosWest = new ArrayList<>();
        this.pes = new ArrayList<>();
        this.outputFifosEast = new ArrayList<>();
        this.interPeFifos = new ArrayList<>();
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
        for (VerilogInputFifo inputFifo : inputFifosWest) {
            inputFifo.length = this.inputFifoLength;
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

    private String getInterPeFifoDeclarations() {
        String interPeFifoDeclarations = "";
        for (VerilogInputFifo interPeFifo : interPeFifos) {
            interPeFifo.length = this.interPeFifoLength;
            interPeFifoDeclarations += interPeFifo.getDeclaration() + "\n";
        }
        return interPeFifoDeclarations;
    }

    private String getOutputFifoDeclarations() {
        String outputFifoDeclarations = "";
        for (VerilogOutputFifo outputFifo : outputFifosEast) {
            outputFifo.length = this.outputFifoLength;
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
        } else {
            // data input from the north 0
            pe.data_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_data_out";
            pe.flag_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_flag_out";
            pe.valid_bit_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_valid_bit_out";
            pe.input_ready_N_0 = "pe_" + row + "_" + column + "_input_ready_N_0";

            // data input from the north 1
            pe.data_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_data_out";
            pe.flag_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_flag_out";
            pe.valid_bit_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_valid_bit_out";
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
        } else {
            // data in from the south 0
            VerilogInputFifo fifoPeSouthN0 = new VerilogInputFifo("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0");

            VerilogWire peSouthDataOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_data_out_N_0", "`DATA_WIDTH");
            wires.add(peSouthDataOutN0);

            fifoPeSouthN0.data_in = peSouthDataOutN0.name;

            VerilogWire peSouthFlagOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_flag_out_N_0");
            wires.add(peSouthFlagOutN0);

            fifoPeSouthN0.flag_in = peSouthFlagOutN0.name;

            VerilogWire peSouthValidBitOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_valid_bit_out_N_0");
            wires.add(peSouthValidBitOutN0);

            fifoPeSouthN0.valid_bit_in = peSouthValidBitOutN0.name;

            fifoPeSouthN0.config_select_in = "pe_" + (row+1) + "_" + column + "_config_select_out_N_0";

            VerilogWire peInputReadyS0 = new VerilogWire("pe_" + row + "_" + column + "_input_ready_S_0");
            wires.add(peInputReadyS0);

            fifoPeSouthN0.ext_input_ready = peInputReadyS0.name;

            fifoPeSouthN0.ext_config_select_in = "pe_" + row + "_" + column + "_config_select_out_S_0";

            VerilogWire fifoPeSouthN0DataOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_data_out", "`DATA_WIDTH");
            wires.add(fifoPeSouthN0DataOut);

            fifoPeSouthN0.data_out = fifoPeSouthN0DataOut.name;

            VerilogWire fifoPeSouthN0FlagOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_flag_out");
            wires.add(fifoPeSouthN0FlagOut);

            fifoPeSouthN0.flag_out = fifoPeSouthN0FlagOut.name;

            VerilogWire fifoPeSouthN0ValidBitOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_valid_bit_out");
            wires.add(fifoPeSouthN0ValidBitOut);

            fifoPeSouthN0.valid_bit_out = fifoPeSouthN0ValidBitOut.name;

            fifoPeSouthN0.config_select_out = "fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out";

            VerilogWire fifoPeSouthN0Full = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_full");
            wires.add(fifoPeSouthN0Full);

            fifoPeSouthN0.full = fifoPeSouthN0Full.name;

            interPeFifos.add(fifoPeSouthN0);

            pe.data_in_S_0 = fifoPeSouthN0DataOut.name;
            pe.flag_in_S_0 = fifoPeSouthN0FlagOut.name;
            pe.valid_bit_in_S_0 = fifoPeSouthN0ValidBitOut.name;
            pe.input_ready_S_0 = peInputReadyS0.name;

            // data in from the south 1
            VerilogInputFifo fifoPeSouthN1 = new VerilogInputFifo("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1");

            VerilogWire peSouthDataOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_data_out_N_1", "`DATA_WIDTH");
            wires.add(peSouthDataOutN1);

            fifoPeSouthN1.data_in = peSouthDataOutN1.name;

            VerilogWire peSouthFlagOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_flag_out_N_1");
            wires.add(peSouthFlagOutN1);

            fifoPeSouthN1.flag_in = peSouthFlagOutN1.name;

            VerilogWire peSouthValidBitOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_valid_bit_out_N_1");
            wires.add(peSouthValidBitOutN1);

            fifoPeSouthN1.valid_bit_in = peSouthValidBitOutN1.name;

            fifoPeSouthN1.config_select_in = "pe_" + (row+1) + "_" + column + "_config_select_out_N_1";

            VerilogWire peInputReadyS1 = new VerilogWire("pe_" + row + "_" + column + "_input_ready_S_1");
            wires.add(peInputReadyS1);

            fifoPeSouthN1.ext_input_ready = peInputReadyS1.name;

            fifoPeSouthN1.ext_config_select_in = "pe_" + row + "_" + column + "_config_select_out_S_1";

            VerilogWire fifoPeSouthN1DataOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_data_out", "`DATA_WIDTH");
            wires.add(fifoPeSouthN1DataOut);

            fifoPeSouthN1.data_out = fifoPeSouthN1DataOut.name;

            VerilogWire fifoPeSouthN1FlagOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_flag_out");
            wires.add(fifoPeSouthN1FlagOut);

            fifoPeSouthN1.flag_out = fifoPeSouthN1FlagOut.name;

            VerilogWire fifoPeSouthN1ValidBitOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_valid_bit_out");
            wires.add(fifoPeSouthN1ValidBitOut);

            fifoPeSouthN1.valid_bit_out = fifoPeSouthN1ValidBitOut.name;

            fifoPeSouthN1.config_select_out = "fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out";

            VerilogWire fifoPeSouthN1Full = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_full");
            wires.add(fifoPeSouthN1Full);

            fifoPeSouthN1.full = fifoPeSouthN1Full.name;

            interPeFifos.add(fifoPeSouthN1);

            pe.data_in_S_1 = fifoPeSouthN1DataOut.name;
            pe.flag_in_S_1 = fifoPeSouthN1FlagOut.name;
            pe.valid_bit_in_S_1 = fifoPeSouthN1ValidBitOut.name;
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
        } else {
            // data input from the west 0
            pe.data_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_data_out";
            pe.flag_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_flag_out";
            pe.valid_bit_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_valid_bit_out";
            pe.input_ready_W_0 = "pe_" + row + "_" + column + "_input_ready_W_0";

            // data input from the west 1
            pe.data_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_data_out";
            pe.flag_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_flag_out";
            pe.valid_bit_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_valid_bit_out";
            pe.input_ready_W_1 = "pe_" + row + "_" + column + "_input_ready_W_1";
        }
    }

    private void connectPeDataFlagInWestToFifos(VerilogPE pe, int row, int column) {
        pe.data_in_W_0 = "input_fifo_W_" + (2*row) + "_data_out";
        pe.flag_in_W_0 = "input_fifo_W_" + (2*row) + "_flag_out";
        pe.valid_bit_in_W_0 = "input_fifo_W_" + (2*row) + "_valid_bit_out";
        pe.input_ready_W_0 = "pe_" + row + "_" + column + "_input_ready_W_0";

        pe.data_in_W_1 = "input_fifo_W_" + (2*row+1) + "_data_out";
        pe.flag_in_W_1 = "input_fifo_W_" + (2*row+1) + "_flag_out";
        pe.valid_bit_in_W_1 = "input_fifo_W_" + (2*row+1) + "_valid_bit_out";
        pe.input_ready_W_1 = "pe_" + row + "_" + column + "_input_ready_W_1";
    }

    private void connectPeDataFlagOutNorth(VerilogPE pe, int row, int column) {
        if(!fifosBetweenPes) {
            // data output to the north 0
            pe.data_out_N_0 = "pe_" + row + "_" + column + "_data_out_N_0";
            pe.flag_out_N_0 = "pe_" + row + "_" + column + "_flag_out_N_0";
            pe.valid_bit_out_N_0 = "pe_" + row + "_" + column + "_valid_bit_out_N_0";
            pe.ext_input_ready_N_0 = "pe_" + (row - 1) + "_" + column + "_input_ready_S_0";

            // data output to the north 1
            pe.data_out_N_1 = "pe_" + row + "_" + column + "_data_out_N_1";
            pe.flag_out_N_1 = "pe_" + row + "_" + column + "_flag_out_N_1";
            pe.valid_bit_out_N_1 = "pe_" + row + "_" + column + "_valid_bit_out_N_1";
            pe.ext_input_ready_N_1 = "pe_" + (row - 1) + "_" + column + "_input_ready_S_1";
        } else {
             // data output to the north 0
            pe.data_out_N_0 = "pe_" + row + "_" + column + "_data_out_N_0";
            pe.flag_out_N_0 = "pe_" + row + "_" + column + "_flag_out_N_0";
            pe.valid_bit_out_N_0 = "pe_" + row + "_" + column + "_valid_bit_out_N_0";
            pe.ext_input_ready_N_0 = "!fifo_pe_" + row + "_" + column + "_data_out_N_0_full";

            // data output to the north 1
            pe.data_out_N_1 = "pe_" + row + "_" + column + "_data_out_N_1";
            pe.flag_out_N_1 = "pe_" + row + "_" + column + "_flag_out_N_1";
            pe.valid_bit_out_N_1 = "pe_" + row + "_" + column + "_valid_bit_out_N_1";
            pe.ext_input_ready_N_1 = "!fifo_pe_" + row + "_" + column + "_data_out_N_1_full";
        }
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
        } else {
            // data output to the east 0
            VerilogInputFifo fifoPeE0 = new VerilogInputFifo("fifo_pe_" + row + "_" + column + "_data_out_E_0");

            VerilogWire peDataOutE0 = new VerilogWire("pe_" + row + "_" + column + "_data_out_E_0", "`DATA_WIDTH");
            wires.add(peDataOutE0);

            fifoPeE0.data_in = peDataOutE0.name;

            VerilogWire peFlagOutE0 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_E_0");
            wires.add(peFlagOutE0);

            fifoPeE0.flag_in = peFlagOutE0.name;

            VerilogWire peValidBitOutE0 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_E_0");
            wires.add(peValidBitOutE0);

            fifoPeE0.valid_bit_in = peValidBitOutE0.name;

            fifoPeE0.config_select_in = "pe_" + row + "_" + column + "_config_select_out_E_0";

            VerilogWire peEastInputReadyW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_input_ready_W_0");
            wires.add(peEastInputReadyW0);

            fifoPeE0.ext_input_ready = peEastInputReadyW0.name;

            VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
            wires.add(peEastConfigSelectOutW0);

            fifoPeE0.ext_config_select_in = peEastConfigSelectOutW0.name;

            VerilogWire fifoPeDataOutE0DataOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_0_data_out", "`DATA_WIDTH");
            wires.add(fifoPeDataOutE0DataOut);

            fifoPeE0.data_out = fifoPeDataOutE0DataOut.name;

            VerilogWire fifoPeDataOutE0FlagOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_0_flag_out");
            wires.add(fifoPeDataOutE0FlagOut);

            fifoPeE0.flag_out = fifoPeDataOutE0FlagOut.name;

            VerilogWire fifoPeDataOutE0ValidBitOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_0_valid_bit_out");
            wires.add(fifoPeDataOutE0ValidBitOut);

            fifoPeE0.valid_bit_out = fifoPeDataOutE0ValidBitOut.name;

            VerilogWire fifoPeDataOutE0ConfigSelectOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_0_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(fifoPeDataOutE0ConfigSelectOut);

            fifoPeE0.config_select_out = fifoPeDataOutE0ConfigSelectOut.name;

            VerilogWire fifoPeDataOutE0Full = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_0_full");
            wires.add(fifoPeDataOutE0Full);

            fifoPeE0.full = fifoPeDataOutE0Full.name;

            interPeFifos.add(fifoPeE0);

            pe.data_out_E_0 = peDataOutE0.name;
            pe.flag_out_E_0 = peFlagOutE0.name;
            pe.valid_bit_out_E_0 = peValidBitOutE0.name;
            pe.ext_input_ready_E_0 = "!" + fifoPeDataOutE0Full.name;

            // data output to the east 1
            VerilogInputFifo fifoPeE1 = new VerilogInputFifo("fifo_pe_" + row + "_" + column + "_data_out_E_1");

            VerilogWire peDataOutE1 = new VerilogWire("pe_" + row + "_" + column + "_data_out_E_1", "`DATA_WIDTH");
            wires.add(peDataOutE1);

            fifoPeE1.data_in = peDataOutE1.name;

            VerilogWire peFlagOutE1 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_E_1");
            wires.add(peFlagOutE1);

            fifoPeE1.flag_in = peFlagOutE1.name;

            VerilogWire peValidBitOutE1 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_E_1");
            wires.add(peValidBitOutE1);

            fifoPeE1.valid_bit_in = peValidBitOutE1.name;

            fifoPeE1.config_select_in = "pe_" + row + "_" + column + "_config_select_out_E_0";

            VerilogWire peEastInputReadyW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_input_ready_W_1");
            wires.add(peEastInputReadyW1);

            fifoPeE1.ext_input_ready = peEastInputReadyW1.name;

            VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
            wires.add(peEastConfigSelectOutW1);

            fifoPeE1.ext_config_select_in = peEastConfigSelectOutW1.name;

            VerilogWire fifoPeDataOutE1DataOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_1_data_out", "`DATA_WIDTH");
            wires.add(fifoPeDataOutE1DataOut);

            fifoPeE1.data_out = fifoPeDataOutE1DataOut.name;

            VerilogWire fifoPeDataOutE1FlagOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_1_flag_out");
            wires.add(fifoPeDataOutE1FlagOut);

            fifoPeE1.flag_out = fifoPeDataOutE1FlagOut.name;

            VerilogWire fifoPeDataOutE1ValidBitOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_1_valid_bit_out");
            wires.add(fifoPeDataOutE1ValidBitOut);

            fifoPeE1.valid_bit_out = fifoPeDataOutE1ValidBitOut.name;

            VerilogWire fifoPeDataOutE1ConfigSelectOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_1_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(fifoPeDataOutE1ConfigSelectOut);

            fifoPeE1.config_select_out = fifoPeDataOutE1ConfigSelectOut.name;

            VerilogWire fifoPeDataOutE1Full = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_E_1_full");
            wires.add(fifoPeDataOutE1Full);

            fifoPeE1.full = fifoPeDataOutE1Full.name;

            interPeFifos.add(fifoPeE1);

            pe.data_out_E_1 = peDataOutE1.name;
            pe.flag_out_E_1 = peFlagOutE1.name;
            pe.valid_bit_out_E_1 = peValidBitOutE1.name;
            pe.ext_input_ready_E_1 = "!" + fifoPeDataOutE1Full.name;
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

        pe.ext_input_ready_E_0 = "!output_fifo_full[" + (row*2) + ":" + (row*2) + "]";

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

        pe.ext_input_ready_E_1 = "!output_fifo_full[" + (row*2+1) + ":" + (row*2+1) + "]";
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
        } else {
            // data output to the south 0
            VerilogInputFifo fifoPeDataOutS0 = new VerilogInputFifo("fifo_pe_" + row + "_" + column + "_data_out_S_0");

            VerilogWire peDataOutS0 = new VerilogWire("pe_" + row + "_" + column + "_data_out_S_0", "`DATA_WIDTH");
            wires.add(peDataOutS0);

            fifoPeDataOutS0.data_in = peDataOutS0.name;

            VerilogWire peFlagOutS0 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_S_0");
            wires.add(peFlagOutS0);

            fifoPeDataOutS0.flag_in = peFlagOutS0.name;

            VerilogWire peValidBitOutS0 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_S_0");
            wires.add(peValidBitOutS0);

            fifoPeDataOutS0.valid_bit_in = peValidBitOutS0.name;

            fifoPeDataOutS0.config_select_in = "pe_" + row + "_" + column + "_config_select_out_S_0";

            VerilogWire peSouthInputReadyN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_input_ready_N_0");
            wires.add(peSouthInputReadyN0);

            fifoPeDataOutS0.ext_input_ready = peSouthInputReadyN0.name;

            VerilogWire peSouthConfigSelectOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
            wires.add(peSouthConfigSelectOutN0);

            fifoPeDataOutS0.ext_config_select_in = peSouthConfigSelectOutN0.name;

            VerilogWire fifoPeDataOutS0DataOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_0_data_out", "`DATA_WIDTH");
            wires.add(fifoPeDataOutS0DataOut);

            fifoPeDataOutS0.data_out = fifoPeDataOutS0DataOut.name;

            VerilogWire fifoPeDataOutS0FlagOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_0_flag_out");
            wires.add(fifoPeDataOutS0FlagOut);

            fifoPeDataOutS0.flag_out = fifoPeDataOutS0FlagOut.name;

            VerilogWire fifoPeDataOutS0ValidBitOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_0_valid_bit_out");
            wires.add(fifoPeDataOutS0ValidBitOut);

            fifoPeDataOutS0.valid_bit_out = fifoPeDataOutS0ValidBitOut.name;

            VerilogWire fifoPeDataOutS0ConfigSelectOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_0_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(fifoPeDataOutS0ConfigSelectOut);

            fifoPeDataOutS0.config_select_out = fifoPeDataOutS0ConfigSelectOut.name;

            VerilogWire fifoPeDataOutS0Full = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_0_full");
            wires.add(fifoPeDataOutS0Full);

            fifoPeDataOutS0.full = fifoPeDataOutS0Full.name;

            interPeFifos.add(fifoPeDataOutS0);

            pe.data_out_S_0 = peDataOutS0.name;
            pe.flag_out_S_0 = peFlagOutS0.name;
            pe.valid_bit_out_S_0 = peValidBitOutS0.name;
            pe.ext_input_ready_S_0 = "!" + fifoPeDataOutS0Full.name;

            // data output to the south 1
            VerilogInputFifo fifoPeDataOutS1 = new VerilogInputFifo("fifo_pe_" + row + "_" + column + "_data_out_S_1");

            VerilogWire peDataOutS1 = new VerilogWire("pe_" + row + "_" + column + "_data_out_S_1", "`DATA_WIDTH");
            wires.add(peDataOutS1);

            fifoPeDataOutS1.data_in = peDataOutS1.name;

            VerilogWire peFlagOutS1 = new VerilogWire("pe_" + row + "_" + column + "_flag_out_S_1");
            wires.add(peFlagOutS1);

            fifoPeDataOutS1.flag_in = peFlagOutS1.name;

            VerilogWire peValidBitOutS1 = new VerilogWire("pe_" + row + "_" + column + "_valid_bit_out_S_1");
            wires.add(peValidBitOutS1);

            fifoPeDataOutS1.valid_bit_in = peValidBitOutS1.name;

            fifoPeDataOutS1.config_select_in = "pe_" + row + "_" + column + "_config_select_out_S_1";

            VerilogWire peSouthInputReadyN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_input_ready_N_1");
            wires.add(peSouthInputReadyN1);

            fifoPeDataOutS1.ext_input_ready = peSouthInputReadyN1.name;

            VerilogWire peSouthConfigSelectOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
            wires.add(peSouthConfigSelectOutN1);

            fifoPeDataOutS1.ext_config_select_in = peSouthConfigSelectOutN1.name;

            VerilogWire fifoPeDataOutS1DataOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_1_data_out", "`DATA_WIDTH");
            wires.add(fifoPeDataOutS1DataOut);

            fifoPeDataOutS1.data_out = fifoPeDataOutS1DataOut.name;

            VerilogWire fifoPeDataOutS1FlagOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_1_flag_out");
            wires.add(fifoPeDataOutS1FlagOut);

            fifoPeDataOutS1.flag_out = fifoPeDataOutS1FlagOut.name;

            VerilogWire fifoPeDataOutS1ValidBitOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_1_valid_bit_out");
            wires.add(fifoPeDataOutS1ValidBitOut);

            fifoPeDataOutS1.valid_bit_out = fifoPeDataOutS1ValidBitOut.name;

            VerilogWire fifoPeDataOutS1ConfigSelectOut = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_1_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(fifoPeDataOutS1ConfigSelectOut);

            fifoPeDataOutS1.config_select_out = fifoPeDataOutS1ConfigSelectOut.name;

            VerilogWire fifoPeDataOutS1Full = new VerilogWire("fifo_pe_" + row + "_" + column + "_data_out_S_1_full");
            wires.add(fifoPeDataOutS1Full);

            fifoPeDataOutS1.full = fifoPeDataOutS1Full.name;

            interPeFifos.add(fifoPeDataOutS1);

            pe.data_out_S_1 = peDataOutS1.name;
            pe.flag_out_S_1 = peFlagOutS1.name;
            pe.valid_bit_out_S_1 = peValidBitOutS1.name;
            pe.ext_input_ready_S_1 = "!" + fifoPeDataOutS1Full.name;
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
                        "    data_in_W,\n" +
                        "    flag_in_W,\n" +
                        "    valid_bit_in_W,\n" +
                        "    config_select_in_W,\n" +
                        "\n" +
                        "    data_out,\n" +
                        "    flag_out,\n" +
                        "    valid_bit_out,\n" +
                        "    config_select_out,\n" +
                        "\n" +
                        "    input_fifo_full_W,\n" +
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
                        "    input wire [(" + crc.getRows() + "*`DATA_WIDTH*2)-1:0] data_in_W;\n" +
                        "    input wire [(" + crc.getRows() + "*2)-1:0] flag_in_W;\n" +
                        "    input wire [(" + crc.getRows() + "*2)-1:0] valid_bit_in_W;\n" +
                        "    input wire [(" + crc.getRows() + "*`CONFIG_SELECT_WIDTH*2)-1:0] config_select_in_W;\n" +
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
                        "    output wire [(" + crc.getRows() + "*2)-1:0] input_fifo_full_W;\n" +
                        "    output wire [(" + crc.getRows() + "*2)-1:0] output_fifo_full;\n\n";




        // generate input FIFOs
        for(int row = 0; row < crc.getRows(); row++) {

            // input FIFO for data_in_0
            int inputFifoNumber = 2*row;

            VerilogInputFifo inputFifo0 = new VerilogInputFifo("input_fifo_W_" + inputFifoNumber);
            inputFifo0.data_in = "data_in_W[`DATA_WIDTH*" + (inputFifoNumber+1) + "-1:`DATA_WIDTH*" + inputFifoNumber + "]";
            inputFifo0.flag_in = "flag_in_W[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo0.valid_bit_in = "valid_bit_in_W[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo0.config_select_in = "config_select_in_W[`CONFIG_SELECT_WIDTH*" + (inputFifoNumber+1) + "-1:`CONFIG_SELECT_WIDTH*" + inputFifoNumber + "]";

            VerilogWire peWestmostColumnInputReadyW0 = new VerilogWire("pe_" + row + "_0_input_ready_W_0");
            wires.add(peWestmostColumnInputReadyW0);

            inputFifo0.ext_input_ready = peWestmostColumnInputReadyW0.name;

            VerilogWire peWestmostColumnConfigSelectOutW0 = new VerilogWire("pe_" + row + "_0_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
            wires.add(peWestmostColumnConfigSelectOutW0);

            inputFifo0.ext_config_select_in = peWestmostColumnConfigSelectOutW0.name;

            VerilogWire inputFifo0DataOut = new VerilogWire("input_fifo_W_" + inputFifoNumber + "_data_out", "`DATA_WIDTH");
            wires.add(inputFifo0DataOut);

            inputFifo0.data_out = inputFifo0DataOut.name;

            VerilogWire inputFifo0FlagOut = new VerilogWire("input_fifo_W_" + inputFifoNumber + "_flag_out");
            wires.add(inputFifo0FlagOut);

            inputFifo0.flag_out = inputFifo0FlagOut.name;

            VerilogWire inputFifo0ValidBitOut = new VerilogWire("input_fifo_W_" + inputFifoNumber + "_valid_bit_out");
            wires.add(inputFifo0ValidBitOut);

            inputFifo0.valid_bit_out = inputFifo0ValidBitOut.name;

            VerilogWire inputFifo0ConfigSelectOut = new  VerilogWire("input_fifo_W_" + inputFifoNumber + "_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(inputFifo0ConfigSelectOut);

            inputFifo0.config_select_out = inputFifo0ConfigSelectOut.name;

            inputFifo0.full = "input_fifo_full_W[" + inputFifoNumber + ":" + inputFifoNumber + "]";

            inputFifosWest.add(inputFifo0);

            // input FIFO for data_in_1
            inputFifoNumber = 2*row+1;

            VerilogInputFifo inputFifo1 = new VerilogInputFifo("input_fifo_W_" + inputFifoNumber);
            inputFifo1.data_in = "data_in_W[`DATA_WIDTH*" + (inputFifoNumber+1) + "-1:`DATA_WIDTH*" + inputFifoNumber + "]";
            inputFifo1.flag_in = "flag_in_W[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo1.valid_bit_in = "valid_bit_in_W[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifo1.config_select_in = "config_select_in_W[`CONFIG_SELECT_WIDTH*" + (inputFifoNumber+1) + "-1:`CONFIG_SELECT_WIDTH*" + inputFifoNumber + "]";

            VerilogWire peWestmostColumnInputReadyW1 = new VerilogWire("pe_" + row + "_0_input_ready_W_1");
            wires.add(peWestmostColumnInputReadyW1);

            inputFifo1.ext_input_ready = peWestmostColumnInputReadyW1.name;

            VerilogWire peWestmostColumnConfigSelectOutW1 = new VerilogWire("pe_" + row + "_0_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
            wires.add(peWestmostColumnConfigSelectOutW1);

            inputFifo1.ext_config_select_in = peWestmostColumnConfigSelectOutW1.name;

            VerilogWire inputFifo1DataOut = new VerilogWire("input_fifo_W_" + inputFifoNumber + "_data_out", "`DATA_WIDTH");
            wires.add(inputFifo1DataOut);

            inputFifo1.data_out = inputFifo1DataOut.name;

            VerilogWire inputFifo1FlagOut = new VerilogWire("input_fifo_W_" + inputFifoNumber + "_flag_out");
            wires.add(inputFifo1FlagOut);

            inputFifo1.flag_out = inputFifo1FlagOut.name;

            VerilogWire inputFifo1ValidBitOut = new VerilogWire("input_fifo_W_" + inputFifoNumber + "_valid_bit_out");
            wires.add(inputFifo1ValidBitOut);

            inputFifo1.valid_bit_out = inputFifo1ValidBitOut.name;

            VerilogWire inputFifo1ConfigSelectOut = new  VerilogWire("input_fifo_W_" + inputFifoNumber + "_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(inputFifo1ConfigSelectOut);

            inputFifo1.config_select_out = inputFifo1ConfigSelectOut.name;

            inputFifo1.full = "input_fifo_full_W[" + inputFifoNumber + ":" + inputFifoNumber + "]";

            inputFifosWest.add(inputFifo1);
        }

        for(int row = 0; row < crc.getRows(); row++) {
            for(int column = 0; column < crc.getColumns(); column++) {

                VerilogPE pe = new VerilogPE("pe_" + row + "_" + column);

                LinkedHashMap<FU.FuMode, Boolean> availableFuModes = crc.getFu(row, column).getAvailableModes();

                pe.op_add = availableFuModes.get(FU.FuMode.add);
                pe.op_sub = availableFuModes.get(FU.FuMode.sub);
                pe.op_mul = availableFuModes.get(FU.FuMode.mul);
                pe.op_div = availableFuModes.get(FU.FuMode.div);
                pe.op_and = availableFuModes.get(FU.FuMode.and);
                pe.op_or = availableFuModes.get(FU.FuMode.or);
                pe.op_xor = availableFuModes.get(FU.FuMode.xor);
                pe.op_not = availableFuModes.get(FU.FuMode.not);
                pe.op_shift_left = availableFuModes.get(FU.FuMode.shift_left);
                pe.op_shift_right = availableFuModes.get(FU.FuMode.shift_right);
                pe.op_compare = availableFuModes.get(FU.FuMode.compare);
                pe.op_multiplex = availableFuModes.get(FU.FuMode.multiplex);
                pe.op_dsp48 = availableFuModes.get(FU.FuMode.dsp48);
                pe.op_lut_8bit = availableFuModes.get(FU.FuMode.lut_8bit);
                pe.op_max = availableFuModes.get(FU.FuMode.max);
                pe.op_min = availableFuModes.get(FU.FuMode.min);

                pe.static_config_content = "" + crc.getPeStaticConfigParameterBits(row, column).length() + "'b" + crc.getPeStaticConfigParameterBits(row, column);
                pe.static_const_reg_content = "" + crc.getPeStaticConstRegContentBits(row, column).length() + "'b" + crc.getPeStaticConstRegContentBits(row, column);

                pe.lut_8bit_content = "2048'h" + crc.getFu(row, column).getLut8BitContentHexString();

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
                        // east
                        VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column + 1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW0);

                        pe.config_select_in_E_0 = peEastConfigSelectOutW0.name;

                        VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column + 1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW1);

                        pe.config_select_in_E_1 = peEastConfigSelectOutW1.name;

                        // south
                        VerilogWire peSouthConfigSelectOutN0 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN0);

                        pe.config_select_in_S_0 = peSouthConfigSelectOutN0.name;

                        VerilogWire peSouthConfigSelectOutN1 = new VerilogWire("pe_" + (row + 1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN1);

                        pe.config_select_in_S_1 = peSouthConfigSelectOutN1.name;

                        // west
                        pe.config_select_in_W_0 = "input_fifo_W_" + (2*row) + "_config_select_out";
                        pe.config_select_in_W_1 = "input_fifo_W_" + ((2*row)+1) + "_config_select_out";

                        // config select outs
                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";

                    } else {
                        // config ins
                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire fifoPeSouthDataOutN0ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN0ConfigSelectOut);

                        pe.config_select_in_S_0 = fifoPeSouthDataOutN0ConfigSelectOut.name;

                        VerilogWire fifoPeSouthDataOutN1ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN1ConfigSelectOut);

                        pe.config_select_in_S_1 = fifoPeSouthDataOutN1ConfigSelectOut.name;

                        // west
                        pe.config_select_in_W_0 = "input_fifo_W_" + (2*row) + "_config_select_out";
                        pe.config_select_in_W_1 = "input_fifo_W_" + ((2*row)+1) + "_config_select_out";

                        // config outs
                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                        // east
                        VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW0);

                        pe.config_select_in_E_0 = peEastConfigSelectOutW0.name;

                        VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW1);

                        pe.config_select_in_E_1 = peEastConfigSelectOutW1.name;

                        // south
                        VerilogWire peSouthConfigSelectOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN0);

                        pe.config_select_in_S_0 = peSouthConfigSelectOutN0.name;

                        VerilogWire peSouthConfigSelectOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN1);

                        pe.config_select_in_S_1 = peSouthConfigSelectOutN1.name;

                        // west
                        pe.config_select_in_W_0 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_0";
                        pe.config_select_in_W_1 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_1";

                        // config select outs
                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire fifoPeSouthDataOutN0ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN0ConfigSelectOut);

                        pe.config_select_in_S_0 = fifoPeSouthDataOutN0ConfigSelectOut.name;

                        VerilogWire fifoPeSouthDataOutN1ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN1ConfigSelectOut);

                        pe.config_select_in_S_1 = fifoPeSouthDataOutN1ConfigSelectOut.name;

                        // west
                        pe.config_select_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_config_select_out";
                        pe.config_select_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_config_select_out";

                        // config select out
                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peSouthConfigSelectOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN0);

                        pe.config_select_in_S_0 = peSouthConfigSelectOutN0.name;

                        VerilogWire peSouthConfigSelectOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN1);

                        pe.config_select_in_S_1 = peSouthConfigSelectOutN1.name;

                        // west
                        pe.config_select_in_W_0 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_0";
                        pe.config_select_in_W_1 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_1";

                        // config select outs
                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire fifoPeSouthDataOutN0ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN0ConfigSelectOut);

                        pe.config_select_in_S_0 = fifoPeSouthDataOutN0ConfigSelectOut.name;

                        VerilogWire fifoPeSouthDataOutN1ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN1ConfigSelectOut);

                        pe.config_select_in_S_1 = fifoPeSouthDataOutN1ConfigSelectOut.name;

                        // west
                        pe.config_select_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_config_select_out";
                        pe.config_select_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_config_select_out";

                        // config select outs
                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                    pe.regs_in_north = true;
                    pe.regs_in_south = true;

                    if(!fifosBetweenPes) {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_0";
                        pe.config_select_in_N_1 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_1";

                        // east
                        VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW0);

                        pe.config_select_in_E_0 = peEastConfigSelectOutW0.name;

                        VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW1);

                        pe.config_select_in_E_1 = peEastConfigSelectOutW1.name;

                        // south
                        VerilogWire peSouthConfigSelectOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN0);

                        pe.config_select_in_S_0 = peSouthConfigSelectOutN0.name;

                        VerilogWire peSouthConfigSelectOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN1);

                        pe.config_select_in_S_1 = peSouthConfigSelectOutN1.name;

                        // west
                        pe.config_select_in_W_0 = "input_fifo_W_" + (2*row) + "_config_select_out";
                        pe.config_select_in_W_1 = "input_fifo_W_" + (2*row+1) + "_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_config_select_out";
                        pe.config_select_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_config_select_out";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire fifoPeSouthDataOutN0ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN0ConfigSelectOut);

                        pe.config_select_in_S_0 = fifoPeSouthDataOutN0ConfigSelectOut.name;

                        VerilogWire fifoPeSouthDataOutN1ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN1ConfigSelectOut);

                        pe.config_select_in_S_1 = fifoPeSouthDataOutN1ConfigSelectOut.name;

                        // west
                        pe.config_select_in_W_0 = "input_fifo_W_" + (2*row) + "_config_select_out";
                        pe.config_select_in_W_1 = "input_fifo_W_" + (2*row+1) + "_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                    pe.regs_in_north = true;
                    pe.regs_in_south = true;

                    if(!fifosBetweenPes) {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_0";
                        pe.config_select_in_N_1 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_1";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peSouthConfigSelectOutN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN0);

                        pe.config_select_in_S_0 = peSouthConfigSelectOutN0.name;

                        VerilogWire peSouthConfigSelectOutN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectOutN1);

                        pe.config_select_in_S_1 = peSouthConfigSelectOutN1.name;

                        // west
                        pe.config_select_in_W_0 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_0";
                        pe.config_select_in_W_1 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_1";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config ins
                        // north
                        pe.config_select_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_config_select_out";
                        pe.config_select_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_config_select_out";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire fifoPeSouthDataOutN0ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN0ConfigSelectOut);

                        pe.config_select_in_S_0 = fifoPeSouthDataOutN0ConfigSelectOut.name;

                        VerilogWire fifoPeSouthDataOutN1ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN1ConfigSelectOut);

                        pe.config_select_in_S_1 = fifoPeSouthDataOutN1ConfigSelectOut.name;

                        // west
                        pe.config_select_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_config_select_out";
                        pe.config_select_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                    pe.regs_in_north = true;
                    pe.regs_in_south = false;

                    if(!fifosBetweenPes) {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_0";
                        pe.config_select_in_N_1 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_1";

                        // east
                        VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW0);

                        pe.config_select_in_E_0 = peEastConfigSelectOutW0.name;

                        VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW1);

                        pe.config_select_in_E_1 = peEastConfigSelectOutW1.name;

                        // west
                        pe.config_select_in_W_0 = "input_fifo_W_" + (row*2) + "_config_select_out";
                        pe.config_select_in_W_1 = "input_fifo_W_" + (row*2+1) + "_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_config_select_out";
                        pe.config_select_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_config_select_out";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_in_W_0 = "input_fifo_W_" + (row*2) + "_config_select_out";
                        pe.config_select_in_W_1 = "input_fifo_W_" + (row*2+1) + "_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                    pe.regs_in_north = true;
                    pe.regs_in_south = false;

                    if(!fifosBetweenPes) {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_0";
                        pe.config_select_in_N_1 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_1";

                        // east
                        VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW0);

                        pe.config_select_in_E_0 = peEastConfigSelectOutW0.name;

                        VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW1);

                        pe.config_select_in_E_1 = peEastConfigSelectOutW1.name;

                        // west
                        pe.config_select_in_W_0 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_0";
                        pe.config_select_in_W_1 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_1";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_config_select_out";
                        pe.config_select_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_config_select_out";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_config_select_out";
                        pe.config_select_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                    pe.regs_in_north = true;
                    pe.regs_in_south = false;

                    if(!fifosBetweenPes) {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_0";
                        pe.config_select_in_N_1 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_1";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_in_W_0 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_0";
                        pe.config_select_in_W_1 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_1";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_config_select_out";
                        pe.config_select_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_config_select_out";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_config_select_out";
                        pe.config_select_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
                    pe.regs_in_north = true;
                    pe.regs_in_south = true;

                    if(!fifosBetweenPes) {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_0";
                        pe.config_select_in_N_1 = "pe_" + (row-1) + "_" + column + "_config_select_out_S_1";

                        // east
                        VerilogWire peEastConfigSelectOutW0 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW0);

                        pe.config_select_in_E_0 = peEastConfigSelectOutW0.name;

                        VerilogWire peEastConfigSelectOutW1 = new VerilogWire("pe_" + row + "_" + (column+1) + "_config_select_out_W_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peEastConfigSelectOutW1);

                        pe.config_select_in_E_1 = peEastConfigSelectOutW1.name;

                        // south
                        VerilogWire peSouthConfigSelectN0 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectN0);

                        pe.config_select_in_S_0 = peSouthConfigSelectN0.name;

                        VerilogWire peSouthConfigSelectN1 = new VerilogWire("pe_" + (row+1) + "_" + column + "_config_select_out_N_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peSouthConfigSelectN1);

                        pe.config_select_in_S_1 = peSouthConfigSelectN1.name;

                        // west
                        pe.config_select_in_W_0 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_0";
                        pe.config_select_in_W_1 = "pe_" + row + "_" + (column-1) + "_config_select_out_E_1";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
                    } else {
                        // config select ins
                        // north
                        pe.config_select_in_N_0 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_0_config_select_out";
                        pe.config_select_in_N_1 = "fifo_pe_" + (row-1) + "_" + column + "_data_out_S_1_config_select_out";

                        // east
                        VerilogWire peConfigSelectOutE0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE0);

                        pe.config_select_in_E_0 = peConfigSelectOutE0.name;

                        VerilogWire peConfigSelectOutE1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_E_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutE1);

                        pe.config_select_in_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire fifoPeSouthDataOutN0ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_0_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN0ConfigSelectOut);

                        pe.config_select_in_S_0 = fifoPeSouthDataOutN0ConfigSelectOut.name;

                        VerilogWire fifoPeSouthDataOutN1ConfigSelectOut = new VerilogWire("fifo_pe_" + (row+1) + "_" + column + "_data_out_N_1_config_select_out", "`CONFIG_SELECT_WIDTH");
                        wires.add(fifoPeSouthDataOutN1ConfigSelectOut);

                        pe.config_select_in_S_1 = fifoPeSouthDataOutN1ConfigSelectOut.name;

                        // west
                        pe.config_select_in_W_0 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_0_config_select_out";
                        pe.config_select_in_W_1 = "fifo_pe_" + row + "_" + (column-1) + "_data_out_E_1_config_select_out";

                        // config select outs
                        // north
                        pe.config_select_out_N_0 = "pe_" + row + "_" + column + "_config_select_out_N_0";
                        pe.config_select_out_N_1 = "pe_" + row + "_" + column + "_config_select_out_N_1";

                        // east
                        pe.config_select_out_E_0 = peConfigSelectOutE0.name;
                        pe.config_select_out_E_1 = peConfigSelectOutE1.name;

                        // south
                        VerilogWire peConfigSelectOutS0 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_0", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS0);

                        pe.config_select_out_S_0 = peConfigSelectOutS0.name;

                        VerilogWire peConfigSelectOutS1 = new VerilogWire("pe_" + row + "_" + column + "_config_select_out_S_1", "`CONFIG_SELECT_WIDTH");
                        wires.add(peConfigSelectOutS1);

                        pe.config_select_out_S_1 = peConfigSelectOutS1.name;

                        // west
                        pe.config_select_out_W_0 = "pe_" + row + "_" + column + "_config_select_out_W_0";
                        pe.config_select_out_W_1 = "pe_" + row + "_" + column + "_config_select_out_W_1";
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
            outputFifo0.config_select_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_config_select_out_E_0";

            outputFifo0.read = "output_fifo_read[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifo0.data_out = "data_out[(" + (outputFifoNumber+1) + "*`DATA_WIDTH)-1:" + outputFifoNumber + "*`DATA_WIDTH]";
            outputFifo0.flag_out = "flag_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo0.valid_bit_out = "valid_bit_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo0.config_select_out = "config_select_out[(" + (outputFifoNumber+1) + "*`CONFIG_SELECT_WIDTH)-1:" + outputFifoNumber + "*`CONFIG_SELECT_WIDTH]";

            outputFifo0.full = "output_fifo_full[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifosEast.add(outputFifo0);

            // output FIFO for data_out_0
            outputFifoNumber = 2 * row + 1;

            VerilogOutputFifo outputFifo1 = new VerilogOutputFifo("output_fifo_" + outputFifoNumber);
            outputFifo1.data_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_data_out_E_1";
            outputFifo1.flag_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_flag_out_E_1";
            outputFifo1.valid_bit_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_valid_bit_out_E_1";
            outputFifo1.config_select_in = "pe_" + row + "_" + (crc.getColumns()-1) + "_config_select_out_E_1";

            outputFifo1.read = "output_fifo_read[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifo1.data_out = "data_out[(" + (outputFifoNumber+1) + "*`DATA_WIDTH)-1:" + outputFifoNumber + "*`DATA_WIDTH]";
            outputFifo1.flag_out = "flag_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo1.valid_bit_out = "valid_bit_out[" + outputFifoNumber + ":" + outputFifoNumber + "]";
            outputFifo1.config_select_out = "config_select_out[(" + (outputFifoNumber+1) + "*`CONFIG_SELECT_WIDTH)-1:" + outputFifoNumber + "*`CONFIG_SELECT_WIDTH]";

            outputFifo1.full = "output_fifo_full[" + outputFifoNumber + ":" + outputFifoNumber + "]";

            outputFifosEast.add(outputFifo1);
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
        module += this.getInterPeFifoDeclarations();
        module += "\n\n";
        module += this.getOutputFifoDeclarations();

        module += "endmodule\n";

        String verilogCode = header + module;

        return verilogCode;
    }
}

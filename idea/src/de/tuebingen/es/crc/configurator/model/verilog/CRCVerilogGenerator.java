package de.tuebingen.es.crc.configurator.model.verilog;

import de.tuebingen.es.crc.configurator.model.CRC;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Konstantin (Konze) Lübeck on 27/02/2017.
 */
public class CRCVerilogGenerator {

    private CRC crc;
    private boolean fifosBetweenPes;
    private ArrayList<VerilogWire> wires;
    private ArrayList<VerilogInputFifo> inputFifos;
    private ArrayList<VerilogPE> pes;

    public CRCVerilogGenerator(CRC crc, boolean fifosBetweenPes) {
        this.crc = crc;
        this.fifosBetweenPes = fifosBetweenPes;
        this.wires = new ArrayList<>();
        this.inputFifos = new ArrayList<>();
        this.pes = new ArrayList<>();
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
                        "    input wire [(" + crc.getRows() + "*2)-1:0] output_fifos_read;\n\n";

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

            VerilogInputFifo inputFifoDataIn0 = new VerilogInputFifo("input_fifo_" + inputFifoNumber);
            inputFifoDataIn0.data_in = "data_in[`DATA_WIDTH*" + (inputFifoNumber+1) + "-1:`DATA_WIDTH*" + inputFifoNumber + "]";
            inputFifoDataIn0.flag_in = "flag_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifoDataIn0.valid_bit_in = "valid_bit_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifoDataIn0.config_select_in = "config_select_in[`CONFIG_SELECT_WIDTH*" + (inputFifoNumber+1) + "-1:`CONFIG_SELECT_WIDTH*" + inputFifoNumber + "]";

            VerilogWire westmostColumnPeInputReadyW0 = new VerilogWire("pe_" + row + "_0_input_ready_W_0");
            wires.add(westmostColumnPeInputReadyW0);

            inputFifoDataIn0.ext_input_ready = westmostColumnPeInputReadyW0.name;

            VerilogWire westmostPeColumnConfigSelectOutW = new VerilogWire("pe_" + row + "_0_config_select_out_W");
            wires.add(westmostPeColumnConfigSelectOutW);

            inputFifoDataIn0.ext_config_select_in = westmostPeColumnConfigSelectOutW.name;

            VerilogWire inputFifoDataIn0DataOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_data_out", "`DATA_WIDTH");
            wires.add(inputFifoDataIn0DataOut);

            inputFifoDataIn0.data_out = inputFifoDataIn0DataOut.name;

            VerilogWire inputFifoDataIn0FlagOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_flag_out");
            wires.add(inputFifoDataIn0FlagOut);

            inputFifoDataIn0.flag_out = inputFifoDataIn0FlagOut.name;

            VerilogWire inputFifoDataIn0ValidBitOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_valid_bit_out");
            wires.add(inputFifoDataIn0ValidBitOut);

            inputFifoDataIn0.valid_bit_out = inputFifoDataIn0ValidBitOut.name;

            VerilogWire inputFifoDataIn0ConfigSelectOut = new  VerilogWire("input_fifo_" + inputFifoNumber + "_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(inputFifoDataIn0ConfigSelectOut);

            inputFifoDataIn0.config_select_out = inputFifoDataIn0ConfigSelectOut.name;

            inputFifoDataIn0.full = "input_fifo_full[" + inputFifoNumber + ":" + inputFifoNumber + "]";

            inputFifos.add(inputFifoDataIn0);

            // input FIFO for data_in_1
            inputFifoNumber = 2*row+1;

            VerilogInputFifo inputFifoDataIn1 = new VerilogInputFifo("input_fifo_" + inputFifoNumber);
            inputFifoDataIn1.data_in = "data_in[`DATA_WIDTH*" + (inputFifoNumber+1) + "-1:`DATA_WIDTH*" + inputFifoNumber + "]";
            inputFifoDataIn1.flag_in = "flag_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifoDataIn1.valid_bit_in = "valid_bit_in[" + inputFifoNumber + ":" + inputFifoNumber + "]";
            inputFifoDataIn1.config_select_in = "config_select_in[`CONFIG_SELECT_WIDTH*" + (inputFifoNumber+1) + "-1:`CONFIG_SELECT_WIDTH*" + inputFifoNumber + "]";

            VerilogWire westmostColumnPeInputReadyW1 = new VerilogWire("pe_" + row + "_0_input_ready_W_1");
            wires.add(westmostColumnPeInputReadyW1);

            inputFifoDataIn1.ext_input_ready = westmostColumnPeInputReadyW1.name;

            inputFifoDataIn1.ext_config_select_in = westmostPeColumnConfigSelectOutW.name;

            VerilogWire inputFifoDataIn1DataOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_data_out", "`DATA_WIDTH");
            wires.add(inputFifoDataIn1DataOut);

            inputFifoDataIn1.data_out = inputFifoDataIn1DataOut.name;

            VerilogWire inputFifoDataIn1FlagOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_flag_out");
            wires.add(inputFifoDataIn1FlagOut);

            inputFifoDataIn1.flag_out = inputFifoDataIn1FlagOut.name;

            VerilogWire inputFifoDataIn1ValidBitOut = new VerilogWire("input_fifo_" + inputFifoNumber + "_valid_bit_out");
            wires.add(inputFifoDataIn1ValidBitOut);

            inputFifoDataIn1.valid_bit_out = inputFifoDataIn1ValidBitOut.name;

            VerilogWire inputFifoDataIn1ConfigSelectOut = new  VerilogWire("input_fifo_" + inputFifoNumber + "_config_select_out", "`CONFIG_SELECT_WIDTH");
            wires.add(inputFifoDataIn1ConfigSelectOut);

            inputFifoDataIn1.config_select_out = inputFifoDataIn1ConfigSelectOut.name;

            inputFifoDataIn1.full = "input_fifo_full[" + inputFifoNumber + ":" + inputFifoNumber + "]";

            inputFifos.add(inputFifoDataIn1);
        }

        for(int row = 0; row < crc.getRows(); row++) {
            for(int column = 0; column < crc.getColumns(); column++) {

                // northwest corner PE
                if(row == 0 && column == 0) {
                    System.out.println("northwest corner PE");
                    System.out.println("pe_" + row + "_" + column);
                }

                // nortmost row PEs
                else if(row == 0 && column != 0 && column != crc.getColumns()-1) {
                    System.out.println("northmost row PEs");
                    System.out.println("pe_" + row + "_" + column);
                }

                // northeast corner PE
                else if(row == 0 && column == crc.getColumns()-1) {
                    System.out.println("northeast corner PE");
                    System.out.println("pe_" + row + "_" + column);
                }

                // westmost column PEs
                else if(row != 0 && row != crc.getRows()-1 && column == 0) {
                    System.out.println("westmost column PEs");
                    System.out.println("pe_" + row + "_" + column);

                }

                // eastmost column PEs
                else if(row != 0 && row != crc.getRows()-1 && column == crc.getColumns()-1) {
                    System.out.println("eastmost column PEs");
                    System.out.println("pe_" + row + "_" + column);

                }

                // southwest corner PE
                else if(row == crc.getRows()-1 && column == 0) {
                    System.out.println("southwest corner PE");
                    System.out.println("pe_" + row + "_" + column);

                }

                // southmost row PEs
                else if(row == crc.getRows()-1 && column != 0 && column != crc.getColumns()-1) {
                    System.out.println("southmost row PE");
                    System.out.println("pe_" + row + "_" + column);

                }

                // southeast corner PE
                else if(row == crc.getRows()-1 && column == crc.getColumns()-1) {
                    System.out.println("southeast corner PE");
                    System.out.println("pe_" + row + "_" + column);

                }

                // center PE
                else {
                    System.out.println("center PE");
                    System.out.println("pe_" + row + "_" + column);
                }

            }
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

        module += "endmodule\n";

        String verilogCode = header + module;

        return verilogCode;
    }
}

package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/01/2017.
 */
public class ExportBitsText {

    private String convertBitsToHex(String bits) {
        if(bits.equals("0000")) {
            return "0";
        } else if(bits.equals("0001")) {
            return "1";
        } else if(bits.equals("0010")) {
            return "2";
        } else if(bits.equals("0011")) {
            return "3";
        } else if(bits.equals("0100")) {
            return "4";
        } else if(bits.equals("0101")) {
            return "5";
        } else if(bits.equals("0110")) {
            return "6";
        } else if(bits.equals("0111")) {
            return "7";
        } else if(bits.equals("1000")) {
            return "8";
        } else if(bits.equals("1001")) {
            return "9";
        } else if(bits.equals("1010")) {
            return "A";
        } else if(bits.equals("1011")) {
            return "B";
        } else if(bits.equals("1100")) {
            return "C";
        } else if(bits.equals("1101")) {
            return "D";
        } else if(bits.equals("1110")) {
            return "E";
        } else if(bits.equals("1111")) {
            return "F";
        } else {
            return "X";
        }
    }

    private String convertBitStringToHexString(String bitString) {
        String hexString = "";

        int i;

        if(bitString.length() % 4 == 1) {
            bitString = "000" + bitString;
        } else if(bitString.length() % 4 == 2) {
            bitString = "00" + bitString;
        } else if(bitString.length() % 4 == 3) {
            bitString = "0" + bitString;
        }

        for(i = 0; i < bitString.length(); i = i + 4) {
            hexString += convertBitsToHex(bitString.substring(i,i+4));
        }

        return hexString;
    }

    private String getString(String inString, boolean cLikeHexRepresentation) {

        String outString = "";

        if(!cLikeHexRepresentation) {
            outString += inString.length() + "'b";
            outString += inString;
        } else {
            outString += "(" + inString.length() + " bits) 0x" + convertBitStringToHexString(inString);
        }
        return outString;
    }

    public String getText(CRC crc, boolean cLikeHexRepresentation) {

        String buffer;
        String text = "";

        text += "Bits for the CRC Core Verilog Module (crc_core.v)\n\n";
        text += "PE_OP_PARAMETERS:\n";
        text += getString(crc.getPeOpParametersBits(), cLikeHexRepresentation);
        text += "\n\n";
        text += "PE_STATIC_CONFIG_PARAMETERS:\n";
        text += getString(crc.getStaticConfigParameterBits(), cLikeHexRepresentation);
        text += "\n\n\n";
        text += "Bits for Single PEs (pe.v) ";

        for(int i = 0; i < crc.getRows(); i++) {
            for(int j = 0; j < crc.getColumns(); j++) {
                text += "PE " + i + "," + j + ":\n";
                text += "static_config_content:\n";
                text += getString(crc.getPeStaticConfigParameterBits(i,j), cLikeHexRepresentation);
                text += "\n\n";

                HashMap<Integer, Configuration> dynamicConfigs = crc.getDynamicConfigs();

                for(Map.Entry<Integer, Configuration> entry : dynamicConfigs.entrySet()) {
                    text += "dynamic_config_content_" + entry.getKey() + ":\n";
                    text += getString(crc.getPeDynamicConfigParameterBits(i, j, entry.getKey()), cLikeHexRepresentation);
                    text += "\n\n";
                }
            }
        }

        return text;
    }
}

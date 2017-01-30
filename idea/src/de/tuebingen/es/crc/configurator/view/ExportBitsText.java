package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/01/2017.
 */
public class ExportBitsText {
    public String getText(CRC crc) {
        String text = "";

        text += "Bits for the CRC Core Verilog Module (crc_core.v)\n\n";
        text += "PE_OP_PARAMETERS:\n";
        text += crc.getPeOpParametersBits().length() + "'b";
        text += crc.getPeOpParametersBits() + "\n\n";
        text += "PE_STATIC_CONFIG_PARAMETERS:\n";
        text += crc.getStaticConfigParameterBits().length() + "'b";
        text += crc.getStaticConfigParameterBits() + "\n\n\n";

        text += "Bits for Single PEs (pe.v) ";

        for(int i = 0; i < crc.getRows(); i++) {
            for(int j = 0; j < crc.getColumns(); j++) {
                text += "PE " + i + "," + j + ":\n";
                text += "static_config_content:\n";
                text += crc.getPeStaticConfigParameterBits(i,j).length() + "'b";
                text += crc.getPeStaticConfigParameterBits(i,j) + "\n\n";

                HashMap<Integer, Configuration> dynamicConfigs = crc.getDynamicConfigs();

                for(Map.Entry<Integer, Configuration> entry : dynamicConfigs.entrySet()) {
                    text += "dynamic_config_content_" + entry.getKey() + ":\n";
                    text += crc.getPeDynamicConfigParameterBits(i, j, entry.getKey()).length() + "'b";
                    text += crc.getPeDynamicConfigParameterBits(i, j, entry.getKey()) + "\n\n";
                }
            }
        }

        return text;
    }
}

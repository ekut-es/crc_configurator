package de.tuebingen.es.crc.configurator.model;

/**
 * Created by Konstantin (Konze) Lübeck on 30/08/16.
 */
public class PeDataFlagOutDriverBitsMap {

    public static String getBits(PE.DataFlagOutDriver dataFlagOutDriver) {

        switch (dataFlagOutDriver) {
            case data_flag_in_N_0:
                return "000";
            case data_flag_in_N_1:
                return "001";
            case data_flag_in_S_0:
                return "010";
            case data_flag_in_S_1:
                return "011";
            case data_flag_in_W_0:
                return "100";
            case data_flag_in_W_1:
                return "101";
            case data_flag_out_FU:
                return "110";
            default:
                return "111";
        }
    }
}

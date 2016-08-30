package de.tuebingen.es.crc.configurator.model;

/**
 * Created by Konstantin (Konze) Lübeck on 30/08/16.
 */
public class PeDataFlagInFuDriverBitsMap {

    public static String getBits(PE.DataFlagInFuDriver dataFlagInFuDriver) {
        switch (dataFlagInFuDriver) {
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
            default:
                return "111";
        }
    }
}

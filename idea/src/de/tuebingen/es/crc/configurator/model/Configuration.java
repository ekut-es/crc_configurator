package de.tuebingen.es.crc.configurator.model;

import java.util.ArrayList;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class Configuration {

    public enum ConfigurationType {
        STATIC, DYNAMIC
    }

    private CRC crc;

    private int number;
    private ConfigurationType configurationType;
    private ArrayList<ArrayList<PE>> pes;

    public Configuration(CRC crc) {
        this.crc = crc;
        number = -1;
        configurationType = ConfigurationType.STATIC;

        this.generatePeMatrix();
    }

    public Configuration(CRC crc, int number, ConfigurationType configurationType) {
        this.crc = crc;
        this.number = number;
        this.configurationType = configurationType;

        this.generatePeMatrix();
    }

    private void generatePeMatrix() {
        pes = new ArrayList<>();

        for(int i = 0; i < crc.getRows(); i++) {

            pes.add(new ArrayList<>());

            for(int j = 0; j < crc.getColumns(); j++) {
                pes.get(i).add(new PE(this));
            }
        }
    }

    public PE getPE(int row, int column) {
        return pes.get(row).get(column);
    }
}

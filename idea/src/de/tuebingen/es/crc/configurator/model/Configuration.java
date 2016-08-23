package de.tuebingen.es.crc.configurator.model;

import java.util.ArrayList;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class Configuration {

    private CRC crc;

    private int number;
    private ArrayList<ArrayList<PE>> pes;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Configuration(CRC crc, int number) {
        this.crc = crc;
        this.number = number;
        //configurationType = ConfigurationType.STATIC;

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

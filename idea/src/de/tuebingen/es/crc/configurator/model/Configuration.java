package de.tuebingen.es.crc.configurator.model;

import de.tuebingen.es.crc.configurator.view.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class Configuration implements Observable {

    private CRC crc;

    private int number;
    private String comment;

    private ArrayList<ArrayList<PE>> pes;
    private List<Observer> observers;

    int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    Configuration(CRC crc, int number) {
        this.crc = crc;
        this.number = number;
        observers = new ArrayList<>();
        comment = "";

        this.generatePeMatrix();
    }

    Configuration(CRC crc, int number, Configuration config) {
        this(crc, number);

        // copy PEs from config
        ArrayList<ArrayList<PE>> configPEs = config.getPes();

        for(int i = 0; i < crc.getRows(); i++) {
            if(i < configPEs.size()) {
                for(int j = 0; j < crc.getColumns(); j++) {
                    if(j < configPEs.get(i).size()) {
                        pes.get(i).get(j).copy(configPEs.get(i).get(j));
                    }
                }
            }
        }
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

    public PE getPe(int row, int column) {
        return pes.get(row).get(column);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private ArrayList<ArrayList<PE>> getPes() {
       return pes;
    }

    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers() {
        observers.forEach(Observer::update);
    }
}

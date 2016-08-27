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
    private ArrayList<ArrayList<PE>> pes;
    private List<Observer> observers;

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
        observers = new ArrayList<Observer>();

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
        for(Observer observer : observers) {
           observer.update();
        }
    }
}

package de.tuebingen.es.crc.configurator.model;

import de.tuebingen.es.crc.configurator.view.Observer;

import java.util.List;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public interface Observable {

    public void attachObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyAllObservers();
}

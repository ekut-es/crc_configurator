package de.tuebingen.es.crc.configurator.model;

import de.tuebingen.es.crc.configurator.view.Observer;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public interface Observable {

    void attachObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyAllObservers();
}

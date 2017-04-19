package de.tuebingen.es.crc.configurator.model;

import javafx.event.Event;

import java.util.EventObject;

/**
 * Created by luebeck on 4/19/17.
 */
public class FileChangedEvent extends EventObject{
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public FileChangedEvent(Object source) {
        super(source);
    }
}

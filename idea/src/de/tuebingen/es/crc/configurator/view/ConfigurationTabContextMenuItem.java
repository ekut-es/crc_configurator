package de.tuebingen.es.crc.configurator.view;

import javafx.scene.control.CheckMenuItem;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class ConfigurationTabContextMenuItem extends CheckMenuItem {

    public ConfigurationTabContextMenuItem(String text) {
        super(text);
        this.setMnemonicParsing(false);
    }
}

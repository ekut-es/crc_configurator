package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.Model;
import javafx.scene.control.Tab;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public abstract class ConfiguratorTab extends Tab {

    protected Model model;

    public static final int PE_DRAW_SIZE = 200;
    public static final int CANVAS_PADDING = 40;
    public static final int INTER_PE_DISTANCE = 80;

    public abstract void update();

}

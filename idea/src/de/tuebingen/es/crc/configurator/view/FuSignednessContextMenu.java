package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class FuSignednessContextMenu extends ContextMenu {

    // false == "unsigned"
    // true == "signed"
    private boolean selectedFuSignedness = false;

    public boolean getSelectFuSignedness() {
        return selectedFuSignedness;
    }

    public FuSignednessContextMenu(FU fu, boolean activeFuSignedess) {

        this.addMenuItem(activeFuSignedess, "signed");
        this.addMenuItem(activeFuSignedess, "unsigned");

    }

    private void addMenuItem(boolean activeFuSignedness, String fuSignedness) {
        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(fuSignedness);

        if (activeFuSignedness && (fuSignedness == "signed")) menuItem.setSelected(true);
        if (!activeFuSignedness && (fuSignedness == "unsigned")) menuItem.setSelected(true);

        menuItem.setOnAction(event -> this.selectedFuSignedness = (fuSignedness == "signed"));

        this.getItems().add(menuItem);
    }
}

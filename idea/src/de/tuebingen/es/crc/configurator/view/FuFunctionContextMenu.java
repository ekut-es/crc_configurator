package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class FuFunctionContextMenu extends ContextMenu {

    private FU.FuFunction selectedFuFunction = null;

    public FU.FuFunction getSelectedFuFunction() {
        return selectedFuFunction;
    }

    public FuFunctionContextMenu(FU fu, PE pe) {

        LinkedHashMap<FU.FuMode, Boolean> availableFuModes = fu.getAvailableModes();

        for(Map.Entry<FU.FuMode, Boolean> entry : availableFuModes.entrySet()) {
            HashSet<FU.FuFunction> fuFunctions = FU.fuFunctionsOfFuMode.get(entry.getKey());

            if(entry.getValue()) {
                for (FU.FuFunction fuFunction : fuFunctions) {
                    this.addMenuItem(fuFunction, false);
                }
            }
        }

        this.addMenuItem(FU.FuFunction.none, pe.getFuFunction() == FU.FuFunction.none);
    }

    private void addMenuItem(FU.FuFunction fuFunction, boolean active) {

        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(FU.fuFunctionToName.get(fuFunction));

        menuItem.setSelected(active);

        menuItem.setOnAction(event -> this.selectedFuFunction = fuFunction);

        this.getItems().add(menuItem);
    }
}

package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class FuFunctionContextMenu extends ContextMenu {

    private PE.FUFunction selectedFuFunction = PE.FUFunction.none;

    public PE.FUFunction getSelectedFuFunction() {
        return selectedFuFunction;
    }

    public FuFunctionContextMenu(FU fu, PE.FUFunction activeFuFunction) {

        LinkedHashMap<String, Boolean> fuFunctions = fu.getFunctions();

        for(Map.Entry<String, Boolean> entry: fuFunctions.entrySet())
            if (entry.getValue()) if (entry.getKey() != "compare" && entry.getKey() != "multiplex") {

                this.addMenuItem(activeFuFunction, entry.getKey());

            } else if (entry.getKey() == "compare") {

                this.addMenuItem(activeFuFunction, "compare_eq");
                this.addMenuItem(activeFuFunction, "compare_neq");
                this.addMenuItem(activeFuFunction, "compare_lt");
                this.addMenuItem(activeFuFunction, "compare_gt");
                this.addMenuItem(activeFuFunction, "compare_leq");
                this.addMenuItem(activeFuFunction, "compare_geq");

            } else if (entry.getKey() == "multiplex") {

                this.addMenuItem(activeFuFunction, "mux_0");
                this.addMenuItem(activeFuFunction, "mux_1");
            }
    }

    private void addMenuItem(PE.FUFunction activeFuFunction, String fuFunction) {
        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(fuFunction);
        if (activeFuFunction == PE.FUFunction.valueOf(fuFunction)) menuItem.setSelected(true);
        menuItem.setOnAction(event -> this.selectedFuFunction = PE.FUFunction.valueOf(fuFunction));
        this.getItems().add(menuItem);
    }
}

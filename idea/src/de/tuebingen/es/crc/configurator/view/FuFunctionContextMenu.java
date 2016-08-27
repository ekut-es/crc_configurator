package de.tuebingen.es.crc.configurator.view;

import com.sun.javafx.tk.Toolkit;
import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.event.EventHandler;
import javafx.scene.Node;
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

    public FuFunctionContextMenu(FU fu, PE pe) {

        LinkedHashMap<String, Boolean> fuFunctions = fu.getFunctions();

        for(Map.Entry<String, Boolean> entry: fuFunctions.entrySet())
            if (entry.getValue()) if (entry.getKey() != "compare" && entry.getKey() != "multiplex") {

                ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(entry.getKey());
                if (pe.getFUFunction() == PE.FUFunction.valueOf(entry.getKey())) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.valueOf(entry.getKey()));
                this.getItems().add(menuItem);

            } else if (entry.getKey() == "compare") {


                ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem("compare_eq");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("compare_eq")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.compare_eq);
                this.getItems().add(menuItem);

                menuItem = new ConfigurationTabContextMenuItem("compare_neq");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("compare_neq")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.compare_neq);
                this.getItems().add(menuItem);


                menuItem = new ConfigurationTabContextMenuItem("compare_lt");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("compare_lt")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.compare_lt);
                this.getItems().add(menuItem);


                menuItem = new ConfigurationTabContextMenuItem("compare_gt");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("compare_gt")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.compare_gt);
                this.getItems().add(menuItem);


                menuItem = new ConfigurationTabContextMenuItem("compare_leq");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("compare_leq")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.compare_leq);
                this.getItems().add(menuItem);


                menuItem = new ConfigurationTabContextMenuItem("compare_geq");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("compare_geq")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.compare_neq);
                this.getItems().add(menuItem);

            } else if (entry.getKey() == "multiplex") {

                ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem("mux_0");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("mux_0")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.mux_0);
                this.getItems().add(menuItem);


                menuItem = new ConfigurationTabContextMenuItem("mux_1");
                if (pe.getFUFunction() == PE.FUFunction.valueOf("mux_1")) menuItem.setSelected(true);
                menuItem.setOnAction(event -> selectedFuFunction = PE.FUFunction.mux_1);
                this.getItems().add(menuItem);
            }
    }
}

package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class DataFlagNorthDriverContextMenu extends ContextMenu {

    private PE.DataFlagOutDriver selectedNorthDriver = PE.DataFlagOutDriver.none;

    public PE.DataFlagOutDriver getSelectedDataFlagNorthDriver() { return selectedNorthDriver; }

    public DataFlagNorthDriverContextMenu(PE.DataFlagOutDriver activeDataFlagNorthDriver, int rows, int row) {

        this.addMenuItem(activeDataFlagNorthDriver, "data_flag_out_FU");
        this.addMenuItem(activeDataFlagNorthDriver, "data_flag_in_W_0");
        this.addMenuItem(activeDataFlagNorthDriver, "data_flag_in_W_1");

        if(row != rows-1) {
            this.addMenuItem(activeDataFlagNorthDriver, "data_flag_in_S_0");
            this.addMenuItem(activeDataFlagNorthDriver, "data_flag_in_S_1");
        }

    }

    private void addMenuItem(PE.DataFlagOutDriver activeDataFlagNorthDriver, String dataFlagNorthDriver) {
        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(dataFlagNorthDriver);
        if(activeDataFlagNorthDriver == PE.DataFlagOutDriver.valueOf(dataFlagNorthDriver)) { menuItem.setSelected(true); }
        menuItem.setOnAction(event -> selectedNorthDriver = PE.DataFlagOutDriver.valueOf(dataFlagNorthDriver));
        this.getItems().add(menuItem);
    }

}

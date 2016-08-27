package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class DataFlagSouthDriverContextMenu extends ContextMenu {

    private PE.DataFlagOutDriver selectedSouthDriver = PE.DataFlagOutDriver.none;

    public PE.DataFlagOutDriver getSelectedDataFlagSouthDriver() { return selectedSouthDriver; }

    public DataFlagSouthDriverContextMenu(PE.DataFlagOutDriver activeDataSouthDriver, int rows, int row) {

        if(row != 0) {
            this.addMenuItem(activeDataSouthDriver, "data_flag_in_N_0");
            this.addMenuItem(activeDataSouthDriver, "data_flag_in_N_1");
        }

        this.addMenuItem(activeDataSouthDriver, "data_flag_out_FU");
        this.addMenuItem(activeDataSouthDriver, "data_flag_in_W_0");
        this.addMenuItem(activeDataSouthDriver, "data_flag_in_W_1");
    }

    private void addMenuItem(PE.DataFlagOutDriver activeDataFlagSouthDriver, String dataFlagSouthDriver) {
        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(dataFlagSouthDriver);
        if(activeDataFlagSouthDriver == PE.DataFlagOutDriver.valueOf(dataFlagSouthDriver)) { menuItem.setSelected(true); }
        menuItem.setOnAction(event -> selectedSouthDriver = PE.DataFlagOutDriver.valueOf(dataFlagSouthDriver));
        this.getItems().add(menuItem);
    }

}

package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class DataFlagInFuDriverContextMenu extends ContextMenu {

    private PE.DataFlagInFuDriver selectedInFuDriver = PE.DataFlagInFuDriver.none;

    public PE.DataFlagInFuDriver getSelectedDataFlagInFuDriver() { return selectedInFuDriver; }

    public DataFlagInFuDriverContextMenu(PE.DataFlagInFuDriver activeDataFlagInFuDriver, int rows, int row, boolean inputsNorth, boolean inputsSouth) {

        if(row != 0 || inputsNorth) {
            this.addMenuItem(activeDataFlagInFuDriver, "data_flag_in_N_0");
            this.addMenuItem(activeDataFlagInFuDriver, "data_flag_in_N_1");
        }

        this.addMenuItem(activeDataFlagInFuDriver, "data_flag_in_W_0");
        this.addMenuItem(activeDataFlagInFuDriver, "data_flag_in_W_1");

        if(row != rows-1 || inputsSouth) {
            this.addMenuItem(activeDataFlagInFuDriver, "data_flag_in_S_0");
            this.addMenuItem(activeDataFlagInFuDriver, "data_flag_in_S_1");
        }

    }

    private void addMenuItem(PE.DataFlagInFuDriver activeDataFlagInFuDriver, String dataFlagInFuDriver) {
        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(dataFlagInFuDriver);
        if(activeDataFlagInFuDriver == PE.DataFlagInFuDriver.valueOf(dataFlagInFuDriver)) { menuItem.setSelected(true); }
        menuItem.setOnAction(event -> selectedInFuDriver = PE.DataFlagInFuDriver.valueOf(dataFlagInFuDriver));
        this.getItems().add(menuItem);
    }

}

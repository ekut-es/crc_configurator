package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.control.ContextMenu;

/**
 * Created by Konstantin (Konze) Lübeck on 27/08/16.
 */
public class DataFlagEastDriverContextMenu extends ContextMenu {

    private PE.DataFlagOutDriver selectedEastDriver = PE.DataFlagOutDriver.none;

    public PE.DataFlagOutDriver getSelectedDataFlagEastDriver() { return selectedEastDriver; }

    public DataFlagEastDriverContextMenu(PE.DataFlagOutDriver activeDataFlagEastDriver, int rows, int row, boolean inputsNorth, boolean inputsSouth) {

        if(row != 0 || inputsNorth) {
            this.addMenuItem(activeDataFlagEastDriver, "data_flag_in_N_0");
            this.addMenuItem(activeDataFlagEastDriver, "data_flag_in_N_1");
        }

        this.addMenuItem(activeDataFlagEastDriver, "data_flag_out_FU");
        this.addMenuItem(activeDataFlagEastDriver, "data_flag_in_W_0");
        this.addMenuItem(activeDataFlagEastDriver, "data_flag_in_W_1");

        if(row != rows-1 || inputsSouth) {
            this.addMenuItem(activeDataFlagEastDriver, "data_flag_in_S_0");
            this.addMenuItem(activeDataFlagEastDriver, "data_flag_in_S_1");
        }

    }

    private void addMenuItem(PE.DataFlagOutDriver activeDataFlagEastDriver, String dataFlagEastDriver) {
        ConfigurationTabContextMenuItem menuItem = new ConfigurationTabContextMenuItem(dataFlagEastDriver);
        if(activeDataFlagEastDriver == PE.DataFlagOutDriver.valueOf(dataFlagEastDriver)) { menuItem.setSelected(true); }
        menuItem.setOnAction(event -> selectedEastDriver = PE.DataFlagOutDriver.valueOf(dataFlagEastDriver));
        this.getItems().add(menuItem);
    }

}

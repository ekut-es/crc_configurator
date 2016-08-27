package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Configuration;
import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.model.PE;
import de.tuebingen.es.crc.configurator.view.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Controller {

    private Model model;
    private Stage stage;

    @FXML
    private VBox mainVBox;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem menuItemEdit;

    @FXML
    private MenuItem menuItemSave;

    @FXML
    private MenuItem menuItemSaveAs;

    @FXML
    private MenuItem menuItemClose;

    private Tab hardwareModelTab;

    private ArrayList<Tab> staticConfigurationTabs;

    /**
     * initializes the model
     * @param model
     */
    public void initModel(Model model) {
        if(this.model != null) {
            throw new IllegalStateException("Model can only be initialzied once.");
        }

        this.model = model;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * displays a error dialog with a text
     * @param errorMessage
     */
    private void showErrorMessage(String errorMessage) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error");
        dialog.setContentText(errorMessage);
        dialog.showAndWait();
    }

    /**
     * checks if file was saved before creating a new one it and presents a warning if necessary
     * shows a dialog to insert the CRC parameters
     * @param actionEvent
     */
    public void handleNewAction(ActionEvent actionEvent) {
        if(!model.isSaved()) {
            NotSavedAlert notSavedAlert = new NotSavedAlert();
            NotSavedAlert.ButtonPressed result = notSavedAlert.displayAndWait();

            if(result == NotSavedAlert.ButtonPressed.SAVE) {
                if(model.getCrcDescriptionFilePath().isEmpty()) {
                    this.saveAsCrcDescriptionFile();
                } else {
                    this.saveCrcDescriptionFile();
                }

                this.closeCrcDescriptionFile();
            }

            if(result == NotSavedAlert.ButtonPressed.DONT_SAVE) {
                this.closeCrcDescriptionFile();
            }

            if(result == NotSavedAlert.ButtonPressed.CANCEL) {
                return;
            }

        } else {
            this.closeCrcDescriptionFile();
        }

        NewDialog newDialog = new NewDialog();
        newDialog.showAndWait();

        if(newDialog.create) {

            try {
                model.createCrcDescriptionFile(newDialog.getRows(), newDialog.getColumns(), newDialog.getStaticConfigLines(), newDialog.getDynamicConfigLines());
            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }

            // show "Edit", "Save As", and "Close" in menu bar
            menuItemEdit.setDisable(false);
            menuItemSaveAs.setDisable(false);
            menuItemClose.setDisable(false);

            stage.setTitle("CRC Configurator (Unnamed File)");
            this.displayHardwareModelTab();
        }
    }

    /**
     * checks if file was saved before open another one it and presents a warning if necessary
     * show a file chooser dialog and passes selected file to model
     * @param actionEvent
     */
    public void handleOpenAction(ActionEvent actionEvent) {

        if(!model.isSaved()) {
            NotSavedAlert notSavedAlert = new NotSavedAlert();
            NotSavedAlert.ButtonPressed result = notSavedAlert.displayAndWait();

            if(result == NotSavedAlert.ButtonPressed.SAVE) {
                if(model.getCrcDescriptionFilePath().isEmpty()) {
                    this.saveAsCrcDescriptionFile();
                } else {
                    this.saveCrcDescriptionFile();
                }

                this.closeCrcDescriptionFile();
            }

            if(result == NotSavedAlert.ButtonPressed.DONT_SAVE) {
                this.closeCrcDescriptionFile();
            }

            if(result == NotSavedAlert.ButtonPressed.CANCEL) {
                return;
            }

        } else {
            this.closeCrcDescriptionFile();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CRC Description File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File crcDescriptionFile = fileChooser.showOpenDialog(mainVBox.getScene().getWindow());

        if(crcDescriptionFile != null) {
            this.openCrcDescriptionFile(crcDescriptionFile);
        }
    }

    public void handleSaveAction(ActionEvent actionEvent) {
       this.saveCrcDescriptionFile();
    }

    public void handleSaveAsAction(ActionEvent actionEvent) {
        this.saveAsCrcDescriptionFile();
    }

    /**
     * checks if file was saved before closing it and presents a warning if necessary
     * closes file
     * @param actionEvent
     */
    public void handleCloseAction(ActionEvent actionEvent) {
        if(!model.isSaved()) {
            NotSavedAlert notSavedAlert = new NotSavedAlert();
            NotSavedAlert.ButtonPressed result = notSavedAlert.displayAndWait();

            if(result == NotSavedAlert.ButtonPressed.SAVE) {
                if(model.getCrcDescriptionFilePath().isEmpty()) {
                    this.saveAsCrcDescriptionFile();
                } else {
                    this.saveCrcDescriptionFile();
                }
            }

            if(result == NotSavedAlert.ButtonPressed.DONT_SAVE) {
                this.closeCrcDescriptionFile();
            }

            // Cancel do nothing
        } else {
            this.closeCrcDescriptionFile();
        }
    }

    public void handleAboutAction(ActionEvent actionEvent) {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.showAndWait();
    }

    public void handleQuitAction(ActionEvent actionEvent) {
        this.quitApplication();
    }

    /**
     * opens a CRC description file and passes it to the model, adjusts the menu bar, and creates the "Hardware Model" tab
     * @param crcDescriptionFile
     */
    public void openCrcDescriptionFile(File crcDescriptionFile) {
        if(crcDescriptionFile != null) {
            try {
                model.parseCrcDescriptionFile(crcDescriptionFile);
            }
            catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }

        // show "Edit", "Save", "Save As", and "Close" in menu bar
        menuItemEdit.setDisable(false);
        menuItemSave.setDisable(false);
        menuItemSaveAs.setDisable(false);
        menuItemClose.setDisable(false);

        stage.setTitle("CRC Configurator (" + crcDescriptionFile.getName() + ")");
        this.displayHardwareModelTab();

        // display configurations
        this.displayStaticConfigurationTabs();
    }

    /**
     * saves data stored in model to file path stored in model
     */
    public void saveCrcDescriptionFile() {
        try {
            model.saveCrcDescriptionFile();
        }
        catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    /**
     * shows a file chooser dialog to save the file to a specific location
     */
    public void saveAsCrcDescriptionFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CRC Description File");
        File crcDescriptionFile = fileChooser.showSaveDialog(mainVBox.getScene().getWindow());

        if(crcDescriptionFile != null) {
            try {
                model.saveCrcDescriptionFile(crcDescriptionFile.getAbsolutePath());
                model.setCrcDescriptionFilePath(crcDescriptionFile.getAbsolutePath());
                menuItemSave.setDisable(false);

                // TODO change main window title and crc description file name in model
                //stage.setTitle("CRC Configurator (" + crcDescriptionFile.getName() + ")");

            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

    /**
     * checks if file was saved before closing it and presents a warning if necessary
     * quits Application
     */
    public void quitApplication() {
        if(!model.isSaved()) {
            NotSavedAlert notSavedAlert = new NotSavedAlert();
            NotSavedAlert.ButtonPressed result = notSavedAlert.displayAndWait();

            if(result == NotSavedAlert.ButtonPressed.SAVE) {
                if(model.getCrcDescriptionFilePath().isEmpty()) {
                    this.saveAsCrcDescriptionFile();
                } else {
                    this.saveCrcDescriptionFile();
                }
            }

            if(result == NotSavedAlert.ButtonPressed.DONT_SAVE) {
                System.exit(0);
            }

            // Cancel do nothing
        } else {
            System.exit(0);
        }
    }

    /**
     * removes all tabs from tab pane, adjusts menu, and resets model
     */
    public void closeCrcDescriptionFile() {
        tabPane.getTabs().clear();
        hardwareModelTab = null;
        model = new Model();
        menuItemEdit.setDisable(true);
        menuItemSave.setDisable(true);
        menuItemSaveAs.setDisable(true);
        menuItemClose.setDisable(true);
        stage.setTitle("CRC Configurator");
    }

    /**
     * adds the "Hardware Model" tab to the tab pane
     */
    private void displayHardwareModelTab() {
        hardwareModelTab = new HardwareModelTab(model, this);
        model.attachObserver((Observer) hardwareModelTab);
        tabPane.getTabs().add(hardwareModelTab);
    }

    /**
     * adds the "Static Configration" tabs to the tab pane
     */
    private void displayStaticConfigurationTabs() {

        staticConfigurationTabs = new ArrayList<>();

        for(int i = 0; i < model.getCrc().getStaticConfigLines(); i++) {
            ConfigurationTab staticConfigurationTab = new ConfigurationTab(model, this, ConfigurationTab.ConfigurationTabType.STATIC, i);
            model.attachObserver(staticConfigurationTab);
            model.getCrc().getStaticConfiguration(i).attachObserver((Observer) staticConfigurationTab);
            tabPane.getTabs().add(staticConfigurationTab);
            staticConfigurationTabs.add(staticConfigurationTab);
        }
    }


    public void handleEditAction(ActionEvent actionEvent) {
        EditDialog editDialog = new EditDialog(
                model.getCrc().getRows(),
                model.getCrc().getColumns(),
                model.getCrc().getStaticConfigLines(),
                model.getCrc().getDynamicConfigLines()
        );

        editDialog.showAndWait();


        if(editDialog.apply) {
            model.editCrc(
                    editDialog.getRows(),
                    editDialog.getColumns(),
                    editDialog.getStaticConfigLines(),
                    editDialog.getDynamicConfigLines()
            );
        }
    }

    /**
     * sets the functions of FU at position row,column
     * @param row
     * @param column
     * @param fuFunctions
     */
    public void setFuFunctions(int row, int column, LinkedHashMap<String, Boolean> fuFunctions) {
        model.setSaved(false);
        model.getCrc().getFu(row, column).setFunctions(fuFunctions);
    }

    private Configuration getConfiguration(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber) {
        if(configurationTabType == ConfigurationTab.ConfigurationTabType.STATIC) {
            return model.getCrc().getStaticConfiguration(configurationNumber);
        } else {
            return model.getCrc().getDynamicConfiguration(configurationNumber);
        }
    }

    public void setPeFunction(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.FUFunction fuFunction) {
        model.setSaved(false);
        this.getConfiguration(configurationTabType, configurationNumber).getPE(row, column).setFUFunction(fuFunction);
    }

    public void setPeDataFlagInFu0Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagInFuDriver dataFlagInFuDriver) {
        model.setSaved(false);
        this.getConfiguration(configurationTabType, configurationNumber).getPE(row, column).setDataFlagInFU0(dataFlagInFuDriver);
    }

    public void setPeDataFlagInFu1Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagInFuDriver dataFlagInFuDriver) {
        model.setSaved(false);
        this.getConfiguration(configurationTabType, configurationNumber).getPE(row, column).setDataFlagInFU1(dataFlagInFuDriver);
    }

    public void setPeFlagInFuMuxDriver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagInFuDriver dataFlagInFuDriver) {
        model.setSaved(false);
        this.getConfiguration(configurationTabType, configurationNumber).getPE(row, column).setFlagInFUMux(dataFlagInFuDriver);
    }

    public void setPeDataFlagN0Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfiguration(configurationTabType, configurationNumber).getPE(row, column).setDataFlagOutN0(dataFlagOutDriver);
    }
}




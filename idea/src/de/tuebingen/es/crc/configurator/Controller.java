package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Configuration;
import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.model.PE;
import de.tuebingen.es.crc.configurator.view.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private MenuItem menuItemExportBits;

    @FXML
    private MenuItem menuItemClose;

    private HardwareModelTab hardwareModelTab;

    private ArrayList<ConfigurationTab> staticConfigurationTabs;
    private ArrayList<ConfigurationTab> dynamicConfigurationTabs;

    /**
     * initializes the model
     * @param model
     */
    public void initModel(Model model) {
        if(this.model != null) {
            throw new IllegalStateException("Model can only be initialzied once.");
        }

        this.model = model;

        staticConfigurationTabs = new ArrayList<>();
        dynamicConfigurationTabs = new ArrayList<>();
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
            menuItemExportBits.setDisable(false);
            menuItemClose.setDisable(false);

            stage.setTitle("CRC Configurator (Unnamed File)");
            this.displayHardwareModelTab();
            this.displayStaticConfigTabs();
            this.displayDynamicConfigTabs();
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

    public void handleDocumentationAction(ActionEvent actionEvent) {

        try {
            String inputPdf = "doc/documentation.pdf";
            InputStream manualAsStream = getClass().getClassLoader().getResourceAsStream(inputPdf);
            Path tempOutput = Files.createTempFile("documentation", ".pdf");
            tempOutput.toFile().deleteOnExit();
            Files.copy(manualAsStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);
            File userManual = new File (tempOutput.toFile().getPath());
            if (userManual.exists()) {
                Desktop.getDesktop().open(userManual);
            }
        } catch (Exception e) {
            this.showErrorMessage(e.getMessage());
        }
    }

    public void handleQuitAction(ActionEvent actionEvent) {
        // TODO Mac OS X closes stage at CMD+Q
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
        menuItemExportBits.setDisable(false);
        menuItemClose.setDisable(false);

        stage.setTitle("CRC Configurator (" + crcDescriptionFile.getName() + ")");
        this.displayHardwareModelTab();

        // display configurations
        this.displayStaticConfigTabs();
        this.displayDynamicConfigTabs();

    }

    /**
     * saves data stored in model to file path stored in model
     */
    private void saveCrcDescriptionFile() {
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
    private void saveAsCrcDescriptionFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        fileChooser.setInitialFileName("*.json");
        fileChooser.setTitle("Save CRC Description File");
        File crcDescriptionFile = fileChooser.showSaveDialog(mainVBox.getScene().getWindow());

        if(crcDescriptionFile != null) {
            try {
                model.saveCrcDescriptionFile(crcDescriptionFile.getAbsolutePath());
                model.setCrcDescriptionFilePath(crcDescriptionFile.getAbsolutePath());
                menuItemSave.setDisable(false);
                stage.setTitle("CRC Configurator (" + crcDescriptionFile.getName() + ")");

            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

    public void handleExportBitsAction(ActionEvent actionEvent) {
        ExportBitsDialog exportBitsDialog = new ExportBitsDialog(model.getCrc());
        exportBitsDialog.showAndWait();
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

            else if(result == NotSavedAlert.ButtonPressed.DONT_SAVE) {
                System.exit(0);
            }

        } else {
            System.exit(0);
        }
    }

    /**
     * removes all tabs from tab pane, adjusts menu, and resets model
     */
    private void closeCrcDescriptionFile() {
        tabPane.getTabs().clear();

        // delete hardware model tab
        model.removeObserver(hardwareModelTab);
        hardwareModelTab = null;
        this.closeAllStaticConfigTabs();
        this.closeAllDynamicConfigTabs();

        model = new Model();
        menuItemEdit.setDisable(true);
        menuItemSave.setDisable(true);
        menuItemSaveAs.setDisable(true);
        menuItemExportBits.setDisable(true);
        menuItemClose.setDisable(true);
        stage.setTitle("CRC Configurator");
    }

    /**
     * adds the "Hardware Model" tab to the tab pane
     */
    private void displayHardwareModelTab() {
        hardwareModelTab = new HardwareModelTab(model, this);
        model.attachObserver(hardwareModelTab);
        tabPane.getTabs().add(hardwareModelTab);
    }

    /**
     * adds the "Static Configration" tabs to the tab pane
     */
    private void displayStaticConfigTabs() {

        HashMap<Integer,Configuration> staticConfigs =  model.getCrc().getStaticConfigs();

        for (Map.Entry<Integer, Configuration> entry : staticConfigs.entrySet()) {
            ConfigurationTab staticConfigurationTab = new ConfigurationTab(model, this, ConfigurationTab.ConfigurationTabType.STATIC, entry.getKey());
            model.attachObserver(staticConfigurationTab);
            model.getCrc().getStaticConfig(entry.getKey()).attachObserver(staticConfigurationTab);
            tabPane.getTabs().add(staticConfigurationTab);
            staticConfigurationTabs.add(staticConfigurationTab);
        }
    }

    private void closeAllStaticConfigTabs() {

        // remove as observer from model and from tab pane
        for(ConfigurationTab staticConfigurationTab : staticConfigurationTabs) {
            model.removeObserver(staticConfigurationTab);
            model.getCrc().getStaticConfig(staticConfigurationTab.getNumber()).removeObserver(staticConfigurationTab);
            tabPane.getTabs().remove(staticConfigurationTab);
        }

        staticConfigurationTabs.clear();
    }

    /**
     * adds the "Dynamic Configration" tabs to the tab pane
     */
    private void displayDynamicConfigTabs() {

        HashMap<Integer,Configuration> dynamicConfigs =  model.getCrc().getDynamicConfigs();

        for (Map.Entry<Integer, Configuration> entry : dynamicConfigs.entrySet()) {
            ConfigurationTab dynamicConfigurationTab = new ConfigurationTab(model, this, ConfigurationTab.ConfigurationTabType.DYNAMIC, entry.getKey());
            model.attachObserver(dynamicConfigurationTab);
            model.getCrc().getDynamicConfig(entry.getKey()).attachObserver(dynamicConfigurationTab);
            tabPane.getTabs().add(dynamicConfigurationTab);
            dynamicConfigurationTabs.add(dynamicConfigurationTab);
        }
    }

    private void closeAllDynamicConfigTabs() {

        // remove as observer from model
        for(ConfigurationTab dynamicConfigurationTab : dynamicConfigurationTabs) {
            model.removeObserver(dynamicConfigurationTab);
            model.getCrc().getDynamicConfig(dynamicConfigurationTab.getNumber()).removeObserver(dynamicConfigurationTab);
            tabPane.getTabs().remove(dynamicConfigurationTab);
        }

        dynamicConfigurationTabs.clear();
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

            this.closeAllStaticConfigTabs();
            this.closeAllDynamicConfigTabs();

            model.editCrc(
                    editDialog.getRows(),
                    editDialog.getColumns(),
                    editDialog.getStaticConfigLines(),
                    editDialog.getDynamicConfigLines()
            );

            this.displayStaticConfigTabs();
            this.displayDynamicConfigTabs();
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
        model.getCrc().setFuFunctions(row, column, fuFunctions);
    }

    private Configuration getConfig(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber) {
        if(configurationTabType == ConfigurationTab.ConfigurationTabType.STATIC) {
            return model.getCrc().getStaticConfig(configurationNumber);
        } else {
            return model.getCrc().getDynamicConfig(configurationNumber);
        }
    }

    public void setPeFunction(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.FUFunction fuFunction) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setFuFunction(fuFunction);
    }

    public void setPeDataFlagInFu0Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagInFuDriver dataFlagInFuDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagInFu0(dataFlagInFuDriver);
    }

    public void setPeDataFlagInFu1Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagInFuDriver dataFlagInFuDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagInFu1(dataFlagInFuDriver);
    }

    public void setPeFlagInFuMuxDriver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagInFuDriver dataFlagInFuDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setFlagInFuMux(dataFlagInFuDriver);
    }

    public void setPeDataFlagN0Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagOutN0(dataFlagOutDriver);
    }

    public void setPeDataFlagN1Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagOutN1(dataFlagOutDriver);
    }

    public void setPeDataFlagE0Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagOutE0(dataFlagOutDriver);
    }

    public void setPeDataFlagE1Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagOutE1(dataFlagOutDriver);
    }

    public void setPeDataFlagS0Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagOutS0(dataFlagOutDriver);
    }

    public void setPeDataFlagS1Driver(ConfigurationTab.ConfigurationTabType configurationTabType, int configurationNumber, int row, int column, PE.DataFlagOutDriver dataFlagOutDriver) {
        model.setSaved(false);
        this.getConfig(configurationTabType, configurationNumber).getPe(row, column).setDataFlagOutS1(dataFlagOutDriver);
    }


}




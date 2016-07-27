package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.AboutDialog;
import de.tuebingen.es.crc.configurator.view.ConfiguratorTab;
import de.tuebingen.es.crc.configurator.view.HardwareModelTab;
import de.tuebingen.es.crc.configurator.view.NotSavedAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Optional;

public class Controller {

    private Model model;
    private Stage stage;

    @FXML
    private VBox mainVBox;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem menuItemSave;

    @FXML
    private MenuItem menuItemSaveAs;

    @FXML
    private MenuItem menuItemClose;

    private Tab hardwareModelTab;


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

    public void handleNewAction(ActionEvent actionEvent) {
        System.out.println("New");
    }

    /**
     * show a file chooser dialog and passes selected file to model
     * @param actionEvent
     */
    public void handleOpenAction(ActionEvent actionEvent) {

        if(!model.isSaved()) {
            showErrorMessage("Current CRC description file was not saved.");
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

    /**
     * shows the about dialog
     * @param actionEvent
     */
    public void handleAboutAction(ActionEvent actionEvent) {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.showAndWait();
    }

    /**
     * calls quit method (checks is a CRC is opened)
     * @param actionEvent
     */
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

        // show "Save" and "Save As" in menu bar
        menuItemSave.setDisable(false);
        menuItemSaveAs.setDisable(false);
        menuItemClose.setDisable(false);

        stage.setTitle("CRC Configurator (" + crcDescriptionFile.getName() + ")");
        this.displayHardwareModelTab();
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
            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

    /**
     * checks if a file is opened and quits application
     */
    public void quitApplication() {
        if(!model.isSaved()) {
            showErrorMessage("Current CRC description file was not saved.");
        }

        System.exit(0);
    }

    /**
     * removes all tabs from tab pane, adjusts menu, and resets model
     */
    public void closeCrcDescriptionFile() {
        tabPane.getTabs().clear();
        hardwareModelTab = null;
        model = new Model();
        menuItemSave.setDisable(true);
        menuItemSaveAs.setDisable(true);
        stage.setTitle("CRC Configurator");
    }

    /**
     * adds the "Hardware Model" tab to the tab pane
     */
    private void displayHardwareModelTab() {
        hardwareModelTab = new HardwareModelTab(model, this);
        model.attachObserver((ConfiguratorTab) hardwareModelTab);
        tabPane.getTabs().add(hardwareModelTab);
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
}



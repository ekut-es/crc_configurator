package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.AboutDialog;
import de.tuebingen.es.crc.configurator.view.HardwareModelTab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    private Model model;

    @FXML
    private VBox mainVBox;

    @FXML
    private TabPane tabPane;

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

    /**
     * displays a error dialog with a text
     * @param errorMessage
     */
    private void showErrorMessage(String errorMessage) {
        Alert aboutDialog = new Alert(Alert.AlertType.ERROR);
        aboutDialog.setTitle("Error");
        aboutDialog.setContentText(errorMessage);
        aboutDialog.showAndWait();
    }

    public void handleNewAction(ActionEvent actionEvent) {
        System.out.println("New");
    }

    /**
     * show a file chooser dialog and passes selected file to model
     * @param actionEvent
     */
    public void handleOpenAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CRC Description File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File crcDescriptionFile = fileChooser.showOpenDialog(mainVBox.getScene().getWindow());

        this.openCrcDescriptionFile(crcDescriptionFile);
    }

    public void openCrcDescriptionFile(File crcDescriptionFile) {
        if(crcDescriptionFile != null) {
            try {
                this.model.parseCrcDescriptionFile(crcDescriptionFile);
            }
            catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }

        this.displayHardwareModelTab();
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
     * quits the application (checks is a CRC is opened)
     * @param actionEvent
     */
    public void handleQuitAction(ActionEvent actionEvent) {

        // TODO: check if a file is open
        System.exit(0);
    }

    /**
     * adds the "Hardware Model" tab to the tab pane
     */
    private void displayHardwareModelTab() {
        this.hardwareModelTab = new HardwareModelTab(this.model);
        tabPane.getTabs().add(hardwareModelTab);
    }
}



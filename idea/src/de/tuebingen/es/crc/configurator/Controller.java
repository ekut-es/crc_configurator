package de.tuebingen.es.crc.configurator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    private Model model;

    @FXML
    private VBox mainVBox;

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

        if(crcDescriptionFile != null) {
            try {
                this.model.parseCrcDescriptionFile(crcDescriptionFile);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * shows the about dialog
     * @param actionEvent
     */
    public void handleAboutAction(ActionEvent actionEvent) {
        Alert aboutDialog = new Alert(Alert.AlertType.NONE);
        aboutDialog.setTitle("About CRC Configurator");

        aboutDialog.setContentText(
                "Konstantin Lübeck\n" +
                        "Eberhard Karls Universität Tübingen\n" +
                        "Wilhelm-Schickard-Institut\n" +
                        "Chair for Embedded Systems\n\n" +
                        "Version 0.1 (2016)"
        );

        aboutDialog.getButtonTypes().clear();
        aboutDialog.getButtonTypes().add(ButtonType.CLOSE);
        aboutDialog.showAndWait();
    }
}

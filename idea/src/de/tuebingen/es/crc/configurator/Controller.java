package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.shape.ArcType;

import java.io.File;

public class Controller {

    private Model model;

    @FXML
    private VBox mainVBox;

    @FXML
    private TabPane tabPane;

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

    public void handleQuitAction(ActionEvent actionEvent) {

        // TODO: check if a file is open
        System.exit(0);
    }

    private void displayHardwareModelTab() {
        Tab hardwareModelTab = new Tab();
        hardwareModelTab.setText("Hardware Model");

        Canvas canvas = new Canvas();
        canvas.setHeight(2000);
        canvas.setWidth(2000);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        //gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        drawPe(gc, 0, 0);
        drawPe(gc, 1, 0);
        drawPe(gc, 0, 1);
        drawPe(gc, 1, 1);

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hardwareModelTab.setContent(scrollPane);

        tabPane.getTabs().add(hardwareModelTab);
    }

    private void drawPe(GraphicsContext gc, int row, int column) {

        int x = 20+(row*240);
        int y = 20+(column*240);

        gc.strokeRect(x, y, 200, 200);
        gc.fillText(row + "," + column, 10+x, 190+y);
        gc.strokePolygon(
                new double[] {0+70+x, 60+70+x, 60+70+x, 0+70+x, 0+70+x, 20+70+x, 0+70+x},
                new double[] {0+30+y, 20+30+y, 120+30+y, 140+30+y, 80+30+y, 70+30+y, 60+30+y},
                7
        );
        gc.fillText("FU", 90+x, 140+y);
    }
}

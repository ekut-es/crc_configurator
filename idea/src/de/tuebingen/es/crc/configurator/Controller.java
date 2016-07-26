package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.CRC;
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

    private static final int PE_DRAW_SIZE = 200;
    private static final int CANVAS_PADDING = 40;
    private static final int INTER_PE_DISTANCE = 60;

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

        this.drawHardwareModelCrc(hardwareModelTab);

        tabPane.getTabs().add(hardwareModelTab);
    }

    private void drawHardwareModelCrc(Tab hardwareModelTab) {

        CRC crc = model.getCrc();

        Canvas canvas = new Canvas();
        canvas.setHeight(2*CANVAS_PADDING+(crc.getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))-INTER_PE_DISTANCE);
        canvas.setWidth(2*CANVAS_PADDING+(crc.getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))-INTER_PE_DISTANCE);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for(int i = 0; i < crc.getRows(); i++) {
            for(int j = 0; j < crc.getColumns(); j++) {
                drawPe(gc, i, j);
            }
        }

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hardwareModelTab.setContent(scrollPane);

    }

    private void drawPe(GraphicsContext gc, int row, int column) {

        int x = CANVAS_PADDING+(column*(PE_DRAW_SIZE+INTER_PE_DISTANCE));
        int y = CANVAS_PADDING+(row*(PE_DRAW_SIZE+INTER_PE_DISTANCE));

        final int peDrawSizeTwentieth = (PE_DRAW_SIZE/20);

        gc.strokeRect(x, y, PE_DRAW_SIZE, PE_DRAW_SIZE);
        gc.fillText(row + "," + column, peDrawSizeTwentieth+x, PE_DRAW_SIZE-(peDrawSizeTwentieth)+y);

        gc.strokePolygon(
                new double[] {
                        0+7*peDrawSizeTwentieth+x,
                        6*peDrawSizeTwentieth+7*peDrawSizeTwentieth+x,
                        6*peDrawSizeTwentieth+7*peDrawSizeTwentieth+x,
                        0+7*peDrawSizeTwentieth+x,
                        0+7*peDrawSizeTwentieth+x,
                        2*peDrawSizeTwentieth+7*peDrawSizeTwentieth+x,
                        0+7*peDrawSizeTwentieth+x
                },
                new double[] {
                        0+3*peDrawSizeTwentieth+y,
                        2*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        12*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        14*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        8*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        7*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        6*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y
                },
                7
        );

        gc.fillText("FU", 9*peDrawSizeTwentieth+x, 14*peDrawSizeTwentieth+y);
    }
}

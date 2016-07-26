package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.AboutDialog;
import de.tuebingen.es.crc.configurator.view.FuFunctionsDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {

    public static final int PE_DRAW_SIZE = 200;
    public static final int CANVAS_PADDING = 40;
    public static final int INTER_PE_DISTANCE = 80;

    private Model model;

    @FXML
    private VBox mainVBox;

    @FXML
    private TabPane tabPane;

    private Tab hardwareModelTab;
    private GraphicsContext hardwareModelGc;

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
        hardwareModelTab = new Tab();
        hardwareModelTab.setText("Hardware Model");

        Canvas canvas = new Canvas();
        canvas.setWidth(2*CANVAS_PADDING+(model.getCrc().getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))-INTER_PE_DISTANCE);
        canvas.setHeight(2*CANVAS_PADDING+(model.getCrc().getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE)));

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if(event.getButton().equals(MouseButton.PRIMARY)) {
                        if(event.getClickCount() == 2) {
                            this.handleHardwareModelDoubleClick((int) event.getX(), (int) event.getY());
                        }
                    }
                }
        );

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        this.hardwareModelGc = canvas.getGraphicsContext2D();
        this.drawHardwareModelCrc(this.hardwareModelGc);

        hardwareModelTab.setContent(scrollPane);

        tabPane.getTabs().add(hardwareModelTab);
    }

    /**
     * draws the hardware model of the CRC into the "Hardware Model" provided graphics content
     * @param gc
     */
    private void drawHardwareModelCrc(GraphicsContext gc) {

        gc.clearRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for(int i = 0; i < model.getCrc().getRows(); i++) {
            for(int j = 0; j < model.getCrc().getColumns(); j++) {
                drawPe(gc, i, j);
                writeFuFunctions(gc, i, j, model.getCrc().getFu(i,j));
            }
        }
    }

    /**
     * draws a PE
     * @param gc
     * @param row
     * @param column
     */
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

    /**
     * writes the supported PE FU functions under a PE
     * @param gc
     * @param row
     * @param column
     * @param fu
     */
    private void writeFuFunctions(GraphicsContext gc, int row, int column, FU fu) {

        int x = CANVAS_PADDING+(column*(PE_DRAW_SIZE+INTER_PE_DISTANCE));
        int y = CANVAS_PADDING+(row*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+(PE_DRAW_SIZE+(PE_DRAW_SIZE/10));

        LinkedHashMap<String, Boolean> fuFunctions = fu.getFunctions();

        String fuFunctionsString = "";

        int i = 0;

        for(Map.Entry<String, Boolean> function : fuFunctions.entrySet())  {
            if(function.getValue()) {

                if(i != 0) {
                    fuFunctionsString += ", ";
                }

                if((i != 0) && (i % 3 == 0)) {
                    fuFunctionsString += "\n";
                }

                fuFunctionsString += function.getKey() ;

                i++;
            }
        }

        if(fuFunctionsString.isEmpty()) {
            fuFunctionsString = "nop";
        }

        gc.fillText(fuFunctionsString, x, y);
    }

    /**
     * figures out on which PE in the "Hardware Model" tab was clicked ans shows dialog to select FU functions
     * @param x
     * @param y
     */
    private void handleHardwareModelDoubleClick(int x, int y) {
        // decide which PE
        int row = -1;
        int column = -1;

        CRC crc = model.getCrc();

        for(int i = 0; i < crc.getColumns(); i++) {

            if(x >= CANVAS_PADDING+(i*(PE_DRAW_SIZE+INTER_PE_DISTANCE)) && x <= CANVAS_PADDING+(i*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+PE_DRAW_SIZE) {

                column = i;

                for(int j = 0; j < crc.getRows(); j++) {

                    if(y >= CANVAS_PADDING+(j*(PE_DRAW_SIZE+INTER_PE_DISTANCE)) && y <= CANVAS_PADDING+(j*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+PE_DRAW_SIZE) {
                        row = j;
                    }
                }
            }
        }

        // it was clicked on PE
        if(row != -1) {

            Point p = MouseInfo.getPointerInfo().getLocation();

            FuFunctionsDialog dialog = new FuFunctionsDialog(row, column, crc.getFu(row, column).getFunctions());

            dialog.setX(p.x-100);
            dialog.setY(p.y-80);

            dialog.showAndWait();

            if(dialog.modelHasChanged) {
                this.drawHardwareModelCrc(this.hardwareModelGc);
            }
        }
    }
}



package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.Controller;
import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.Model;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class HardwareModelTab extends ConfiguratorTab implements Observer {

    private final Model model;
    private final Controller controller;
    private GraphicsContext gc;
    private ScrollPane scrollPane;
    private double scrollPaneVvalue;
    private double scrollPaneHvalue;

    public HardwareModelTab(Model model, Controller controller) {
        super();

        this.model = model;
        this.controller = controller;

        this.setText("Hardware Model");

        this.scrollPaneVvalue = 0.0;
        this.scrollPaneHvalue = 0.0;

        this.setup();
        this.drawCrcHardwareModel();

    }

    protected void selectionChanged() {
        if(!this.isSelected()) {
            scrollPaneVvalue = scrollPane.getVvalue();
            scrollPaneHvalue = scrollPane.getHvalue();
            gc = null;
        } else {
            this.update();
        }
    }

    /**
     * redraws config
     */
    @Override
    public void update() {

        if(this.isSelected()) {
            this.setup();
            this.drawCrcHardwareModel();
        }
    }

    /**
     * sets up the drawing canvas and draws config
     */
    private void setup() {

        VBox outerVBox = new VBox(2);

        Canvas canvas = new Canvas();
        canvas.setHeight(2*CANVAS_PADDING+(model.getCrc().getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE)));
        canvas.setWidth(2*CANVAS_PADDING+(model.getCrc().getColumns()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))-INTER_PE_DISTANCE);

        // listen for double clicks in the hardware model tab
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if(event.getButton().equals(MouseButton.PRIMARY)) {
                        if(event.getClickCount() == 2) {
                            this.handleHardwareModelDoubleClick((int) event.getX(), (int) event.getY());
                        }
                    }
                }
        );

        scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        outerVBox.getChildren().add(scrollPane);

        TextArea commentTextArea = this.addCommentTextArea(outerVBox);
        commentTextArea.setText(model.getCrc().getComment());
        commentTextArea.textProperty().addListener((observable, oldValue, newValue) -> controller.setCrcComment(newValue));


        this.setContent(outerVBox);

        scrollPane.setVvalue(scrollPaneVvalue);
        scrollPane.setHvalue(scrollPaneHvalue);

        gc = canvas.getGraphicsContext2D();

    }


    /**
     * draws CRC hardware model
     */
    private void drawCrcHardwareModel() {

        gc.clearRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for(int i = 0; i < model.getCrc().getRows(); i++) {
            for(int j = 0; j < model.getCrc().getColumns(); j++) {
                drawPe(gc, i, j);
                writeFuFunctions(gc, i, j, model.getCrc().getFu(i, j));
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

        gc.strokeRect(x, y, PE_DRAW_SIZE, PE_DRAW_SIZE);

        this.drawFU(gc, x, y, true);
        gc.fillText(row + "," + column, peDrawSizeTwentieth+x, PE_DRAW_SIZE-(peDrawSizeTwentieth)+y);
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

        for(int i = 0; i < model.getCrc().getColumns(); i++) {

            if(x >= CANVAS_PADDING+(i*(PE_DRAW_SIZE+INTER_PE_DISTANCE)) && x <= CANVAS_PADDING+(i*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+PE_DRAW_SIZE) {

                column = i;

                for(int j = 0; j < model.getCrc().getRows(); j++) {

                    if(y >= CANVAS_PADDING+(j*(PE_DRAW_SIZE+INTER_PE_DISTANCE)) && y <= CANVAS_PADDING+(j*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+PE_DRAW_SIZE) {
                        row = j;
                    }
                }
            }
        }

        // if was clicked on PE
        if(row != -1) {

            Point p = MouseInfo.getPointerInfo().getLocation();

            FuFunctionsDialog dialog = new FuFunctionsDialog(row, column, model.getCrc().getFu(row, column).getFunctions());

            dialog.setX(p.x-100);
            dialog.setY(p.y-80);

            dialog.showAndWait();

            // data was changed -> update model
            if(dialog.modelHasChanged) {
                controller.setFuFunctions(row, column, dialog.getFuFunctions());
            }
        }
    }


}

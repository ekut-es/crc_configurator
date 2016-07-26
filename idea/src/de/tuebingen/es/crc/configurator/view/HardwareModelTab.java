package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.Model;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class HardwareModelTab extends ConfiguratorTab {

    private Model model;
    private GraphicsContext gc;

    public HardwareModelTab(Model model) {
        super();

        this.model = model;

        this.setText("Hardware Model");

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

        this.gc = canvas.getGraphicsContext2D();
        this.drawHardwareModelCrc(this.gc);

        this.setContent(scrollPane);
    }

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
                this.drawHardwareModelCrc(this.gc);
            }
        }
    }
}

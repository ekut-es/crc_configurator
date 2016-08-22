package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.Controller;
import de.tuebingen.es.crc.configurator.model.FU;
import de.tuebingen.es.crc.configurator.model.Model;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class ConfigurationTab extends ConfiguratorTab {

    private Model model;
    private Controller controller;
    private GraphicsContext gc;

    final int peDrawSizeTwentieth = (PE_DRAW_SIZE/20);

    public ConfigurationTab(Model model, Controller controller) {
        super();

        this.model = model;
        this.controller = controller;

        this.setText("TODO Configuration");

        this.setup();
        this.drawConfigurationCrc();

    }

    @Override
    public void update() {
        if(model.wasCrcResized()) {
            this.setup();
        }
        this.drawConfigurationCrc();
    }

    private void setup() {
        Canvas canvas = new Canvas();
        canvas.setHeight(2*CANVAS_PADDING+(model.getCrc().getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))-INTER_PE_DISTANCE);
        canvas.setWidth(2*CANVAS_PADDING+(model.getCrc().getColumns()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+INTER_PE_DISTANCE);

        // listen for double clicks in the hardware model tab
        /*
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if(event.getButton().equals(MouseButton.PRIMARY)) {
                        if(event.getClickCount() == 2) {
                            this.handleHardwareModelDoubleClick((int) event.getX(), (int) event.getY());
                        }
                    }
                }
        );
        */

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        this.setContent(scrollPane);

        gc = canvas.getGraphicsContext2D();
    }

    private void drawConfigurationCrc() {

        gc.clearRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());


        for(int i = 0; i < model.getCrc().getRows(); i++) {
            for(int j = 0; j < model.getCrc().getColumns(); j++) {
                drawPe(gc, i, j);
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

        int x = CANVAS_PADDING+(column*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+INTER_PE_DISTANCE;
        int y = CANVAS_PADDING+(row*(PE_DRAW_SIZE+INTER_PE_DISTANCE));


        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // draw PE border
        gc.strokeRect(x, y, PE_DRAW_SIZE, PE_DRAW_SIZE);
        gc.fillText(row + "," + column, peDrawSizeTwentieth+x, PE_DRAW_SIZE-(peDrawSizeTwentieth)+y);

        // draw FU
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


        // draw data paths
        // northwest corner
        if(row == 0 && column == 0) {
            this.drawConnectionE0toW0(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);
            this.drawConnectionE1toW1(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);

            this.drawConnectionE0toW0(x, y, false, false);
            this.drawConnectionE1toW1(x, y, false, false);
            this.drawConnectionS0toN0(x, y);
            this.drawConnectionS1toN1(x, y);
        }

        // northmost row
        else if(row == 0 && column > 0 && column < model.getCrc().getColumns()-1) {
            this.drawConnectionE0toW0(x, y, false, false);
            this.drawConnectionE1toW1(x, y, false, false);
            this.drawConnectionS0toN0(x, y);
            this.drawConnectionS1toN1(x, y);
        }

        // northeast corner
        else if(row == 0 && column == model.getCrc().getColumns()-1) {
            this.drawConnectionE0toW0(x, y, true, false);
            this.drawConnectionE1toW1(x, y, true, false);
            this.drawConnectionS0toN0(x, y);
            this.drawConnectionS1toN1(x, y);
        }

        // eastmost column
        else if(row > 0 && row < model.getCrc().getRows()-1 && column == model.getCrc().getColumns()-1) {
            this.drawConnectionE0toW0(x, y, true, false);
            this.drawConnectionE1toW1(x, y, true, false);
            this.drawConnectionS0toN0(x, y);
            this.drawConnectionS1toN1(x, y);
            this.drawConnectionN0toS0(x, y);
            this.drawConnectionN1toS1(x, y);
        }

        // southeast corner
        else if(row == model.getCrc().getRows()-1 && column == model.getCrc().getColumns()-1) {
            this.drawConnectionE0toW0(x, y, true, false);
            this.drawConnectionE1toW1(x, y, true, false);
            this.drawConnectionN0toS0(x, y);
            this.drawConnectionN1toS1(x, y);
        }

        // southmost row
        else if(row == model.getCrc().getRows()-1 && column > 0 && column < model.getCrc().getColumns()-1) {
            this.drawConnectionE0toW0(x, y, false, false);
            this.drawConnectionE1toW1(x, y, false, false);
            this.drawConnectionN0toS0(x, y);
            this.drawConnectionN1toS1(x, y);
        }

        // southwest corner
        else if(row == model.getCrc().getRows()-1 && column == 0) {
            this.drawConnectionE0toW0(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);
            this.drawConnectionE1toW1(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);

            this.drawConnectionE0toW0(x, y, false, false);
            this.drawConnectionE1toW1(x, y, false, false);
            this.drawConnectionN0toS0(x, y);
            this.drawConnectionN1toS1(x, y);
        }

        // westmost column
        else if(row > 0 && row < model.getCrc().getRows()-1 && column == 0) {
            this.drawConnectionE0toW0(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);
            this.drawConnectionE1toW1(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);

            this.drawConnectionE0toW0(x, y, false, false);
            this.drawConnectionE1toW1(x, y, false, false);
            this.drawConnectionN0toS0(x, y);
            this.drawConnectionN1toS1(x, y);
            this.drawConnectionS0toN0(x, y);
            this.drawConnectionS1toN1(x, y);
        }

        // center
        else {
            this.drawConnectionE0toW0(x, y, false, false);
            this.drawConnectionE1toW1(x, y, false, false);
            this.drawConnectionN0toS0(x, y);
            this.drawConnectionN1toS1(x, y);
            this.drawConnectionS0toN0(x, y);
            this.drawConnectionS1toN1(x, y);
        }
    }

    private void drawConnectionN0toS0(double x, double y) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+3*peDrawSizeTwentieth, y-3, x+3*peDrawSizeTwentieth, y-INTER_PE_DISTANCE+4);
        gc.strokePolygon(
                new double[] {
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth-3,
                        x+3*peDrawSizeTwentieth+3
                },
                new double[] {
                        y-INTER_PE_DISTANCE+3,
                        y-INTER_PE_DISTANCE+8,
                        y-INTER_PE_DISTANCE+8
                },
                3
        );

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("N0", x+(peDrawSizeTwentieth/2), y-peDrawSizeTwentieth);
        gc.fillText("S0", x+(peDrawSizeTwentieth/2), y-INTER_PE_DISTANCE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionN1toS1(double x, double y) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+7*peDrawSizeTwentieth, y-3, x+7*peDrawSizeTwentieth, y-INTER_PE_DISTANCE+4);
        gc.strokePolygon(
                new double[] {
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-3,
                        x+7*peDrawSizeTwentieth+3
                },
                new double[] {
                        y-INTER_PE_DISTANCE+3,
                        y-INTER_PE_DISTANCE+8,
                        y-INTER_PE_DISTANCE+8
                },
                3
        );

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("N1", x+4.5*peDrawSizeTwentieth, y-peDrawSizeTwentieth);
        gc.fillText("S1", x+4.5*peDrawSizeTwentieth, y-INTER_PE_DISTANCE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionS0toN0(double x, double y) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+13*peDrawSizeTwentieth, y+PE_DRAW_SIZE+3, x+13*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-4);
        gc.strokePolygon(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth-3,
                        x+13*peDrawSizeTwentieth+3
                },
                new double[] {
                        y+PE_DRAW_SIZE+INTER_PE_DISTANCE-3,
                        y+PE_DRAW_SIZE+INTER_PE_DISTANCE-8,
                        y+PE_DRAW_SIZE+INTER_PE_DISTANCE-8
                },
                3
        );

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("S0", x+10.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-peDrawSizeTwentieth);
        gc.fillText("N0", x+10.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionS1toN1(double x, double y) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+17*peDrawSizeTwentieth, y+PE_DRAW_SIZE+3, x+17*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-4);
        gc.strokePolygon(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth-3,
                        x+17*peDrawSizeTwentieth+3
                },
                new double[] {
                        y+PE_DRAW_SIZE+INTER_PE_DISTANCE-3,
                        y+PE_DRAW_SIZE+INTER_PE_DISTANCE-8,
                        y+PE_DRAW_SIZE+INTER_PE_DISTANCE-8
                },
                3
        );

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("S1", x+14.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-peDrawSizeTwentieth);
        gc.fillText("N1", x+14.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionE0toW0(double x, double y, boolean crcOutput, boolean crcInput) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+PE_DRAW_SIZE+3, y+8*peDrawSizeTwentieth, x+PE_DRAW_SIZE+INTER_PE_DISTANCE-4, y+8*peDrawSizeTwentieth);
        gc.strokePolygon(
                new double[] {
                        x+PE_DRAW_SIZE+INTER_PE_DISTANCE-3,
                        x+PE_DRAW_SIZE+INTER_PE_DISTANCE-8,
                        x+PE_DRAW_SIZE+INTER_PE_DISTANCE-8
                },
                new double[] {
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth-3,
                        y+8*peDrawSizeTwentieth+3
                },
                3
        );

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        if(!crcInput) {
            gc.fillText("E0", x + PE_DRAW_SIZE + (peDrawSizeTwentieth / 2), y + 7 * peDrawSizeTwentieth);
        }

        if(!crcOutput) {
            gc.fillText("W0", x + PE_DRAW_SIZE + INTER_PE_DISTANCE - 2.5 * peDrawSizeTwentieth, y + 7 * peDrawSizeTwentieth);
        }

    }

    private void drawConnectionE1toW1(double x, double y, boolean crcOutput, boolean crcInput) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+PE_DRAW_SIZE+3, y+12*peDrawSizeTwentieth, x+PE_DRAW_SIZE+INTER_PE_DISTANCE-4, y+12*peDrawSizeTwentieth);
        gc.strokePolygon(
                new double[] {
                        x+PE_DRAW_SIZE+INTER_PE_DISTANCE-3,
                        x+PE_DRAW_SIZE+INTER_PE_DISTANCE-8,
                        x+PE_DRAW_SIZE+INTER_PE_DISTANCE-8
                },
                new double[] {
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth-3,
                        y+12*peDrawSizeTwentieth+3
                },
                3
        );

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        if(!crcInput) {
            gc.fillText("E1", x + PE_DRAW_SIZE + (peDrawSizeTwentieth / 2), y + 11 * peDrawSizeTwentieth);
        }

        if(!crcOutput) {
            gc.fillText("W1", x + PE_DRAW_SIZE + INTER_PE_DISTANCE - 2.5 * peDrawSizeTwentieth, y + 11 * peDrawSizeTwentieth);
        }
    }
}

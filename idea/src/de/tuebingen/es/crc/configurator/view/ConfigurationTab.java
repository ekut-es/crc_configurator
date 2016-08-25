package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.Controller;
import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class ConfigurationTab extends ConfiguratorTab {

    public enum ConfigurationTabType {
        STATIC, DYNAMIC
    }

    private Model model;
    private Controller controller;
    private GraphicsContext gc;
    private int number;
    private ConfigurationTabType configurationTabType;

    private final int peDrawSizeTwentieth = (PE_DRAW_SIZE/20);

    public ConfigurationTab(Model model, Controller controller, ConfigurationTabType configurationTabType, int number) {
        super();

        this.model = model;
        this.controller = controller;
        this.configurationTabType = configurationTabType;
        this.number = number;

        if(this.configurationTabType == ConfigurationTabType.STATIC) {
            this.setText("Static Configuration " + number);
        } else {
            this.setText("Dynamic Configuration " + number);
        }

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
        this.drawFU(gc, x, y);

        // draw function into FU
        PE.FUFunction fuFunction = model.getCrc().getStaticConfiguration(number).getPE(row, column).getFUFunction();


        String fuFunctionString;
        double fuFunctionStringOffset = 0;

        switch (fuFunction) {
            case add:
                fuFunctionString = "+";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case sub:
                fuFunctionString = "−";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case mul:
                fuFunctionString = "×";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case div:
                fuFunctionString = "÷";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case and:
                fuFunctionString = "AND";
                fuFunctionStringOffset = 1.3*peDrawSizeTwentieth;
                break;
            case or:
                fuFunctionString = "OR";
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case xor:
                fuFunctionString = "XOR";
                fuFunctionStringOffset = 1.2*peDrawSizeTwentieth;
                break;
            case not:
                fuFunctionString = "NOT";
                fuFunctionStringOffset = 1.2*peDrawSizeTwentieth;
                break;
            case shift_left:
                fuFunctionString = "<<";
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case shift_right:
                fuFunctionString = ">>";
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_eq:
                fuFunctionString = "==";
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_neq:
                fuFunctionString = "!=";
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_lt:
                fuFunctionString = "<";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case compare_gt:
                fuFunctionString = ">";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case compare_leq:
                fuFunctionString = "<=";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case compare_geq:
                fuFunctionString = ">=";
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case mux_0:
                fuFunctionString = "MUX 0";
                fuFunctionStringOffset = 0.5*peDrawSizeTwentieth;
                break;
            case mux_1:
                fuFunctionString = "MUX 1";
                fuFunctionStringOffset = 0.5*peDrawSizeTwentieth;
                break;
            default:
                fuFunctionString = "NOP";
                fuFunctionStringOffset = 1.2*peDrawSizeTwentieth;
                break;
        }

        Font defaultFont = gc.getFont();
        Font fontBold = Font.font(defaultFont.getName(), FontWeight.BOLD, defaultFont.getSize()+2);

        gc.setFont(fontBold);
        gc.fillText(fuFunctionString, 7*peDrawSizeTwentieth+fuFunctionStringOffset+x, 8*peDrawSizeTwentieth+y);
        gc.setFont(defaultFont);

        // draw FU pads
        gc.setFill(Color.GRAY);

        gc.fillPolygon(
                new double[] {
                        0+7*peDrawSizeTwentieth+x-1,
                        0+7*peDrawSizeTwentieth+x-1,
                        0+7*peDrawSizeTwentieth+x-12-1,
                        0+7*peDrawSizeTwentieth+x-12-1
                },
                new double[] {
                        6*peDrawSizeTwentieth+y,
                        6*peDrawSizeTwentieth+y+12,
                        6*peDrawSizeTwentieth+y+12,
                        6*peDrawSizeTwentieth+y
                },
                4
        );

        gc.fillPolygon(
                new double[] {
                        0+7*peDrawSizeTwentieth+x-1,
                        0+7*peDrawSizeTwentieth+x-1,
                        0+7*peDrawSizeTwentieth+x-12-1,
                        0+7*peDrawSizeTwentieth+x-12-1
                },
                new double[] {
                        13*peDrawSizeTwentieth+y,
                        13*peDrawSizeTwentieth+y+12,
                        13*peDrawSizeTwentieth+y+12,
                        13*peDrawSizeTwentieth+y
                },
                4
        );

        gc.setFill(Color.BLACK);


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

            // internal connection
            this.drawInternalConnectionFUtoN0(x, y);
            this.drawInternalConnectionFUtoN1(x, y);
            this.drawInternalConnectionFUtoE0(x, y);
            this.drawInternalConnectionFUtoE1(x, y);
            this.drawInternalConnectionFUtoS0(x, y);
            this.drawInternalConnectionFUtoS1(x, y);

            this.drawInternalConnectionN0toInFU0(x, y);
            this.drawInternalConnectionN0toInFU1(x, y);
            this.drawInternalConnectionN0toE0(x, y);
            this.drawInternalConnectionN0toE1(x, y);
            this.drawInternalConnectionN0toS0(x, y);
            this.drawInternalConnectionN0toS1(x, y);

            this.drawInternalConnectionN1toInFU0(x, y);
            this.drawInternalConnectionN1toInFU1(x, y);
            this.drawInternalConnectionN1toE0(x, y);
            this.drawInternalConnectionN1toE1(x, y);
            this.drawInternalConnectionN1toS0(x, y);
            this.drawInternalConnectionN1toS1(x, y);

            this.drawInternalConnectionS0toInFU0(x, y);
            this.drawInternalConnectionS0toInFU1(x, y);
            this.drawInternalConnectionS0toE0(x, y);
            this.drawInternalConnectionS0toE1(x, y);
            this.drawInternalConnectionS0toN0(x, y);
            this.drawInternalConnectionS0toN1(x, y);

            this.drawInternalConnectionS1toInFU0(x, y);
            this.drawInternalConnectionS1toInFU1(x, y);
            this.drawInternalConnectionS1toE0(x, y);
            this.drawInternalConnectionS1toE1(x, y);
            this.drawInternalConnectionS1toN0(x, y);
            this.drawInternalConnectionS1toN1(x, y);

            this.drawInternalConnectionW0toInFU0(x, y);
            this.drawInternalConnectionW0toInFU1(x, y);
            this.drawInternalConnectionW0toN0(x, y);
            this.drawInternalConnectionW0toN1(x, y);
            this.drawInternalConnectionW0toS0(x, y);
            this.drawInternalConnectionW0toS1(x, y);

            this.drawInternalConnectionW1toInFU0(x, y);
            this.drawInternalConnectionW1toInFU1(x, y);
            this.drawInternalConnectionW1toN0(x, y);
            this.drawInternalConnectionW1toN1(x, y);
            this.drawInternalConnectionW1toS0(x, y);
            this.drawInternalConnectionW1toS1(x, y);
        }
    }

    private void drawConnectionN0toS0(double x, double y) {

        // line
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+3*peDrawSizeTwentieth, y-3, x+3*peDrawSizeTwentieth, y-INTER_PE_DISTANCE+4);

        // arrow tip
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

        // pad
        gc.setFill(Color.GRAY);
        gc.fillPolygon(
                new double[] {
                        x+3*peDrawSizeTwentieth-6,
                        x+3*peDrawSizeTwentieth+6,
                        x+3*peDrawSizeTwentieth+6,
                        x+3*peDrawSizeTwentieth-6
                },
                new double[] {
                        y+1,
                        y+1,
                        y+13,
                        y+13
                },
                4
        );

        // text
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("N0", x+(peDrawSizeTwentieth/2), y-peDrawSizeTwentieth);
        gc.fillText("S0", x+(peDrawSizeTwentieth/2), y-INTER_PE_DISTANCE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionN1toS1(double x, double y) {

        // line
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+7*peDrawSizeTwentieth, y-3, x+7*peDrawSizeTwentieth, y-INTER_PE_DISTANCE+4);

        // arrow tip
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

        // pad
        gc.setFill(Color.GRAY);
        gc.fillPolygon(
                new double[] {
                        x+7*peDrawSizeTwentieth-6,
                        x+7*peDrawSizeTwentieth+6,
                        x+7*peDrawSizeTwentieth+6,
                        x+7*peDrawSizeTwentieth-6
                },
                new double[] {
                        y+1,
                        y+1,
                        y+13,
                        y+13
                },
                4
        );

        // text
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("N1", x+4.5*peDrawSizeTwentieth, y-peDrawSizeTwentieth);
        gc.fillText("S1", x+4.5*peDrawSizeTwentieth, y-INTER_PE_DISTANCE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionS0toN0(double x, double y) {

        // line
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+13*peDrawSizeTwentieth, y+PE_DRAW_SIZE+3, x+13*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-4);

        // arrow tip
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

        // pad
        gc.setFill(Color.GRAY);
        gc.fillPolygon(
                new double[] {
                        x+13*peDrawSizeTwentieth-6,
                        x+13*peDrawSizeTwentieth+6,
                        x+13*peDrawSizeTwentieth+6,
                        x+13*peDrawSizeTwentieth-6
                },
                new double[] {
                        y+PE_DRAW_SIZE-1,
                        y+PE_DRAW_SIZE-1,
                        y+PE_DRAW_SIZE-13,
                        y+PE_DRAW_SIZE-13
                },
                4
        );

        // text
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("N0", x+10.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-peDrawSizeTwentieth);
        gc.fillText("S0", x+10.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionS1toN1(double x, double y) {

        // line
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+17*peDrawSizeTwentieth, y+PE_DRAW_SIZE+3, x+17*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-4);

        // arrow tip
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

        // pad
        gc.setFill(Color.GRAY);
        gc.fillPolygon(
                new double[] {
                        x+17*peDrawSizeTwentieth-6,
                        x+17*peDrawSizeTwentieth+6,
                        x+17*peDrawSizeTwentieth+6,
                        x+17*peDrawSizeTwentieth-6
                },
                new double[] {
                        y+PE_DRAW_SIZE-1,
                        y+PE_DRAW_SIZE-1,
                        y+PE_DRAW_SIZE-13,
                        y+PE_DRAW_SIZE-13
                },
                4
        );

        // text
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillText("N1", x+14.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+INTER_PE_DISTANCE-peDrawSizeTwentieth);
        gc.fillText("S1", x+14.5*peDrawSizeTwentieth, y+PE_DRAW_SIZE+2*peDrawSizeTwentieth);
    }

    private void drawConnectionE0toW0(double x, double y, boolean crcOutput, boolean crcInput) {

        // line
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+PE_DRAW_SIZE+3, y+8*peDrawSizeTwentieth, x+PE_DRAW_SIZE+INTER_PE_DISTANCE-4, y+8*peDrawSizeTwentieth);

        // arrow tip
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

        // pad
        if(!crcInput) {
            gc.setFill(Color.GRAY);
            gc.fillPolygon(
                    new double[]{
                            x + PE_DRAW_SIZE - 1,
                            x + PE_DRAW_SIZE - 1,
                            x + PE_DRAW_SIZE - 12 - 1,
                            x + PE_DRAW_SIZE - 12 - 1
                    },
                    new double[]{
                            y + 8 * peDrawSizeTwentieth - 6,
                            y + 8 * peDrawSizeTwentieth + 6,
                            y + 8 * peDrawSizeTwentieth + 6,
                            y + 8 * peDrawSizeTwentieth - 6
                    },
                    4
            );
        }

        // text
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);

        if(!crcInput) {
            gc.fillText("E0", x + PE_DRAW_SIZE + (peDrawSizeTwentieth / 2), y + 7 * peDrawSizeTwentieth);
        }

        if(!crcOutput) {
            gc.fillText("W0", x + PE_DRAW_SIZE + INTER_PE_DISTANCE - 2.5 * peDrawSizeTwentieth, y + 7 * peDrawSizeTwentieth);
        }

    }

    private void drawConnectionE1toW1(double x, double y, boolean crcOutput, boolean crcInput) {

        // line
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);
        gc.strokeLine(x+PE_DRAW_SIZE+3, y+12*peDrawSizeTwentieth, x+PE_DRAW_SIZE+INTER_PE_DISTANCE-4, y+12*peDrawSizeTwentieth);

        // arrow tip
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

        // pad
        if(!crcInput) {
            gc.setFill(Color.GRAY);
            gc.fillPolygon(
                    new double[]{
                            x + PE_DRAW_SIZE - 1,
                            x + PE_DRAW_SIZE - 1,
                            x + PE_DRAW_SIZE - 12 - 1,
                            x + PE_DRAW_SIZE - 12 - 1
                    },
                    new double[]{
                            y + 12 * peDrawSizeTwentieth - 6,
                            y + 12 * peDrawSizeTwentieth + 6,
                            y + 12 * peDrawSizeTwentieth + 6,
                            y + 12 * peDrawSizeTwentieth - 6
                    },
                    4
            );
        }

        // text
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);

        if(!crcInput) {
            gc.fillText("E1", x + PE_DRAW_SIZE + (peDrawSizeTwentieth / 2), y + 11 * peDrawSizeTwentieth);
        }

        if(!crcOutput) {
            gc.fillText("W1", x + PE_DRAW_SIZE + INTER_PE_DISTANCE - 2.5 * peDrawSizeTwentieth, y + 11 * peDrawSizeTwentieth);
        }
    }

    private void drawInternalConnectionFUtoN0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13.5*peDrawSizeTwentieth,
                        x+13.5*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth
                },
                new double[] {
                        y+(PE_DRAW_SIZE/2),
                        y+(PE_DRAW_SIZE/2),
                        y+3.5*peDrawSizeTwentieth,
                        y+3.5*peDrawSizeTwentieth,
                        y+12+1
                },
                5
        );

        this.drawArrowTipN0(x, y);
    }

    private void drawInternalConnectionFUtoN1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+14*peDrawSizeTwentieth,
                        x+14*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth
                },
                new double[] {
                        y+(PE_DRAW_SIZE/2),
                        y+(PE_DRAW_SIZE/2),
                        y+3*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+12+1
                },
                5
        );

        this.drawArrowTipN1(x, y);
    }

    private void drawInternalConnectionFUtoE0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[] {
                        y+(PE_DRAW_SIZE/2),
                        y+(PE_DRAW_SIZE/2),
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                4
        );

        this.drawArrowTipE0(x,y);
    }

    private void drawInternalConnectionFUtoE1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[] {
                        y+(PE_DRAW_SIZE/2),
                        y+(PE_DRAW_SIZE/2),
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                4
        );

        this.drawArrowTipE1(x,y);
    }

    private void drawInternalConnectionFUtoS0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13.5*peDrawSizeTwentieth,
                        x+13.5*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth
                },
                new double[] {
                        y+(PE_DRAW_SIZE/2),
                        y+(PE_DRAW_SIZE/2),
                        y+18*peDrawSizeTwentieth,
                        y+18*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                5
        );

        this.drawArrowTipS0(x,y);
    }

    private void drawInternalConnectionFUtoS1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+14*peDrawSizeTwentieth,
                        x+14*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth
                },
                new double[] {
                        y+(PE_DRAW_SIZE/2),
                        y+(PE_DRAW_SIZE/2),
                        y+18*peDrawSizeTwentieth,
                        y+18*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                5
        );

        this.drawArrowTipS1(x,y);
    }

    private void drawInternalConnectionN0toInFU0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+4*peDrawSizeTwentieth,
                        x+4*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU0(x,y);
    }

    private void drawInternalConnectionN0toInFU1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+4*peDrawSizeTwentieth,
                        x+4*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU1(x,y);
    }

    private void drawInternalConnectionN0toE0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                5
        );

        this.drawArrowTipE0(x,y);
    }

    private void drawInternalConnectionN0toE1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                5
        );

        this.drawArrowTipE1(x,y);
    }

    private void drawInternalConnectionN0toS0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                6
        );

        this.drawArrowTipS0(x,y);
    }

    private void drawInternalConnectionN0toS1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                6
        );

        this.drawArrowTipS1(x,y);
    }
    private void drawInternalConnectionN1toInFU0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+4.5*peDrawSizeTwentieth,
                        x+4.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU0(x,y);
    }

    private void drawInternalConnectionN1toInFU1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+4.5*peDrawSizeTwentieth,
                        x+4.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU1(x,y);
    }

    private void drawInternalConnectionN1toE0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                5
        );

        this.drawArrowTipE0(x,y);
    }

    private void drawInternalConnectionN1toE1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                5
        );

        this.drawArrowTipE1(x,y);
    }

    private void drawInternalConnectionN1toS0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                6
        );

        this.drawArrowTipS0(x,y);
    }

    private void drawInternalConnectionN1toS1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                6
        );

        this.drawArrowTipS1(x,y);
    }

    private void drawInternalConnectionS0toInFU0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y+PE_DRAW_SIZE,
                        y+18.5*peDrawSizeTwentieth,
                        y+18.5*peDrawSizeTwentieth,
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU0(x,y);
    }

    private void drawInternalConnectionS0toInFU1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y+PE_DRAW_SIZE,
                        y+18.5*peDrawSizeTwentieth,
                        y+18.5*peDrawSizeTwentieth,
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU1(x,y);
    }

    private void drawInternalConnectionS0toE0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+11.5*peDrawSizeTwentieth,
                        x+11.5*peDrawSizeTwentieth,
                        x+15.5*peDrawSizeTwentieth,
                        x+15.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+18.5*peDrawSizeTwentieth,
                        y+18.5*peDrawSizeTwentieth,
                        y+15*peDrawSizeTwentieth,
                        y+15*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                7
        );

        this.drawArrowTipE0(x, y);
    }

    private void drawInternalConnectionS0toE1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+11.5*peDrawSizeTwentieth,
                        x+11.5*peDrawSizeTwentieth,
                        x+15.5*peDrawSizeTwentieth,
                        x+15.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+18.5*peDrawSizeTwentieth,
                        y+18.5*peDrawSizeTwentieth,
                        y+15*peDrawSizeTwentieth,
                        y+15*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                7
        );

        this.drawArrowTipE0(x, y);
    }

    private void drawInternalConnectionS0toN0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                },
                new double[] {
                        y+PE_DRAW_SIZE,
                        y+12+1
                },
                2
        );

        this.drawArrowTipN0(x,y);
    }

    private void drawInternalConnectionS0toN1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth
                },
                new double[] {
                        y+PE_DRAW_SIZE,
                        y+3*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+12+1
                },
                4
        );

        this.drawArrowTipN1(x,y);
    }

    private void drawInternalConnectionS1toInFU0(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU0(x,y);
    }

    private void drawInternalConnectionS1toInFU1(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[] {
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6
                },
                5
        );

        this.drawArrowTipInFU1(x,y);
    }

    private void drawInternalConnectionS1toE0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+12*peDrawSizeTwentieth,
                        x+12*peDrawSizeTwentieth,
                        x+16*peDrawSizeTwentieth,
                        x+16*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+15.5*peDrawSizeTwentieth,
                        y+15.5*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                7
        );

        this.drawArrowTipE0(x, y);
    }

    private void drawInternalConnectionS1toE1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+12*peDrawSizeTwentieth,
                        x+12*peDrawSizeTwentieth,
                        x+16*peDrawSizeTwentieth,
                        x+16*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+15.5*peDrawSizeTwentieth,
                        y+15.5*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                7
        );

        this.drawArrowTipE1(x, y);
    }

    private void drawInternalConnectionS1toN0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+12+1
                },
                6
        );

        this.drawArrowTipN0(x, y);
    }

    private void drawInternalConnectionS1toN1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+3.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+12+1
                },
                6
        );

        this.drawArrowTipN1(x, y);
    }

    private void drawInternalConnectionW0toInFU0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6
                },
                4
        );

        this.drawArrowTipInFU0(x, y);
    }

    private void drawInternalConnectionW0toInFU1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6
                },
                4
        );

        this.drawArrowTipInFU1(x, y);
    }

    private void drawInternalConnectionW0toN0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+12+1
                },
                5
        );

        this.drawArrowTipN0(x, y);
    }

    private void drawInternalConnectionW0toN1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+12+1
                },
                5
        );

        this.drawArrowTipN1(x, y);
    }

    private void drawInternalConnectionW0toS0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                5
        );

        this.drawArrowTipS0(x, y);
    }

    private void drawInternalConnectionW0toS1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+16*peDrawSizeTwentieth,
                        y+16*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                7
        );

        this.drawArrowTipS1(x, y);
    }

    private void drawInternalConnectionW1toInFU0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6
                },
                4
        );

        this.drawArrowTipInFU0(x, y);
    }

    private void drawInternalConnectionW1toInFU1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth-12-1
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6
                },
                4
        );

        this.drawArrowTipInFU1(x, y);
    }

    private void drawInternalConnectionW1toN0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+12+1
                },
                5
        );

        this.drawArrowTipN0(x, y);
    }

    private void drawInternalConnectionW1toN1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+12+1
                },
                5
        );

        this.drawArrowTipN1(x, y);
    }

    private void drawInternalConnectionW1toS0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+12.5*peDrawSizeTwentieth,
                        x+12.5*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+18*peDrawSizeTwentieth,
                        y+18*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                7
        );

        this.drawArrowTipS0(x, y);
    }

    private void drawInternalConnectionW1toS1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+PE_DRAW_SIZE-12-1
                },
                5
        );

        this.drawArrowTipS1(x, y);
    }

    private void drawArrowTipN0(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth+4,
                        x+3*peDrawSizeTwentieth-4
                },
                new double[] {
                        y+12,
                        y+16,
                        y+16
                },
                3
        );
    }

    private void drawArrowTipN1(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth+4,
                        x+7*peDrawSizeTwentieth-4
                },
                new double[] {
                        y+12,
                        y+16,
                        y+16
                },
                3
        );
    }

    private void drawArrowTipE0(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+PE_DRAW_SIZE-12,
                        x+PE_DRAW_SIZE-16,
                        x+PE_DRAW_SIZE-16
                },
                new double[] {
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth+4,
                        y+8*peDrawSizeTwentieth-4
                },
                3
        );
    }

    private void drawArrowTipE1(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+PE_DRAW_SIZE-12,
                        x+PE_DRAW_SIZE-16,
                        x+PE_DRAW_SIZE-16
                },
                new double[] {
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth+4,
                        y+12*peDrawSizeTwentieth-4
                },
                3
        );
    }

    private void drawArrowTipS0(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth+4,
                        x+13*peDrawSizeTwentieth-4
                },
                new double[] {
                        y+PE_DRAW_SIZE-12,
                        y+PE_DRAW_SIZE-12-4,
                        y+PE_DRAW_SIZE-12-4,
                },
                3
        );
    }

    private void drawArrowTipS1(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth+4,
                        x+17*peDrawSizeTwentieth-4
                },
                new double[] {
                        y+PE_DRAW_SIZE-12,
                        y+PE_DRAW_SIZE-12-4,
                        y+PE_DRAW_SIZE-12-4,
                },
                3
        );
    }

    private void drawArrowTipInFU0(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+7*peDrawSizeTwentieth-12,
                        x+7*peDrawSizeTwentieth-12-4,
                        x+7*peDrawSizeTwentieth-12-4
                },
                new double[] {
                        y+6*peDrawSizeTwentieth+6,
                        y+6*peDrawSizeTwentieth+6+4,
                        y+6*peDrawSizeTwentieth+6-4
                },
                3
        );
    }

    private void drawArrowTipInFU1(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+7*peDrawSizeTwentieth-12,
                        x+7*peDrawSizeTwentieth-12-4,
                        x+7*peDrawSizeTwentieth-12-4
                },
                new double[] {
                        y+13*peDrawSizeTwentieth+6,
                        y+13*peDrawSizeTwentieth+6+4,
                        y+13*peDrawSizeTwentieth+6-4
                },
                3
        );
    }

}

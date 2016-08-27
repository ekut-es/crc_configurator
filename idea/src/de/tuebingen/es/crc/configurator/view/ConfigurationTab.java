package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.Controller;
import de.tuebingen.es.crc.configurator.model.Configuration;
import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.*;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class ConfigurationTab extends ConfiguratorTab implements Observer {

    public enum ConfigurationTabType {
        STATIC, DYNAMIC
    }

    private Model model;
    private Controller controller;
    private GraphicsContext gc;
    private int number;
    private ConfigurationTabType configurationTabType;

    private final int peDrawSizeTwentieth = (PE_DRAW_SIZE/20);

    private ContextMenu contextMenu;

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

        // listen for right clicks in the hardware model tab
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if(contextMenu != null && contextMenu.isShowing()) {
                        contextMenu.hide();
                    }
                    if(event.getButton().equals(MouseButton.SECONDARY)) {
                        this.handleConfigurationRightClick((int) event.getX(), (int) event.getY());
                    }
                }
        );

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        this.setContent(scrollPane);

        gc = canvas.getGraphicsContext2D();
    }

    private Configuration getConfiguration() {
        if(configurationTabType == ConfigurationTabType.STATIC) {
            return model.getCrc().getStaticConfiguration(number);
        } else {
            return model.getCrc().getDynamicConfiguration(number);
        }
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
        PE.FUFunction fuFunction = this.getConfiguration().getPE(row, column).getFUFunction();
        FuFunctionStringMap fuFunctionStringMap = new FuFunctionStringMap();

        String fuFunctionString = fuFunctionStringMap.getString(fuFunction);

        double fuFunctionStringOffset = 0;

        switch (fuFunction) {
            case add:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case sub:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case mul:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case div:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case and:
                fuFunctionStringOffset = 1.3*peDrawSizeTwentieth;
                break;
            case or:
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case xor:
                fuFunctionStringOffset = 1.2*peDrawSizeTwentieth;
                break;
            case not:
                fuFunctionStringOffset = 1.2*peDrawSizeTwentieth;
                break;
            case shift_left:
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case shift_right:
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_eq:
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_neq:
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_lt:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case compare_gt:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case compare_leq:
                fuFunctionStringOffset = 2*peDrawSizeTwentieth;
                break;
            case compare_geq:
                fuFunctionStringOffset = 2.5*peDrawSizeTwentieth;
                break;
            case mux_0:
                fuFunctionStringOffset = 0.5*peDrawSizeTwentieth;
                break;
            case mux_1:
                fuFunctionStringOffset = 0.5*peDrawSizeTwentieth;
                break;
            default:
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

        gc.fillPolygon(
                new double[] {
                        10*peDrawSizeTwentieth+x-6,
                        10*peDrawSizeTwentieth+x+6,
                        10*peDrawSizeTwentieth+x+6,
                        10*peDrawSizeTwentieth+x-6
                },
                new double[] {
                        15*peDrawSizeTwentieth+y+4-1,
                        15*peDrawSizeTwentieth+y-1,
                        14.5*peDrawSizeTwentieth+y+12,
                        14.5*peDrawSizeTwentieth+y+12
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


        }

        /*
        this.drawInternalConnectionFUtoN0(x, y);
        this.drawInternalConnectionFUtoN1(x, y);
        this.drawInternalConnectionFUtoE0(x, y);
        this.drawInternalConnectionFUtoE1(x, y);
        this.drawInternalConnectionFUtoS0(x, y);
        this.drawInternalConnectionFUtoS1(x, y);

        this.drawInternalConnectionN0toInFU0(x, y);
        this.drawInternalConnectionN0toInFU1(x, y);
        this.drawInternalConnectionN0toInFUMux(x, y);
        this.drawInternalConnectionN0toE0(x, y);
        this.drawInternalConnectionN0toE1(x, y);
        this.drawInternalConnectionN0toS0(x, y);
        this.drawInternalConnectionN0toS1(x, y);

        this.drawInternalConnectionN1toInFU0(x, y);
        this.drawInternalConnectionN1toInFU1(x, y);
        this.drawInternalConnectionN1toInFUMux(x, y);
        this.drawInternalConnectionN1toE0(x, y);
        this.drawInternalConnectionN1toE1(x, y);
        this.drawInternalConnectionN1toS0(x, y);
        this.drawInternalConnectionN1toS1(x, y);

        this.drawInternalConnectionS0toInFU0(x, y);
        this.drawInternalConnectionS0toInFU1(x, y);
        this.drawInternalConnectionS0toInFUMux(x, y);
        this.drawInternalConnectionS0toN0(x, y);
        this.drawInternalConnectionS0toN1(x, y);
        this.drawInternalConnectionS0toE0(x, y);
        this.drawInternalConnectionS0toE1(x, y);

        this.drawInternalConnectionS1toInFU0(x, y);
        this.drawInternalConnectionS1toInFU1(x, y);
        this.drawInternalConnectionS1toInFUMux(x, y);
        this.drawInternalConnectionS1toN0(x, y);
        this.drawInternalConnectionS1toN1(x, y);
        this.drawInternalConnectionS1toE0(x, y);
        this.drawInternalConnectionS1toE1(x, y);

        this.drawInternalConnectionW0toInFU0(x, y);
        this.drawInternalConnectionW0toInFU1(x, y);
        this.drawInternalConnectionW0toInFUMux(x, y);
        this.drawInternalConnectionW0toN0(x, y);
        this.drawInternalConnectionW0toN1(x, y);
        this.drawInternalConnectionW0toE0(x, y);
        this.drawInternalConnectionW0toE1(x, y);
        this.drawInternalConnectionW0toS0(x, y);
        this.drawInternalConnectionW0toS1(x, y);

        this.drawInternalConnectionW1toInFU0(x, y);
        this.drawInternalConnectionW1toInFU1(x, y);
        this.drawInternalConnectionW1toInFUMux(x, y);
        this.drawInternalConnectionW1toN0(x, y);
        this.drawInternalConnectionW1toN1(x, y);
        this.drawInternalConnectionW1toE0(x, y);
        this.drawInternalConnectionW1toE1(x, y);
        this.drawInternalConnectionW1toS1(x, y);
        this.drawInternalConnectionW1toS1(x, y);
        */

        // internal connection
        // * -> FU0
        PE.DataFlagInFuDriver dataFlagInFu0Driver = this.getConfiguration().getPE(row, column).getDataFlagInFU0();

        switch (dataFlagInFu0Driver) {
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toInFU0(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toInFU0(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toInFU0(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toInFU0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toInFU0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toInFU0(x, y);
                break;
        }

        // * -> FU1
        PE.DataFlagInFuDriver dataFlagInFu1Driver = this.getConfiguration().getPE(row, column).getDataFlagInFU1();

        switch (dataFlagInFu1Driver) {
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toInFU1(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toInFU1(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toInFU1(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toInFU1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toInFU1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toInFU1(x, y);
                break;
        }

        // * -> FUMux
        PE.DataFlagInFuDriver flagInFuMuxDriver = this.getConfiguration().getPE(row, column).getFlagInFUMux();

        switch (flagInFuMuxDriver) {
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toInFUMux(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toInFUMux(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toInFUMux(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toInFUMux(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toInFUMux(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toInFUMux(x, y);
                break;
        }

        // * -> N0
        PE.DataFlagOutDriver dataFlagOutN0Driver = this.getConfiguration().getPE(row, column).getDataFlagOutN0();

        switch (dataFlagOutN0Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFUtoN0(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toN0(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toN0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toN0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toN0(x, y);
                break;
        }

        // * -> N1
        PE.DataFlagOutDriver dataFlagOutN1Driver = this.getConfiguration().getPE(row, column).getDataFlagOutN1();

        switch (dataFlagOutN1Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFUtoN1(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toN1(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toN1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toN1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toN1(x, y);
                break;
        }

        // * -> E0
        PE.DataFlagOutDriver dataFlagOutE0Driver = this.getConfiguration().getPE(row, column).getDataFlagOutE0();

        switch (dataFlagOutE0Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFUtoE0(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toE0(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toE0(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toE0(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toE0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toE0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toE0(x, y);
                break;
        }

        // * -> E1
        PE.DataFlagOutDriver dataFlagOutE1Driver = this.getConfiguration().getPE(row, column).getDataFlagOutE1();

        switch (dataFlagOutE1Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFUtoE1(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toE1(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toE1(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0toE1(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1toE1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toE1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toE1(x, y);
                break;
        }

        // * -> S0
        PE.DataFlagOutDriver dataFlagOutS0Driver = this.getConfiguration().getPE(row, column).getDataFlagOutS0();

        switch (dataFlagOutS0Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFUtoS0(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toS0(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toS0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toS0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toS0(x, y);
                break;
        }

        // * -> S1
        PE.DataFlagOutDriver dataFlagOutS1Driver = this.getConfiguration().getPE(row, column).getDataFlagOutS1();

        switch (dataFlagOutS1Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFUtoS1(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0toS1(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1toS1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0toS1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1toS1(x, y);
                break;
        }


        if(!this.getConfiguration().getPE(row, column).isActive()) {
            this.drawInternalConnectionInactive(x, y);
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

    private void drawInternalConnectionN0toInFUMux(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+14.5*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth
                },
                new double[] {
                        y,
                        y+2*peDrawSizeTwentieth,
                        y+2*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+18*peDrawSizeTwentieth,
                        y+18*peDrawSizeTwentieth,
                        y+14.5*peDrawSizeTwentieth+12+1
                },
                8
        );

        this.drawArrowTipInFUMux(x,y);
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

   private void drawInternalConnectionN1toInFUMux(double x, double y) {

        gc.strokePolyline(
                new double[] {
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth
                },
                new double[] {
                        y,
                        y+2.5*peDrawSizeTwentieth,
                        y+2.5*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+17.5*peDrawSizeTwentieth,
                        y+14.5*peDrawSizeTwentieth+12+1
                },
                8
        );

        this.drawArrowTipInFUMux(x,y);
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

    private void drawInternalConnectionS0toInFUMux(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+3*peDrawSizeTwentieth,
                        x+3*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+18.5*peDrawSizeTwentieth,
                        y+18.5*peDrawSizeTwentieth,
                        y+14.5*peDrawSizeTwentieth+12+1,
                },
                4
        );

        this.drawArrowTipInFUMux(x, y);
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

        this.drawArrowTipE1(x, y);
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

    private void drawInternalConnectionS1toInFUMux(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x+7*peDrawSizeTwentieth,
                        x+7*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                },
                new double[]{
                        y+PE_DRAW_SIZE,
                        y+19*peDrawSizeTwentieth,
                        y+19*peDrawSizeTwentieth,
                        y+14.5*peDrawSizeTwentieth+12+1,
                },
                4
        );

        this.drawArrowTipInFUMux(x, y);
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

    private void drawInternalConnectionW0toInFUMux(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+14.5*peDrawSizeTwentieth+12+1
                },
                5
        );

        this.drawArrowTipInFUMux(x, y);
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

    private void drawInternalConnectionW0toE0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+16*peDrawSizeTwentieth,
                        y+16*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                8
        );

        this.drawArrowTipE0(x, y);
    }

    private void drawInternalConnectionW0toE1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+13*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+17*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+16*peDrawSizeTwentieth,
                        y+16*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                8
        );

        this.drawArrowTipE1(x, y);
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

    private void drawInternalConnectionW1toInFUMux(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+14.5*peDrawSizeTwentieth+12+1
                },
                5
        );

        this.drawArrowTipInFUMux(x, y);
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

    private void drawInternalConnectionW1toE0(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+16.5*peDrawSizeTwentieth,
                        x+16.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth

                },
                6
        );

        this.drawArrowTipE0(x, y);
    }

    private void drawInternalConnectionW1toE1(double x, double y) {

        gc.strokePolyline(
                new double[]{
                        x,
                        x+2.5*peDrawSizeTwentieth,
                        x+2.5*peDrawSizeTwentieth,
                        x+16.5*peDrawSizeTwentieth,
                        x+16.5*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-1
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+16.5*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth

                },
                6
        );

        this.drawArrowTipE1(x, y);
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

    private void drawArrowTipInFUMux(double x, double y) {
        gc.fillPolygon(
                new double[] {
                        x+10*peDrawSizeTwentieth,
                        x+10*peDrawSizeTwentieth+4,
                        x+10*peDrawSizeTwentieth-4
                },
                new double[] {
                        y+14.5*peDrawSizeTwentieth+12,
                        y+14.5*peDrawSizeTwentieth+12+4,
                        y+14.5*peDrawSizeTwentieth+12+4
                },
                3
        );
    }

    private void drawInternalConnectionInactive(double x, double y) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(4);

        // arrow E0
        gc.strokePolygon(
                new double[] {
                        x+PE_DRAW_SIZE-12-1-3,
                        x+PE_DRAW_SIZE-12-1-8,
                        x+PE_DRAW_SIZE-12-1-8
                },
                new double[] {
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth-3,
                        y+8*peDrawSizeTwentieth+3
                },
                3
        );

        // arrow E1
        gc.strokePolygon(
                new double[] {
                        x+PE_DRAW_SIZE-12-1-3,
                        x+PE_DRAW_SIZE-12-1-8,
                        x+PE_DRAW_SIZE-12-1-8
                },
                new double[] {
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth-3,
                        y+12*peDrawSizeTwentieth+3
                },
                3
        );

        // E0 -> W0
        gc.strokePolyline(
                new double[]{
                        x+3,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-5
                },
                new double[]{
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+3*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth,
                        y+8*peDrawSizeTwentieth
                },
                6
        );

        // E1 -> W1
        gc.strokePolyline(
                new double[]{
                        x+3,
                        x+2*peDrawSizeTwentieth,
                        x+2*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+15*peDrawSizeTwentieth,
                        x+PE_DRAW_SIZE-12-5
                },
                new double[]{
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+17*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth,
                        y+12*peDrawSizeTwentieth
                },
                6
        );

        gc.setFill(Color.BLACK);
        gc.setLineWidth(2);
    }

    private void handleConfigurationRightClick(int x, int y) {

        // decide if the inside of a PE was clicked
        int row = -1;
        int column = -1;

        for(int i = 0; i < model.getCrc().getColumns(); i++) {

            if(x >= CANVAS_PADDING+(i*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+INTER_PE_DISTANCE && x <= CANVAS_PADDING+(i*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+PE_DRAW_SIZE+INTER_PE_DISTANCE) {

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

            int xOffset = CANVAS_PADDING+INTER_PE_DISTANCE+(column*(PE_DRAW_SIZE+INTER_PE_DISTANCE));
            int yOffset = CANVAS_PADDING+(row*(PE_DRAW_SIZE+INTER_PE_DISTANCE));

            int xNormalized = x - xOffset;
            int yNormalized = y - yOffset;

            Point p = MouseInfo.getPointerInfo().getLocation();
            int finalRow = row;
            int finalColumn = column;

            // check if a pad and which pad was clicked

            // FU Function
             if(
                    xNormalized >= 7.5*peDrawSizeTwentieth &&
                    xNormalized <= 12.5*peDrawSizeTwentieth &&
                    yNormalized >= 6.5*peDrawSizeTwentieth &&
                    yNormalized <= 8.5*peDrawSizeTwentieth) {

                 FuFunctionContextMenu fuFunctionContextMenu = new FuFunctionContextMenu(model.getCrc().getFu(row, column), this.getConfiguration().getPE(row, column).getFUFunction());
                 contextMenu = fuFunctionContextMenu;
                 fuFunctionContextMenu.show(this.getContent(), p.x, p.y);

                 fuFunctionContextMenu.setOnHiding(event -> {
                     if(fuFunctionContextMenu.getSelectedFuFunction() != PE.FUFunction.none) {
                        controller.setPeFunction(configurationTabType, number, finalRow, finalColumn, fuFunctionContextMenu.getSelectedFuFunction());
                     }
                 });
            }


            // in FU0
            if(
                    xNormalized >= 7*peDrawSizeTwentieth-12-4 &&
                    xNormalized <= 7*peDrawSizeTwentieth+4 &&
                    yNormalized >= 6*peDrawSizeTwentieth-4 &&
                    yNormalized <= 6*peDrawSizeTwentieth+12+4) {

                DataFlagInFuDriverContextMenu dataFlagInFuDriverContextMenu = new DataFlagInFuDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagInFU0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagInFuDriverContextMenu;
                dataFlagInFuDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagInFuDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagInFU0()) {
                        controller.setPeDataFlagInFu0Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagInFuDriver.none);
                    }
                    else if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() != PE.DataFlagInFuDriver.none) {
                        controller.setPeDataFlagInFu0Driver(configurationTabType, number, finalRow, finalColumn, dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver());
                    }
                });

            }

            // in FU1
            if(
                    xNormalized >= 7*peDrawSizeTwentieth-12-4 &&
                    xNormalized <= 7*peDrawSizeTwentieth+4 &&
                    yNormalized >= 13*peDrawSizeTwentieth-4 &&
                    yNormalized <= 13*peDrawSizeTwentieth+12+4) {

                DataFlagInFuDriverContextMenu dataFlagInFuDriverContextMenu = new DataFlagInFuDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagInFU1(), model.getCrc().getRows(), row);
                contextMenu = dataFlagInFuDriverContextMenu;
                dataFlagInFuDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagInFuDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagInFU1()) {
                        controller.setPeDataFlagInFu1Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagInFuDriver.none);
                    }
                    else if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() != PE.DataFlagInFuDriver.none) {
                        controller.setPeDataFlagInFu1Driver(configurationTabType, number, finalRow, finalColumn, dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver());
                    }
                });
            }

            // in FUMux
            if(
                    xNormalized >= 10*peDrawSizeTwentieth-6-4 &&
                    xNormalized <= 10*peDrawSizeTwentieth+6+4 &&
                    yNormalized >= 15*peDrawSizeTwentieth &&
                    yNormalized <= 14.5*peDrawSizeTwentieth+12+4) {

                DataFlagInFuDriverContextMenu dataFlagInFuDriverContextMenu = new DataFlagInFuDriverContextMenu(this.getConfiguration().getPE(row, column).getFlagInFUMux(), model.getCrc().getRows(), row);
                contextMenu = dataFlagInFuDriverContextMenu;
                dataFlagInFuDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagInFuDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getFlagInFUMux()) {
                        controller.setPeFlagInFuMuxDriver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagInFuDriver.none);
                    }
                    else if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() != PE.DataFlagInFuDriver.none) {
                        controller.setPeFlagInFuMuxDriver(configurationTabType, number, finalRow, finalColumn, dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver());
                    }
                });
            }


            // in N0
            if(
                    xNormalized >= 3*peDrawSizeTwentieth-6-4 &&
                    xNormalized <= 3*peDrawSizeTwentieth+6+4 &&
                    yNormalized >= 1-4 &&
                    yNormalized <= 13+4 &&
                    row != 0) {

                DataFlagNorthDriverContextMenu dataFlagNorthDriverContextMenu = new DataFlagNorthDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagOutN0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagNorthDriverContextMenu;
                dataFlagNorthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagNorthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagOutN0()) {
                        controller.setPeDataFlagN0Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagOutDriver.none);
                    }
                    else if(dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver() != PE.DataFlagOutDriver.none) {
                        controller.setPeDataFlagN0Driver(configurationTabType, number, finalRow, finalColumn, dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver());
                    }
                });
            }

            // in N1
            if(
                    xNormalized >= 7*peDrawSizeTwentieth-6-4 &&
                    xNormalized <= 7*peDrawSizeTwentieth+6+4 &&
                    yNormalized >= 1-4 &&
                    yNormalized <= 13+4 &&
                    row != 0) {

                DataFlagNorthDriverContextMenu dataFlagNorthDriverContextMenu = new DataFlagNorthDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagOutN1(), model.getCrc().getRows(), row);
                contextMenu = dataFlagNorthDriverContextMenu;
                dataFlagNorthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagNorthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagOutN1()) {
                        controller.setPeDataFlagN1Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagOutDriver.none);
                    }
                    else if(dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver() != PE.DataFlagOutDriver.none) {
                        controller.setPeDataFlagN1Driver(configurationTabType, number, finalRow, finalColumn, dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver());
                    }
                });
            }

            // in E0
            if(
                    xNormalized >= PE_DRAW_SIZE-12-4 &&
                    xNormalized <= PE_DRAW_SIZE-1+4 &&
                    yNormalized >= 8*peDrawSizeTwentieth-6-4 &&
                    yNormalized <= 8*peDrawSizeTwentieth+6+4) {

                DataFlagEastDriverContextMenu dataFlagEastDriverContextMenu = new DataFlagEastDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagOutE0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagEastDriverContextMenu;
                dataFlagEastDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagEastDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagOutE0()) {
                        controller.setPeDataFlagE0Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagOutDriver.none);
                    }
                    else if(dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver() != PE.DataFlagOutDriver.none) {
                        controller.setPeDataFlagE0Driver(configurationTabType, number, finalRow, finalColumn, dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver());
                    }
                });
            }

            // in E1
            if(
                    xNormalized >= PE_DRAW_SIZE-12-4 &&
                    xNormalized <= PE_DRAW_SIZE-1+4 &&
                    yNormalized >= 12*peDrawSizeTwentieth-6-4 &&
                    yNormalized <= 12*peDrawSizeTwentieth+6+4) {

                DataFlagEastDriverContextMenu dataFlagEastDriverContextMenu = new DataFlagEastDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagOutE1(), model.getCrc().getRows(), row);
                contextMenu = dataFlagEastDriverContextMenu;
                dataFlagEastDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagEastDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagOutE1()) {
                        controller.setPeDataFlagE1Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagOutDriver.none);
                    }
                    else if(dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver() != PE.DataFlagOutDriver.none) {
                        controller.setPeDataFlagE1Driver(configurationTabType, number, finalRow, finalColumn, dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver());
                    }
                });
            }

            // in S0
            if(
                    xNormalized >= 13*peDrawSizeTwentieth-6-4 &&
                    xNormalized <= 13*peDrawSizeTwentieth+6+4 &&
                    yNormalized >= PE_DRAW_SIZE-13-4 &&
                    yNormalized <= PE_DRAW_SIZE-1+4 &&
                    row != model.getCrc().getRows()-1) {

                DataFlagSouthDriverContextMenu dataFlagSouthDriverContextMenu = new DataFlagSouthDriverContextMenu(this.getConfiguration().getPE(row, column).getDataFlagOutS0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagSouthDriverContextMenu;
                dataFlagSouthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagSouthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver() == this.getConfiguration().getPE(finalRow, finalColumn).getDataFlagOutS0()) {
                        controller.setPeDataFlagS0Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagOutDriver.none);
                    }
                    else if(dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver() != PE.DataFlagOutDriver.none) {
                        controller.setPeDataFlagS0Driver(configurationTabType, number, finalRow, finalColumn, dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver());
                    }
                });

            }

            // in S1
            if(
                    xNormalized >= 17*peDrawSizeTwentieth-6-4 &&
                    xNormalized <= 17*peDrawSizeTwentieth+6+4 &&
                    yNormalized >= PE_DRAW_SIZE-13-4 &&
                    yNormalized <= PE_DRAW_SIZE-1+4 &&
                    row != model.getCrc().getRows()-1) {
                System.out.println("S1");
            }
        }
    }
}

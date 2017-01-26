package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.Controller;
import de.tuebingen.es.crc.configurator.model.Configuration;
import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.model.PE;
import javafx.event.*;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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

    private final Model model;
    private final Controller controller;
    private GraphicsContext gc;
    private ScrollPane scrollPane;
    private double scrollPaneVvalue;
    private double scrollPaneHvalue;
    private final int number;
    private final ConfigurationTabType configurationTabType;

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

        this.scrollPaneVvalue = 0.0;
        this.scrollPaneHvalue = 0.0;

        this.setup();
        this.drawCrcConfig();

    }

    public int getNumber() {
        return number;
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
            this.drawCrcConfig();
        }
    }

    /**
     * sets up the drawing canvas and draws config
     */
    private void setup() {

        VBox outerVBox = new VBox(2);

        //Canvas canvas = new Canvas();
        canvas = new Canvas();
        canvas.setHeight(2*CANVAS_PADDING+(model.getCrc().getRows()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))-INTER_PE_DISTANCE);
        canvas.setWidth(2*CANVAS_PADDING+(model.getCrc().getColumns()*(PE_DRAW_SIZE+INTER_PE_DISTANCE))+INTER_PE_DISTANCE);

        // listen for right clicks and double clicks in the configuration tab
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if(contextMenu != null && contextMenu.isShowing()) {
                        contextMenu.hide();
                    }

                    if(event.getButton().equals(MouseButton.SECONDARY)) {
                        this.handleConfigurationClick((int) event.getX(), (int) event.getY());
                    } else if(event.getButton().equals(MouseButton.PRIMARY)) {
                        if(event.getClickCount() == 2) {
                            this.handleConfigurationClick((int) event.getX(), (int) event.getY());
                        }
                    }
                }
        );

        scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        outerVBox.getChildren().add(scrollPane);

        TextArea commentTextArea = this.addCommentTextArea(outerVBox);
        commentTextArea.setText(this.getConfig().getComment());
        commentTextArea.textProperty().addListener((observable, oldValue, newValue) -> controller.setConfigurationComment(configurationTabType, number, newValue));

        this.setContent(outerVBox);

        scrollPane.setHvalue(scrollPaneHvalue);
        scrollPane.setVvalue(scrollPaneVvalue);

        gc = canvas.getGraphicsContext2D();
    }

    /**
     * gets the static or dynamic config for this tab
     * @return Configuration static or dynamic
     */
    private Configuration getConfig() {
        if(configurationTabType == ConfigurationTabType.STATIC) {
            return model.getCrc().getStaticConfig(number);
        } else {
            return model.getCrc().getDynamicConfig(number);
        }
    }

    /**
     * draws the config
     */
    private void drawCrcConfig() {

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
        this.drawFU(gc, x, y, false);

        // draw function into FU
        PE.FUFunction fuFunction = this.getConfig().getPe(row, column).getFuFunction();

        String fuFunctionString = FuFunctionStringMap.getString(fuFunction);

        double fuFunctionStringOffset;

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

        // draw singed or unsigned
        boolean signedData = this.getConfig().getPe(row, column).isSignedData();

        if(!signedData) {
            gc.fillText("unsigned", 7.2*peDrawSizeTwentieth+x, 13*peDrawSizeTwentieth+y);
        } else {
            gc.fillText("signed", 8*peDrawSizeTwentieth+x, 13*peDrawSizeTwentieth+y);
        }

        // draw FU pads
        gc.setFill(Color.GRAY);

        //noinspection PointlessArithmeticExpression,PointlessArithmeticExpression,PointlessArithmeticExpression,PointlessArithmeticExpression
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

        //noinspection PointlessArithmeticExpression,PointlessArithmeticExpression,PointlessArithmeticExpression,PointlessArithmeticExpression
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
            this.drawConnectionE0ToW0(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);
            this.drawConnectionE1ToW1(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);

            this.drawConnectionE0ToW0(x, y, false, false);
            this.drawConnectionE1ToW1(x, y, false, false);
            this.drawConnectionS0ToN0(x, y);
            this.drawConnectionS1ToN1(x, y);
        }

        // northmost row
        else if(row == 0 && column > 0 && column < model.getCrc().getColumns()-1) {
            this.drawConnectionE0ToW0(x, y, false, false);
            this.drawConnectionE1ToW1(x, y, false, false);
            this.drawConnectionS0ToN0(x, y);
            this.drawConnectionS1ToN1(x, y);
        }

        // northeast corner
        else if(row == 0 && column == model.getCrc().getColumns()-1) {
            this.drawConnectionE0ToW0(x, y, true, false);
            this.drawConnectionE1ToW1(x, y, true, false);
            this.drawConnectionS0ToN0(x, y);
            this.drawConnectionS1ToN1(x, y);
        }

        // eastmost column
        else if(row > 0 && row < model.getCrc().getRows()-1 && column == model.getCrc().getColumns()-1) {
            this.drawConnectionE0ToW0(x, y, true, false);
            this.drawConnectionE1ToW1(x, y, true, false);
            this.drawConnectionS0ToN0(x, y);
            this.drawConnectionS1ToN1(x, y);
            this.drawConnectionN0ToS0(x, y);
            this.drawConnectionN1ToS1(x, y);
        }

        // southeast corner
        else if(row == model.getCrc().getRows()-1 && column == model.getCrc().getColumns()-1) {
            this.drawConnectionE0ToW0(x, y, true, false);
            this.drawConnectionE1ToW1(x, y, true, false);
            this.drawConnectionN0ToS0(x, y);
            this.drawConnectionN1ToS1(x, y);
        }

        // southmost row
        else if(row == model.getCrc().getRows()-1 && column > 0 && column < model.getCrc().getColumns()-1) {
            this.drawConnectionE0ToW0(x, y, false, false);
            this.drawConnectionE1ToW1(x, y, false, false);
            this.drawConnectionN0ToS0(x, y);
            this.drawConnectionN1ToS1(x, y);
        }

        // southwest corner
        else if(row == model.getCrc().getRows()-1 && column == 0) {
            this.drawConnectionE0ToW0(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);
            this.drawConnectionE1ToW1(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);

            this.drawConnectionE0ToW0(x, y, false, false);
            this.drawConnectionE1ToW1(x, y, false, false);
            this.drawConnectionN0ToS0(x, y);
            this.drawConnectionN1ToS1(x, y);
        }

        // westmost column
        else if(row > 0 && row < model.getCrc().getRows()-1 && column == 0) {
            this.drawConnectionE0ToW0(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);
            this.drawConnectionE1ToW1(x-INTER_PE_DISTANCE-PE_DRAW_SIZE, y, false, true);

            this.drawConnectionE0ToW0(x, y, false, false);
            this.drawConnectionE1ToW1(x, y, false, false);
            this.drawConnectionN0ToS0(x, y);
            this.drawConnectionN1ToS1(x, y);
            this.drawConnectionS0ToN0(x, y);
            this.drawConnectionS1ToN1(x, y);
        }

        // center
        else {
            this.drawConnectionE0ToW0(x, y, false, false);
            this.drawConnectionE1ToW1(x, y, false, false);
            this.drawConnectionN0ToS0(x, y);
            this.drawConnectionN1ToS1(x, y);
            this.drawConnectionS0ToN0(x, y);
            this.drawConnectionS1ToN1(x, y);


        }

        /*
        this.drawInternalConnectionFuToN0(x, y);
        this.drawInternalConnectionFuToN1(x, y);
        this.drawInternalConnectionFuToE0(x, y);
        this.drawInternalConnectionFuToE1(x, y);
        this.drawInternalConnectionFuToS0(x, y);
        this.drawInternalConnectionFuToS1(x, y);

        this.drawInternalConnectionN0ToInFu0(x, y);
        this.drawInternalConnectionN0ToInFu1(x, y);
        this.drawInternalConnectionN0ToInFuMux(x, y);
        this.drawInternalConnectionN0ToE0(x, y);
        this.drawInternalConnectionN0ToE1(x, y);
        this.drawInternalConnectionN0ToS0(x, y);
        this.drawInternalConnectionN0ToS1(x, y);

        this.drawInternalConnectionN1ToInFu0(x, y);
        this.drawInternalConnectionN1ToInFu1(x, y);
        this.drawInternalConnectionN1ToInFuMux(x, y);
        this.drawInternalConnectionN1ToE0(x, y);
        this.drawInternalConnectionN1ToE1(x, y);
        this.drawInternalConnectionN1ToS0(x, y);
        this.drawInternalConnectionN1ToS1(x, y);

        this.drawInternalConnectionS0ToInFu0(x, y);
        this.drawInternalConnectionS0ToInFu1(x, y);
        this.drawInternalConnectionS0ToInFuMux(x, y);
        this.drawInternalConnectionS0ToN0(x, y);
        this.drawInternalConnectionS0ToN1(x, y);
        this.drawInternalConnectionS0ToE0(x, y);
        this.drawInternalConnectionS0ToE1(x, y);

        this.drawInternalConnectionS1ToInFu0(x, y);
        this.drawInternalConnectionS1ToInFu1(x, y);
        this.drawInternalConnectionS1toInFUMux(x, y);
        this.drawInternalConnectionS1ToN0(x, y);
        this.drawInternalConnectionS1ToN1(x, y);
        this.drawInternalConnectionS1ToE0(x, y);
        this.drawInternalConnectionS1ToE1(x, y);

        this.drawInternalConnectionW0ToInFu0(x, y);
        this.drawInternalConnectionW0ToInFu1(x, y);
        this.drawInternalConnectionW0ToInFuMux(x, y);
        this.drawInternalConnectionW0ToN0(x, y);
        this.drawInternalConnectionW0ToN1(x, y);
        this.drawInternalConnectionW0ToE0(x, y);
        this.drawInternalConnectionW0ToE1(x, y);
        this.drawInternalConnectionW0ToS0(x, y);
        this.drawInternalConnectionW0ToS1(x, y);

        this.drawInternalConnectionW1ToInFu0(x, y);
        this.drawInternalConnectionW1ToInFu1(x, y);
        this.drawInternalConnectionW1ToInFuMux(x, y);
        this.drawInternalConnectionW1ToN0(x, y);
        this.drawInternalConnectionW1ToN1(x, y);
        this.drawInternalConnectionW1ToE0(x, y);
        this.drawInternalConnectionW1ToE1(x, y);
        this.drawInternalConnectionW1ToS1(x, y);
        this.drawInternalConnectionW1ToS1(x, y);
        */

        // internal connection
        // * -> FU0
        PE.DataFlagInFuDriver dataFlagInFu0Driver = this.getConfig().getPe(row, column).getDataFlagInFu0();

        switch (dataFlagInFu0Driver) {
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToInFu0(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToInFu0(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToInFu0(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToInFu0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToInFu0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToInFu0(x, y);
                break;
        }

        // * -> FU1
        PE.DataFlagInFuDriver dataFlagInFu1Driver = this.getConfig().getPe(row, column).getDataFlagInFu1();

        switch (dataFlagInFu1Driver) {
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToInFu1(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToInFu1(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToInFu1(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToInFu1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToInFu1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToInFu1(x, y);
                break;
        }

        // * -> FUMux
        PE.DataFlagInFuDriver flagInFuMuxDriver = this.getConfig().getPe(row, column).getFlagInFuMux();

        switch (flagInFuMuxDriver) {
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToInFuMux(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToInFuMux(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToInFuMux(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToInFuMux(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToInFuMux(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToInFuMux(x, y);
                break;
        }

        // * -> N0
        PE.DataFlagOutDriver dataFlagOutN0Driver = this.getConfig().getPe(row, column).getDataFlagOutN0();

        switch (dataFlagOutN0Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFuToN0(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToN0(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToN0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToN0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToN0(x, y);
                break;
        }

        // * -> N1
        PE.DataFlagOutDriver dataFlagOutN1Driver = this.getConfig().getPe(row, column).getDataFlagOutN1();

        switch (dataFlagOutN1Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFuToN1(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToN1(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToN1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToN1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToN1(x, y);
                break;
        }

        // * -> E0
        PE.DataFlagOutDriver dataFlagOutE0Driver = this.getConfig().getPe(row, column).getDataFlagOutE0();

        switch (dataFlagOutE0Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFuToE0(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToE0(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToE0(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToE0(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToE0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToE0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToE0(x, y);
                break;
        }

        // * -> E1
        PE.DataFlagOutDriver dataFlagOutE1Driver = this.getConfig().getPe(row, column).getDataFlagOutE1();

        switch (dataFlagOutE1Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFuToE1(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToE1(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToE1(x, y);
                break;
            case data_flag_in_S_0:
                this.drawInternalConnectionS0ToE1(x, y);
                break;
            case data_flag_in_S_1:
                this.drawInternalConnectionS1ToE1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToE1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToE1(x, y);
                break;
        }

        // * -> S0
        PE.DataFlagOutDriver dataFlagOutS0Driver = this.getConfig().getPe(row, column).getDataFlagOutS0();

        switch (dataFlagOutS0Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFuToS0(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToS0(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToS0(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToS0(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToS0(x, y);
                break;
        }

        // * -> S1
        PE.DataFlagOutDriver dataFlagOutS1Driver = this.getConfig().getPe(row, column).getDataFlagOutS1();

        switch (dataFlagOutS1Driver) {
            case data_flag_out_FU:
                this.drawInternalConnectionFuToS1(x, y);
                break;
            case data_flag_in_N_0:
                this.drawInternalConnectionN0ToS1(x, y);
                break;
            case data_flag_in_N_1:
                this.drawInternalConnectionN1ToS1(x, y);
                break;
            case data_flag_in_W_0:
                this.drawInternalConnectionW0ToS1(x, y);
                break;
            case data_flag_in_W_1:
                this.drawInternalConnectionW1ToS1(x, y);
                break;
        }


        if(!this.getConfig().getPe(row, column).isActive()) {
            this.drawInternalConnectionInactive(x, y);
        }
    }

    private void drawConnectionN0ToS0(double x, double y) {

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

    private void drawConnectionN1ToS1(double x, double y) {

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

    private void drawConnectionS0ToN0(double x, double y) {

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

    private void drawConnectionS1ToN1(double x, double y) {

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

    private void drawConnectionE0ToW0(double x, double y, boolean crcOutput, boolean crcInput) {

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

    private void drawConnectionE1ToW1(double x, double y, boolean crcOutput, boolean crcInput) {

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

    private void drawInternalConnectionFuToN0(double x, double y) {

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

    private void drawInternalConnectionFuToN1(double x, double y) {

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

    private void drawInternalConnectionFuToE0(double x, double y) {

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

    private void drawInternalConnectionFuToE1(double x, double y) {

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

    private void drawInternalConnectionFuToS0(double x, double y) {

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

    private void drawInternalConnectionFuToS1(double x, double y) {

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

    private void drawInternalConnectionN0ToInFu0(double x, double y) {

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

    private void drawInternalConnectionN0ToInFu1(double x, double y) {

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

    private void drawInternalConnectionN0ToInFuMux(double x, double y) {

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

    private void drawInternalConnectionN0ToE0(double x, double y) {

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

    private void drawInternalConnectionN0ToE1(double x, double y) {

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

    private void drawInternalConnectionN0ToS0(double x, double y) {

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

    private void drawInternalConnectionN0ToS1(double x, double y) {

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

    private void drawInternalConnectionN1ToInFu0(double x, double y) {

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

    private void drawInternalConnectionN1ToInFu1(double x, double y) {

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

   private void drawInternalConnectionN1ToInFuMux(double x, double y) {

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

    private void drawInternalConnectionN1ToE0(double x, double y) {

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

    private void drawInternalConnectionN1ToE1(double x, double y) {

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

    private void drawInternalConnectionN1ToS0(double x, double y) {

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

    private void drawInternalConnectionN1ToS1(double x, double y) {

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

    private void drawInternalConnectionS0ToInFu0(double x, double y) {

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

    private void drawInternalConnectionS0ToInFu1(double x, double y) {

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

    private void drawInternalConnectionS0ToInFuMux(double x, double y) {

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

    private void drawInternalConnectionS0ToE0(double x, double y) {

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

    private void drawInternalConnectionS0ToE1(double x, double y) {

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

    private void drawInternalConnectionS0ToN0(double x, double y) {

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

    private void drawInternalConnectionS0ToN1(double x, double y) {

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

    private void drawInternalConnectionS1ToInFu0(double x, double y) {

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

    private void drawInternalConnectionS1ToInFu1(double x, double y) {

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

    private void drawInternalConnectionS1ToInFuMux(double x, double y) {

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

    private void drawInternalConnectionS1ToE0(double x, double y) {

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

    private void drawInternalConnectionS1ToE1(double x, double y) {

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

    private void drawInternalConnectionS1ToN0(double x, double y) {

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

    private void drawInternalConnectionS1ToN1(double x, double y) {

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

    private void drawInternalConnectionW0ToInFu0(double x, double y) {

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

    private void drawInternalConnectionW0ToInFu1(double x, double y) {

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

    private void drawInternalConnectionW0ToInFuMux(double x, double y) {

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

    private void drawInternalConnectionW0ToN0(double x, double y) {

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

    private void drawInternalConnectionW0ToN1(double x, double y) {

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

    private void drawInternalConnectionW0ToE0(double x, double y) {

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

    private void drawInternalConnectionW0ToE1(double x, double y) {

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

    private void drawInternalConnectionW0ToS0(double x, double y) {

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

    private void drawInternalConnectionW0ToS1(double x, double y) {

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

    private void drawInternalConnectionW1ToInFu0(double x, double y) {

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

    private void drawInternalConnectionW1ToInFu1(double x, double y) {

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

    private void drawInternalConnectionW1ToInFuMux(double x, double y) {

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

    private void drawInternalConnectionW1ToN0(double x, double y) {

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

    private void drawInternalConnectionW1ToN1(double x, double y) {

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

    private void drawInternalConnectionW1ToE0(double x, double y) {

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

    private void drawInternalConnectionW1ToE1(double x, double y) {

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

    private void drawInternalConnectionW1ToS0(double x, double y) {

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

    private void drawInternalConnectionW1ToS1(double x, double y) {

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

    /**
     * displays a context menu for the component which was clicked
     * @param x
     * @param y
     */
    private void handleConfigurationClick(int x, int y) {
        scrollPaneVvalue = scrollPane.getVvalue();
        scrollPaneHvalue = scrollPane.getHvalue();

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

                 FuFunctionContextMenu fuFunctionContextMenu = new FuFunctionContextMenu(model.getCrc().getFu(row, column), this.getConfig().getPe(row, column).getFuFunction());
                 contextMenu = fuFunctionContextMenu;
                 fuFunctionContextMenu.show(this.getContent(), p.x, p.y);

                 fuFunctionContextMenu.setOnHiding(event -> {
                     if(fuFunctionContextMenu.getSelectedFuFunction() != PE.FUFunction.none) {
                        controller.setPeFunction(configurationTabType, number, finalRow, finalColumn, fuFunctionContextMenu.getSelectedFuFunction());
                     }
                 });
            }

            // FU signedness
            if(
                    xNormalized >= 6*peDrawSizeTwentieth &&
                    xNormalized <= 13*peDrawSizeTwentieth &&
                    yNormalized >= 11.5*peDrawSizeTwentieth &&
                    yNormalized <= 13.5*peDrawSizeTwentieth) {

                FuSignednessContextMenu fuSignednessContextMenu = new FuSignednessContextMenu(model.getCrc().getFu(row, column), this.getConfig().getPe(row, column).isSignedData());
                contextMenu = fuSignednessContextMenu;
                fuSignednessContextMenu.show(this.getContent(), p.x, p.y);

                fuSignednessContextMenu.setOnHiding(event -> {
                    controller.setFuSignedness(configurationTabType, number, finalRow, finalColumn, fuSignednessContextMenu.getSelectFuSignedness());
                });
            }

            // in FU0
            if(
                    xNormalized >= 7*peDrawSizeTwentieth-12-4 &&
                    xNormalized <= 7*peDrawSizeTwentieth+4 &&
                    yNormalized >= 6*peDrawSizeTwentieth-4 &&
                    yNormalized <= 6*peDrawSizeTwentieth+12+4) {

                DataFlagInFuDriverContextMenu dataFlagInFuDriverContextMenu = new DataFlagInFuDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagInFu0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagInFuDriverContextMenu;
                dataFlagInFuDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagInFuDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagInFu0()) {
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

                DataFlagInFuDriverContextMenu dataFlagInFuDriverContextMenu = new DataFlagInFuDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagInFu1(), model.getCrc().getRows(), row);
                contextMenu = dataFlagInFuDriverContextMenu;
                dataFlagInFuDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagInFuDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagInFu1()) {
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

                DataFlagInFuDriverContextMenu dataFlagInFuDriverContextMenu = new DataFlagInFuDriverContextMenu(this.getConfig().getPe(row, column).getFlagInFuMux(), model.getCrc().getRows(), row);
                contextMenu = dataFlagInFuDriverContextMenu;
                dataFlagInFuDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagInFuDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagInFuDriverContextMenu.getSelectedDataFlagInFuDriver() == this.getConfig().getPe(finalRow, finalColumn).getFlagInFuMux()) {
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

                DataFlagNorthDriverContextMenu dataFlagNorthDriverContextMenu = new DataFlagNorthDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagOutN0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagNorthDriverContextMenu;
                dataFlagNorthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagNorthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagOutN0()) {
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

                DataFlagNorthDriverContextMenu dataFlagNorthDriverContextMenu = new DataFlagNorthDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagOutN1(), model.getCrc().getRows(), row);
                contextMenu = dataFlagNorthDriverContextMenu;
                dataFlagNorthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagNorthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagNorthDriverContextMenu.getSelectedDataFlagNorthDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagOutN1()) {
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

                DataFlagEastDriverContextMenu dataFlagEastDriverContextMenu = new DataFlagEastDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagOutE0(), model.getCrc().getRows(), row);
                contextMenu = dataFlagEastDriverContextMenu;
                dataFlagEastDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagEastDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagOutE0()) {
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

                DataFlagEastDriverContextMenu dataFlagEastDriverContextMenu = new DataFlagEastDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagOutE1(), model.getCrc().getRows(), row);
                contextMenu = dataFlagEastDriverContextMenu;
                dataFlagEastDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagEastDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagEastDriverContextMenu.getSelectedDataFlagEastDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagOutE1()) {
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

                DataFlagSouthDriverContextMenu dataFlagSouthDriverContextMenu = new DataFlagSouthDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagOutS0(), row);
                contextMenu = dataFlagSouthDriverContextMenu;
                dataFlagSouthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagSouthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagOutS0()) {
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

                DataFlagSouthDriverContextMenu dataFlagSouthDriverContextMenu = new DataFlagSouthDriverContextMenu(this.getConfig().getPe(row, column).getDataFlagOutS1(), row);
                contextMenu = dataFlagSouthDriverContextMenu;
                dataFlagSouthDriverContextMenu.show(this.getContent(), p.x, p.y);

                dataFlagSouthDriverContextMenu.setOnHiding(event -> {
                    if(dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver() == this.getConfig().getPe(finalRow, finalColumn).getDataFlagOutS1()) {
                        controller.setPeDataFlagS1Driver(configurationTabType, number, finalRow, finalColumn, PE.DataFlagOutDriver.none);
                    }
                    else if(dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver() != PE.DataFlagOutDriver.none) {
                        controller.setPeDataFlagS1Driver(configurationTabType, number, finalRow, finalColumn, dataFlagSouthDriverContextMenu.getSelectedDataFlagSouthDriver());
                    }
                });

            }
        }
    }
}

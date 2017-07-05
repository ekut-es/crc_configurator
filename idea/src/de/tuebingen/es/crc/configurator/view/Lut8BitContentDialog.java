package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by luebeck on 7/5/17.
 */
public class Lut8BitContentDialog extends Stage {

    private static final int WINDOW_PADDING = 10;
    private static final int CANVAS_PADDING = 30;

    private String lut8BitContentHexString;

    private boolean applyToAll;
    private boolean modelHasChanged;

    private String formatLut8BitContentHexString(String lut8BitContentHexString) {
        // remove illegal characters
        lut8BitContentHexString = lut8BitContentHexString.toLowerCase();
        lut8BitContentHexString = lut8BitContentHexString.replaceAll("[^0-9a-fA-F]", "");
        lut8BitContentHexString = String.format("%512s", lut8BitContentHexString).replace(' ', '0');
        return lut8BitContentHexString;
    }

    private void plotLutFunction(GraphicsContext gc, String lut8BitContentHexString) {

        gc.clearRect(0, 0, 2*CANVAS_PADDING+256, 2*CANVAS_PADDING+256);

        // draw border
        gc.strokePolygon(
                new double[] {
                        0,
                        256+2*CANVAS_PADDING,
                        256+2*CANVAS_PADDING,
                        0
                },
                new double[] {
                        0,
                        0,
                        256+2*CANVAS_PADDING,
                        256+2*CANVAS_PADDING
                },
                4
        );

        // draw x axis
        gc.strokeLine(CANVAS_PADDING, 256+CANVAS_PADDING, 256+CANVAS_PADDING, 256+CANVAS_PADDING);
        // 0 tick
        gc.strokeLine(CANVAS_PADDING, 256+CANVAS_PADDING, CANVAS_PADDING, 256+CANVAS_PADDING+(CANVAS_PADDING/4));
        // 255 tick
        gc.strokeLine(256+CANVAS_PADDING, 256+CANVAS_PADDING, 256+CANVAS_PADDING, 256+CANVAS_PADDING+(CANVAS_PADDING/4));
        // x axis labels
        gc.fillText("0x00", CANVAS_PADDING-12, 2*CANVAS_PADDING+256-10);
        gc.fillText("0xff", CANVAS_PADDING+256-12, 2*CANVAS_PADDING+256-10);
        gc.fillText("LUT input", (2*CANVAS_PADDING+256)/2-30, 2*CANVAS_PADDING+256-10);

        // draw y axis
        gc.strokeLine(CANVAS_PADDING, CANVAS_PADDING, CANVAS_PADDING, 256+CANVAS_PADDING);
        // 0 tick
        gc.strokeLine(CANVAS_PADDING, 256+CANVAS_PADDING, CANVAS_PADDING-(CANVAS_PADDING/4), 256+CANVAS_PADDING);
        // 255 tick
        gc.strokeLine(CANVAS_PADDING, CANVAS_PADDING, CANVAS_PADDING-(CANVAS_PADDING/4), CANVAS_PADDING);
        gc.save();
        gc.translate(CANVAS_PADDING-12, CANVAS_PADDING+256+8);
        gc.rotate(-90);
        gc.fillText("0x00", 0, 0);
        gc.restore();

        gc.save();
        gc.translate(CANVAS_PADDING-12, CANVAS_PADDING+12);
        gc.rotate(-90);
        gc.fillText("0xff", 0, 0);
        gc.restore();

        gc.save();
        gc.translate(CANVAS_PADDING-12, (2*CANVAS_PADDING+256)/2+30);
        gc.rotate(-90);
        gc.fillText("LUT output", 0, 0);
        gc.restore();

        // draw center lines
        gc.strokeLine(CANVAS_PADDING, CANVAS_PADDING+128, CANVAS_PADDING+256, CANVAS_PADDING+128);
        gc.strokeLine(CANVAS_PADDING+128, CANVAS_PADDING, CANVAS_PADDING+128, CANVAS_PADDING+256);

        gc.save();
        // plot points
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        for(int x = 0; x < 256; x++) {
            String yString = lut8BitContentHexString.substring(512-x*2-2, 512-x*2);
            int y = Integer.parseInt(yString, 16);
            gc.strokeLine(x+CANVAS_PADDING,CANVAS_PADDING+256-y,x+CANVAS_PADDING,CANVAS_PADDING+256-y);
        }
        gc.restore();

    }

    public Lut8BitContentDialog(int row, int column, String lut8BitContentHexString) {

        super();
        this.lut8BitContentHexString = lut8BitContentHexString;
        this.modelHasChanged = false;
        this.applyToAll = false;

        this.setTitle("LUT 8Bit Content PE " + row + "," + column);
        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        Group root = new Group();

        Scene scene = new Scene(root, 2*CANVAS_PADDING+256+2*WINDOW_PADDING, 185+2*CANVAS_PADDING+256);

        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(WINDOW_PADDING,WINDOW_PADDING,WINDOW_PADDING,WINDOW_PADDING));
        vBox.setSpacing(20);

        TextField lut8BitContentTextField = new TextField();
        lut8BitContentTextField.setText(lut8BitContentHexString);
        lut8BitContentTextField.setMaxWidth(2*CANVAS_PADDING+256);

        // check text field content
        lut8BitContentTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            lut8BitContentTextField.setText(formatLut8BitContentHexString(lut8BitContentTextField.getText()));
        });

        Label lut8BitContentNote = new Label("2048 bit as hexadecimal representation (512 digits)");
        lut8BitContentNote.setFont(Font.font(Font.getDefault().getName(), FontWeight.NORMAL, 10));

        Label plotPlaceHolder = new Label("Placeholder for Plot");

        Canvas canvas = new Canvas();
        canvas.setHeight(2*CANVAS_PADDING+256);
        canvas.setWidth(2*CANVAS_PADDING+256);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        this.plotLutFunction(gc, lut8BitContentHexString);

        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            root.requestFocus();
            this.plotLutFunction(gc, lut8BitContentTextField.getText());
        });

        CheckBox applyToAllCheckbox = new CheckBox();
        applyToAllCheckbox.setText("Apply to all PEs");
        applyToAllCheckbox.setSelected(applyToAll);

        Button cancelButton = new Button("Cancel");
        Button applyButton = new Button("Apply");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        // close dialog when "Apply" button was pressed;
        applyButton.setOnAction(event -> {
            this.lut8BitContentHexString = lut8BitContentTextField.getText();
            this.applyToAll = applyToAllCheckbox.isSelected();
            this.modelHasChanged = true;
            this.close();
        });

        HBox hBoxCancelApply = new HBox(2);
        hBoxCancelApply.setSpacing(15);

        hBoxCancelApply.getChildren().addAll(cancelButton,applyButton);

        vBox.getChildren().add(lut8BitContentTextField);
        vBox.getChildren().add(lut8BitContentNote);
        //vBox.getChildren().add(plotPlaceHolder);
        vBox.getChildren().add(canvas);
        vBox.getChildren().add(applyToAllCheckbox);
        vBox.getChildren().add(hBoxCancelApply);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }

    public boolean getApplyToAll() {
        return applyToAll;
    }

    public boolean hasModelChanged() {
        return modelHasChanged;
    }

    public String getLut8BitContentHexString() {
        return lut8BitContentHexString;
    }
}

package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Konstantin (Konze) Lübeck on 27/07/16.
 */
public class NewDialog extends Stage {

    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;
    private boolean inputsNorth;
    private boolean inputsSouth;
    private int dataWidth;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getStaticConfigLines() {
        return staticConfigLines;
    }

    public int getDynamicConfigLines() {
        return dynamicConfigLines;
    }

    public boolean areInputsNorth() {
        return inputsNorth;
    }

    public boolean areInputsSouth() {
        return inputsSouth;
    }

    public int getDataWidth() {
        return dataWidth;
    }

    public boolean create;

    public NewDialog() {
        super();

        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        create = false;

        Group root = new Group();

        Scene scene = new Scene(root, 200, 310);

        VBox vBox = new VBox(4);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label rowsLabel = new Label("Rows");
        NumberTextField rowsTextField = new NumberTextField();
        rowsTextField.setMaxDigits(2);
        rowsTextField.setMinWidth(40);
        rowsTextField.setMaxWidth(40);
        rowsTextField.setText("2");

        Label columnsLabel = new Label("Columns");
        NumberTextField columnsTextField = new NumberTextField();
        columnsTextField.setMaxDigits(2);
        columnsTextField.setMinWidth(40);
        columnsTextField.setMaxWidth(40);
        columnsTextField.setText("2");

        Label staticConfigLinesLabel = new Label("Static Conf. Lines");
        NumberTextField staticConfigLinesTextField = new NumberTextField();
        staticConfigLinesTextField.setMaxDigits(2);
        staticConfigLinesTextField.setMinWidth(40);
        staticConfigLinesTextField.setMaxWidth(40);
        staticConfigLinesTextField.setText("0");

        Label dynamicConfigLinesLabel = new Label("Dynamic Conf. Lines");
        NumberTextField dynamicConfigLinesTextField = new NumberTextField();
        dynamicConfigLinesTextField.setMaxDigits(2);
        dynamicConfigLinesTextField.setMinWidth(40);
        dynamicConfigLinesTextField.setMaxWidth(40);
        dynamicConfigLinesTextField.setText("0");

        Label dataWidthLabel = new Label("Data Width");
        NumberTextField dataWidthTextField = new NumberTextField();
        dataWidthTextField.setMinNumber(8);
        dataWidthTextField.setMaxNumber(64);
        dataWidthTextField.setMinWidth(40);
        dataWidthTextField.setMaxWidth(40);
        dataWidthTextField.setText("32");

        GridPane.setConstraints(rowsLabel, 0, 0);
        GridPane.setConstraints(rowsTextField, 1, 0);

        GridPane.setConstraints(columnsLabel, 0, 1);
        GridPane.setConstraints(columnsTextField, 1, 1);

        GridPane.setConstraints(staticConfigLinesLabel, 0, 2);
        GridPane.setConstraints(staticConfigLinesTextField, 1, 2);

        GridPane.setConstraints(dynamicConfigLinesLabel, 0, 3);
        GridPane.setConstraints(dynamicConfigLinesTextField, 1, 3);

        GridPane.setConstraints(dataWidthLabel, 0, 4);
        GridPane.setConstraints(dataWidthTextField, 1, 4);

        gridPane.getChildren().addAll(
                rowsLabel,
                rowsTextField,
                columnsLabel,
                columnsTextField,
                staticConfigLinesLabel,
                staticConfigLinesTextField,
                dynamicConfigLinesLabel,
                dynamicConfigLinesTextField,
                dataWidthLabel,
                dataWidthTextField
        );

        CheckBox inputsSouthCheckbox = new CheckBox();
        inputsSouthCheckbox.setText("Inputs in the South");

        CheckBox inputsNorthCheckbox = new CheckBox();
        inputsNorthCheckbox.setText("Inputs in the North");

        Button cancelButton = new Button("Cancel");
        Button createButton = new Button("Create");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        // set variable when "Create" button was pressed
        createButton.setOnAction(event -> {
            create = true;
            rows = Integer.parseInt(rowsTextField.getText());
            columns = Integer.parseInt(columnsTextField.getText());
            staticConfigLines = Integer.parseInt(staticConfigLinesTextField.getText());
            dynamicConfigLines = Integer.parseInt(dynamicConfigLinesTextField.getText());
            inputsNorth = inputsNorthCheckbox.isSelected();
            inputsSouth = inputsSouthCheckbox.isSelected();
            dataWidth = Integer.parseInt(dataWidthTextField.getText());
            this.close();
        });

        HBox buttonHBox = new HBox(2);
        buttonHBox.setSpacing(15);
        buttonHBox.getChildren().addAll(cancelButton,createButton);

        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(inputsNorthCheckbox);
        vBox.getChildren().add(inputsSouthCheckbox);
        vBox.getChildren().add(buttonHBox);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }
}

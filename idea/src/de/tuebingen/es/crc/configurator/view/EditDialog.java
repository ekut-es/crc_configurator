package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Konstantin (Konze) Lübeck on 27/07/16.
 */
public class EditDialog extends Stage {

    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;

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

    public boolean apply;

    public EditDialog(int rows, int columns, int staticConfigLines, int dynamicConfigLines) {
        super();

        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        apply = false;

        Group root = new Group();

        Scene scene = new Scene(root, 200, 330);

        VBox vBox = new VBox(2);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label rowsLabel = new Label("Rows");
        NumberTextField rowsTextField = new NumberTextField();
        rowsTextField.setMaxNumbers(2);
        rowsTextField.setMinWidth(40);
        rowsTextField.setMaxWidth(40);
        rowsTextField.setText("" + rows);

        Label columnsLabel = new Label("Columns");
        NumberTextField columnsTextField = new NumberTextField();
        columnsTextField.setMaxNumbers(2);
        columnsTextField.setMinWidth(40);
        columnsTextField.setMaxWidth(40);
        columnsTextField.setText("" + columns);

        Label staticConfigLinesLabel = new Label("Static Conf. Lines");
        NumberTextField staticConfigLinesTextField = new NumberTextField();
        staticConfigLinesTextField.setMaxNumbers(2);
        staticConfigLinesTextField.setMinWidth(40);
        staticConfigLinesTextField.setMaxWidth(40);
        staticConfigLinesTextField.setText("" + staticConfigLines);

        Label dynamicConfigLinesLabel = new Label("Dynamic Conf. Lines");
        NumberTextField dynamicConfigLinesTextField = new NumberTextField();
        dynamicConfigLinesTextField.setMaxNumbers(2);
        dynamicConfigLinesTextField.setMinWidth(40);
        dynamicConfigLinesTextField.setMaxWidth(40);
        dynamicConfigLinesTextField.setText("" + dynamicConfigLines);

        GridPane.setConstraints(rowsLabel, 0, 0);
        GridPane.setConstraints(rowsTextField, 1, 0);

        GridPane.setConstraints(columnsLabel, 0, 1);
        GridPane.setConstraints(columnsTextField, 1, 1);

        GridPane.setConstraints(staticConfigLinesLabel, 0, 2);
        GridPane.setConstraints(staticConfigLinesTextField, 1, 2);

        GridPane.setConstraints(dynamicConfigLinesLabel, 0, 3);
        GridPane.setConstraints(dynamicConfigLinesTextField, 1, 3);

        Label warningLabel = new Label("Caution:\nIf you decrease a value \nFU configurations, \nstatic configurations, \nand dynamic configurations \nwill be lost!");
        warningLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 12));
        warningLabel.setTextFill(Color.web("#ff0000"));

        warningLabel.setWrapText(true);

        gridPane.getChildren().addAll(
                rowsLabel,
                rowsTextField,
                columnsLabel,
                columnsTextField,
                staticConfigLinesLabel,
                staticConfigLinesTextField,
                dynamicConfigLinesLabel,
                dynamicConfigLinesTextField
        );

        Button applyButton = new Button("Cancel");
        Button createButton = new Button("Apply");

        // close dialog when "Cancel" button was pressed
        applyButton.setOnAction(event -> this.close());

        // set variable when "Create" button was pressed
        createButton.setOnAction(event -> {
            apply = true;
            this.rows = Integer.parseInt(rowsTextField.getText());
            this.columns = Integer.parseInt(columnsTextField.getText());
            this.staticConfigLines = Integer.parseInt(staticConfigLinesTextField.getText());
            this.dynamicConfigLines = Integer.parseInt(dynamicConfigLinesTextField.getText());

            this.close();
        });

        HBox buttonHBox = new HBox(2);
        buttonHBox.setSpacing(15);
        buttonHBox.getChildren().addAll(applyButton,createButton);

        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(warningLabel);
        vBox.getChildren().add(buttonHBox);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }
}

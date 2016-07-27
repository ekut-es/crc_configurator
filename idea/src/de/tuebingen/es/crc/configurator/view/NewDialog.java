package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    public boolean create;

    public NewDialog() {
        super();

        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        create = false;

        Group root = new Group();

        Scene scene = new Scene(root, 200, 205);

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

        Label columnsLabel = new Label("Columns");
        NumberTextField columnsTextField = new NumberTextField();
        columnsTextField.setMaxNumbers(2);
        columnsTextField.setMinWidth(40);
        columnsTextField.setMaxWidth(40);

        Label staticConfigLinesLabel = new Label("Static Conf. Lines");
        NumberTextField staticConfigLinesTextField = new NumberTextField();
        staticConfigLinesTextField.setMaxNumbers(2);
        staticConfigLinesTextField.setMinWidth(40);
        staticConfigLinesTextField.setMaxWidth(40);

        Label dynamicConfigLinesLabel = new Label("Dynamic Conf. Lines");
        NumberTextField dynamicConfigLinesTextField = new NumberTextField();
        dynamicConfigLinesTextField.setMaxNumbers(2);
        dynamicConfigLinesTextField.setMinWidth(40);
        dynamicConfigLinesTextField.setMaxWidth(40);

        GridPane.setConstraints(rowsLabel, 0, 0);
        GridPane.setConstraints(rowsTextField, 1, 0);

        GridPane.setConstraints(columnsLabel, 0, 1);
        GridPane.setConstraints(columnsTextField, 1, 1);

        GridPane.setConstraints(staticConfigLinesLabel, 0, 2);
        GridPane.setConstraints(staticConfigLinesTextField, 1, 2);

        GridPane.setConstraints(dynamicConfigLinesLabel, 0, 3);
        GridPane.setConstraints(dynamicConfigLinesTextField, 1, 3);

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

        Button cancelButton = new Button("Cancel");
        Button createButton = new Button("Create");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        HBox buttonHBox = new HBox(2);
        buttonHBox.setSpacing(15);
        buttonHBox.getChildren().addAll(cancelButton,createButton);

        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(buttonHBox);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }
}

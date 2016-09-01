package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class FuFunctionsDialog extends Stage {

    public boolean modelHasChanged;
    private final LinkedHashMap<String, Boolean> fuFunctions;
    private final LinkedHashMap<String, CheckBox> fuFunctionCheckboxes;

    public FuFunctionsDialog(int row, int column, LinkedHashMap<String, Boolean> fuFunctions) {

        super();
        this.fuFunctions = new LinkedHashMap<>();
        this.fuFunctions.putAll(fuFunctions);

        fuFunctionCheckboxes = new LinkedHashMap<>();
        modelHasChanged = false;

        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        Group root = new Group();

        Scene scene = new Scene(root, 150, 460);

        VBox vBox = new VBox(fuFunctions.size()+2);
        vBox.setPadding(new Insets(10,10,10,10));

        vBox.getChildren().add(new Text("FU Functions PE " + row + "," + column));

        for(Map.Entry<String, Boolean> function : fuFunctions.entrySet())  {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(function.getKey());
            checkBox.setSelected(function.getValue());
            fuFunctionCheckboxes.put(function.getKey(), checkBox);
            vBox.getChildren().add(checkBox);
        }

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        // save changes to model an close dialog when "Save" button was pressed
        saveButton.setOnAction(event -> {
            for(Map.Entry<String, CheckBox> function : fuFunctionCheckboxes.entrySet()) {
                this.fuFunctions.replace(function.getKey(), function.getValue().isSelected());
            }
            modelHasChanged = true;
            this.close();
        });

        HBox buttonHBox = new HBox(2);
        buttonHBox.setSpacing(15);

        buttonHBox.getChildren().addAll(cancelButton,saveButton);

        vBox.getChildren().add(buttonHBox);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }

    public LinkedHashMap<String, Boolean> getFuFunctions() {
        return this.fuFunctions;
    }
}

package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.FU;
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
public class FuModesDialog extends Stage {

    public boolean modelHasChanged;
    private boolean applyToAll;
    private final LinkedHashMap<FU.FuMode, Boolean> availableFuModes;
    private final LinkedHashMap<FU.FuMode, CheckBox> fuModeCheckboxes;

    public FuModesDialog(int row, int column, LinkedHashMap<FU.FuMode, Boolean> availableFuModes) {

        super();
        this.availableFuModes = new LinkedHashMap<>();
        this.availableFuModes.putAll(availableFuModes);

        fuModeCheckboxes = new LinkedHashMap<>();
        modelHasChanged = false;
        applyToAll = false;

        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        Group root = new Group();

        Scene scene = new Scene(root, 150, 590);

        VBox vBox = new VBox(availableFuModes.size()+2);
        vBox.setPadding(new Insets(10,10,10,10));

        vBox.getChildren().add(new Text("FU Modes PE " + row + "," + column));

        for(Map.Entry<FU.FuMode, Boolean> fuMode : availableFuModes.entrySet())  {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(FU.fuModeToName.get(fuMode.getKey()));
            checkBox.setSelected(fuMode.getValue());
            fuModeCheckboxes.put(fuMode.getKey(), checkBox);
            vBox.getChildren().add(checkBox);
        }

        CheckBox checkBoxApplyToAll = new CheckBox();
        checkBoxApplyToAll.setText("Apply to all PEs");
        checkBoxApplyToAll.setSelected(applyToAll);
        vBox.getChildren().add(checkBoxApplyToAll);

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        // save changes to model an close dialog when "Save" button was pressed
        saveButton.setOnAction(event -> {
            for(Map.Entry<FU.FuMode, CheckBox> function : fuModeCheckboxes.entrySet()) {
                this.availableFuModes.replace(function.getKey(), function.getValue().isSelected());
            }

            applyToAll = checkBoxApplyToAll.isSelected();

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

    public LinkedHashMap<FU.FuMode, Boolean> getAvailableFuModes() {
        return this.availableFuModes;
    }

    public Boolean getApplyToAll() {
        return this.applyToAll;
    }
}

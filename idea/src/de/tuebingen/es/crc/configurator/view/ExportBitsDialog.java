package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.Configuration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Created by Konstantin (Konze) Lübeck on 29/08/16.
 */
public class ExportBitsDialog extends Stage {

    public ExportBitsDialog(CRC crc) {
        super();

        ExportBitsText exportBitsText = new ExportBitsText();
        Preferences userPreferences = Preferences.userNodeForPackage(this.getClass());

        this.setTitle("Export Bits");
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10,10,10,10));

        int width = userPreferences.getInt("width", 300);
        int height = userPreferences.getInt("height", 200);

        Scene scene = new Scene(border, width, height);

        this.setMinWidth(500);
        this.setMinHeight(200);

        Boolean cLikeHexRepresentation = userPreferences.getBoolean("cLikeHexRepresentation", false);

        CheckBox cLikeHexRepresentationCheckBox = new CheckBox("C-like hexadecimal representation");
        cLikeHexRepresentationCheckBox.setSelected(cLikeHexRepresentation);

        ComboBox configurationComboBox = new ComboBox();

        configurationComboBox.getItems().add("All");
        configurationComboBox.getItems().add("Static Configurations");

        // add dynamic configurations to combo box
        for(int i = 0; i < crc.getDynamicConfigLines(); i++) {
            configurationComboBox.getItems().add("Dynamic Configuration " + i);
        }

        String configurationComboBoxValue = userPreferences.get("configurationComboBoxValue", "All");

        if(configurationComboBox.getItems().contains(configurationComboBoxValue)) {
            configurationComboBox.setValue(configurationComboBoxValue);
        } else {
            configurationComboBox.setValue("All");
        }

        HBox topHBox = new HBox(2);
        topHBox.setPadding(new Insets(0,0,0,0));
        topHBox.setSpacing(20);

        topHBox.getChildren().add(cLikeHexRepresentationCheckBox);
        topHBox.getChildren().add(configurationComboBox);

        TextArea textArea = new TextArea();
        textArea.setFont(Font.font("Courier", 14));
        textArea.wrapTextProperty().set(true);
        textArea.setMinWidth(100);
        textArea.setEditable(false);

        textArea.setText(exportBitsText.getText(crc, cLikeHexRepresentation, getConfigurationTypeFromComboBox(configurationComboBox), getConfigurationTypeNumberFromComboBox(configurationComboBox)));

        Button closeButton = new Button("Close");

        cLikeHexRepresentationCheckBox.setOnAction(event -> {
                textArea.setText(exportBitsText.getText(crc, cLikeHexRepresentationCheckBox.isSelected(), getConfigurationTypeFromComboBox(configurationComboBox), getConfigurationTypeNumberFromComboBox(configurationComboBox)));
        });

        configurationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            textArea.setText(exportBitsText.getText(crc, cLikeHexRepresentationCheckBox.isSelected(), getConfigurationTypeFromComboBox(configurationComboBox), getConfigurationTypeNumberFromComboBox(configurationComboBox)));
        });

        closeButton.setOnAction(event -> {
            userPreferences.putInt("width", (int) this.getWidth());
            userPreferences.putInt("height", (int) this.getHeight());
            userPreferences.putBoolean("cLikeHexRepresentation", cLikeHexRepresentationCheckBox.isSelected());
            userPreferences.put("configurationComboBoxValue", configurationComboBox.getValue().toString());
            this.close();
        });

        border.setTop(topHBox);
        border.setCenter(textArea);
        BorderPane.setMargin(textArea, new Insets(10,0,10,0));
        border.setBottom(closeButton);

        this.setScene(scene);
        this.sizeToScene();
    }

    private Configuration.ConfigurationType getConfigurationTypeFromComboBox(ComboBox comboBox) {
        String comboBoxValue = comboBox.getValue().toString();
        if(comboBoxValue.contains("Static")) {
            return Configuration.ConfigurationType.STATIC;
        } else if(comboBoxValue.contains("Dynamic")) {
            return Configuration.ConfigurationType.DYNAMIC;
        }
        return Configuration.ConfigurationType.NONE;
    }

    private int getConfigurationTypeNumberFromComboBox(ComboBox comboBox) {
        String comboBoxValue = comboBox.getValue().toString();

        if(comboBoxValue.equals("All") || comboBoxValue.equals("Static Configurations")) {
            return 0;
        }

        return Integer.parseInt(comboBoxValue.substring(comboBoxValue.lastIndexOf(" ")+1));
    }
}

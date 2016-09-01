package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.CRC;
import de.tuebingen.es.crc.configurator.model.Configuration;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 29/08/16.
 */
public class ExportBitsDialog extends Stage {

    public ExportBitsDialog(CRC crc) {
        super();

        String text = "";

        text += "Bits for the CRC Core Verilog Module (crc_core.v)\n\n";
        text += "PE_OP_PARAMETERS:\n";
        text += crc.getPeOpParametersBits() + "\n\n";
        text += "PE_STATIC_CONFIG_PARAMETERS:\n";
        text += crc.getStaticConfigParameterBits() + "\n\n\n";

        text += "Bits for Single PEs (pe.v)";

        for(int i = 0; i < crc.getRows(); i++) {
            for(int j = 0; j < crc.getColumns(); j++) {
                text += "PE " + i + "," + j + ":\n";
                text += "static_config_content:\n";
                text += crc.getPeStaticConfigParameterBits(i,j) + "\n\n";

                HashMap<Integer, Configuration> dynamicConfigs = crc.getDynamicConfigs();

                for(Map.Entry<Integer, Configuration> entry : dynamicConfigs.entrySet()) {
                    text += "dynamic_config_content_" + entry.getKey() + ":\n";
                    text += crc.getPeDynamicConfigParameterBits(i, j, entry.getKey()) + "\n\n";
                }
            }
        }

        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10,10,10,10));

        Scene scene = new Scene(border);
        this.setMinWidth(300);
        this.setMinHeight(200);

        TextArea textArea = new TextArea();
        textArea.setFont(Font.font("Courier",14));
        textArea.setText(text);
        textArea.wrapTextProperty().set(true);
        textArea.setMinWidth(100);
        textArea.setEditable(false);

        Button closeButton = new Button("Close");

        closeButton.setOnAction(event -> this.close());

        border.setCenter(textArea);
        BorderPane.setMargin(textArea, new Insets(0,0,10,0));
        border.setBottom(closeButton);

        this.setScene(scene);
        this.sizeToScene();
    }
}

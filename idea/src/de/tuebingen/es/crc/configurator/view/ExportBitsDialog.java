package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Konstantin (Konze) Lübeck on 29/08/16.
 */
public class ExportBitsDialog extends Stage {

    public ExportBitsDialog(String text) {
        super();

        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10,10,10,10));

        Scene scene = new Scene(border);
        this.setMinWidth(250);
        this.setMinHeight(200);

        TextArea textArea = new TextArea();
        textArea.setFont(Font.font("Courier",14));
        textArea.setText(text);
        textArea.setMinWidth(100);
        textArea.setEditable(false);

        Button closeButton = new Button("Close");

        closeButton.setOnAction(event -> this.close());

        border.setCenter(textArea);
        border.setMargin(textArea, new Insets(0,0,10,0));
        border.setBottom(closeButton);

        this.setScene(scene);
        this.sizeToScene();
    }
}

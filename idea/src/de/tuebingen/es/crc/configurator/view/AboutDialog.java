package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public class AboutDialog extends Stage {

    public AboutDialog() {

        super();

        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("About CRC Configurator");

        Group root = new Group();

        Scene scene = new Scene(root, 250, 160);

        VBox vBox = new VBox(2);
        vBox.setPadding(new Insets(10,10,10,10));

        Text aboutText = new Text(
                "Konstantin Lübeck\n" +
                        "Eberhard Karls Universität Tübingen\n" +
                        "Wilhelm-Schickard-Institut\n" +
                        "Chair for Embedded Systems\n\n" +
                        "Version 0.1 (2016)\n"
        );

        Button closeButton = new Button("Close");

        closeButton.setOnAction(event -> this.close());

        vBox.getChildren().addAll(aboutText, closeButton);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }
}

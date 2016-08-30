package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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

        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10,10,10,10));

        Scene scene = new Scene(border);

        ImageView icon = new ImageView();
        icon.setImage(new Image("icon/icon_100x100.png"));
        icon.setSmooth(true);

        VBox vBox = new VBox(4);

        Text aboutText = new Text(
                "Konstantin Lübeck\n" +
                        "Eberhard Karls Universität Tübingen\n" +
                        "Wilhelm-Schickard-Institut\n" +
                        "Chair for Embedded Systems\n\n" +
                        "Version 0.1 (2016)\n"
        );

        Text iconCredit = new Text("The Icon was adapted from FroyoShark");

        Hyperlink creditLink = new Hyperlink();
        creditLink.setText("http://froyoshark.deviantart.com");
        creditLink.setFont(Font.font(10));

        Text creativeCommons = new Text("CC-BY 4.0");
        creativeCommons.setFont(Font.font(10));

        iconCredit.setFont(Font.font(10));

        Button closeButton = new Button("Close");

        closeButton.setOnAction(event -> this.close());

        vBox.getChildren().addAll(aboutText, iconCredit, creditLink, creativeCommons);

        border.setLeft(icon);
        border.setCenter(vBox);
        border.setBottom(closeButton);

        border.setMargin(icon, new Insets(0,10,0,0));
        border.setMargin(closeButton, new Insets(10,0,0,0));
        border.setAlignment(closeButton, Pos.CENTER);

        this.setScene(scene);
        this.sizeToScene();
    }
}

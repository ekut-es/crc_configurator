package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.ConfiguratorTab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class Main extends Application {

    private static String filePath;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/mainWindow.fxml"));
        Parent root = fxmlLoader.load();

        Model model = new Model();

        Controller controller = fxmlLoader.getController();
        controller.initModel(model);
        controller.setStage(primaryStage);

        int stageWidth = 2*ConfiguratorTab.CANVAS_PADDING+2*ConfiguratorTab.PE_DRAW_SIZE+3*ConfiguratorTab.INTER_PE_DISTANCE+10;
        int stageHeight = 2*ConfiguratorTab.CANVAS_PADDING+2*ConfiguratorTab.PE_DRAW_SIZE+2*ConfiguratorTab.INTER_PE_DISTANCE+110;

        primaryStage.setTitle("CRC Configurator");
        primaryStage.getIcons().add(new Image("icon/icon_512x512.png"));

        if(System.getProperty("os.name").equals("Mac OS X")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CRC Configurator");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            com.apple.eawt.Application.getApplication().setDockIconImage(new ImageIcon("icon/icon_512x512.png").getImage());
        }

        primaryStage.setMinWidth(stageWidth);
        primaryStage.setMinHeight(stageHeight);
        primaryStage.setScene(new Scene(root, stageWidth, stageHeight));

        primaryStage.setOnCloseRequest(event -> controller.quitApplication());

        primaryStage.show();

        // open file from file path given from the command line
        if(filePath != null) {
            File crcDescriptionFile = new File(filePath);
            controller.openCrcDescriptionFile(crcDescriptionFile);
        }
    }


    public static void main(String[] args) {

        // check if a command line argument was given
        if(args.length > 0) {
            filePath = args[0];
        }

        launch(args);
    }
}

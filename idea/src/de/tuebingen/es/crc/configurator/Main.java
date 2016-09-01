package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.ConfiguratorTab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private static String filePath;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // TODO command line version: -e, --export-bits which prints the bits on the command line

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

        // check first command line argument
        if(args.length == 1) {
            filePath = args[0];
        }

        // check second command line argument
        if(args.length == 2) {
            filePath = args[0];
            String secondArg = args[1];
        }

        launch(args);
    }
}

package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.ConfiguratorTab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        int stageWidth = 2*ConfiguratorTab.CANVAS_PADDING+2*ConfiguratorTab.PE_DRAW_SIZE+ConfiguratorTab.INTER_PE_DISTANCE+10;
        int stageHeight = 2*ConfiguratorTab.CANVAS_PADDING+2*ConfiguratorTab.PE_DRAW_SIZE+ConfiguratorTab.INTER_PE_DISTANCE+110;

        primaryStage.setTitle("CRC Configurator");
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

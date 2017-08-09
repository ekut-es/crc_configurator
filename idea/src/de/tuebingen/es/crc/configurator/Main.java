package de.tuebingen.es.crc.configurator;

import de.tuebingen.es.crc.configurator.model.Model;
import de.tuebingen.es.crc.configurator.view.ConfiguratorTab;
import de.tuebingen.es.crc.configurator.view.ExportBitsText;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

public class Main extends Application {

    private static String[] commandLineArgs;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // command line args:
        // 1: file path
        String filePath = null;

        // 2,3: export statements
        Boolean exportBits = false;
        Boolean exportPNGs = false;

        // check command line args
        if(commandLineArgs.length > 0) {
            if(commandLineArgs[0].equals("-h") || commandLineArgs[0].equals("--help")) {
                syntaxMessage();
                System.exit(0);
            }

            File f = new File(commandLineArgs[0]);

            if (f.exists() && !f.isDirectory()) {
                filePath = commandLineArgs[0];
            } else {
                System.out.println("File '" + commandLineArgs[0] + "' does not exist.");
                System.out.println("Use -h | --help to show the command line help.");
                System.exit(1);
            }

            for(int i = 1; i < commandLineArgs.length; i++) {
                if(commandLineArgs[i].equals("-eb") || commandLineArgs[i].equals("--export-bits")) {
                    exportBits = true;
                }
                else if(commandLineArgs[i].equals("-epngs") || commandLineArgs[i].equals("--export-pngs")) {
                    exportPNGs = true;
                } else {
                    System.out.println("Unknown command line argument '" + commandLineArgs[i] + "'.");
                    System.out.println("Use -h | --help to show the command line help.");
                    System.exit(1);
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/mainWindow.fxml"));
        Parent root = fxmlLoader.load();

        Model model = new Model();
        Preferences userPreferences = Preferences.userNodeForPackage(this.getClass());

        Controller controller = fxmlLoader.getController();
        controller.initModelViewController(model);
        controller.setStage(primaryStage);

        int stageWidth = userPreferences.getInt("stageWidth", 2*ConfiguratorTab.CANVAS_PADDING+2*ConfiguratorTab.PE_DRAW_SIZE+3*ConfiguratorTab.INTER_PE_DISTANCE+10);
        int stageHeight = userPreferences.getInt("stageHeight", 2*ConfiguratorTab.CANVAS_PADDING+2*ConfiguratorTab.PE_DRAW_SIZE+2*ConfiguratorTab.INTER_PE_DISTANCE+110);

        primaryStage.setTitle("CRC Configurator");
        primaryStage.getIcons().add(new Image("icon/icon_512x512.png"));

        primaryStage.setMinWidth(stageWidth);
        primaryStage.setMinHeight(stageHeight);
        primaryStage.setScene(new Scene(root, stageWidth, stageHeight));

        primaryStage.setOnCloseRequest(event -> {
            // save stage width and height
            userPreferences.putInt("stageWidth", (int) primaryStage.getWidth());
            userPreferences.putInt("stageHeight", (int) primaryStage.getHeight());
            // quit application
            controller.quitApplication();
        });

        if(!exportBits && !exportPNGs) {
            primaryStage.show();
        }

        // open file from file path given from the command line
        if(filePath != null) {
            File crcDescriptionFile = new File(filePath);
            controller.openCrcDescriptionFile(crcDescriptionFile);

            if(exportBits) {
                ExportBitsText exportBitsText = new ExportBitsText();
                System.out.println(exportBitsText.getText(model.getCrc(), false));
            }

            if(exportPNGs) {
                List<Tab> tabs = controller.getTabPane().getTabs();

                for(Tab tab : tabs) {
                    ConfiguratorTab configuratorTab = (ConfiguratorTab) tab;
                    controller.getTabPane().getSelectionModel().select(configuratorTab);
                    configuratorTab.update();

                    String pngFileName = new String(crcDescriptionFile.getName().replace(".json", "") + "_" + configuratorTab.getText().toLowerCase().replace(' ', '_') + ".png");

                    File pngFile = new File(pngFileName);

                    Canvas canvas = configuratorTab.getCanvas();
                    try {
                        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                        canvas.snapshot(null, writableImage);
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(renderedImage, "png", pngFile);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            if(exportBits || exportPNGs) {
                System.exit(0);
            }
        }
    }


    public static void main(String[] args) {

        commandLineArgs = args;
        launch(args);
    }

    public static void syntaxMessage() {
        System.out.println("DESCRIPTION:");
        System.out.println("    CRC Configurator\n");
        System.out.println("SYNTAX:");
        System.out.println("    java -jar crc_configurator.jar [FILE [EXPORTS...]]\n");
        System.out.println("FILE:");
        System.out.println("    Path to CRC description file which should be opened.\n");
        System.out.println("EXPORTS:");
        System.out.println("    -eb    | --export-bits : Prints configuration bits.");
        System.out.println("    -epngs | --export-pngs : Exports the hardware model and all configurations as PNGs.");
    }
}

package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Created by Konstantin (Konze) Lübeck on 27/02/2017.
 */
public class ExportVerilogDialog extends Stage {

    private File verilogFile;
    private boolean fifosBetweenPes;
    private boolean export;

    public ExportVerilogDialog(String crcDescriptionFilePath) {
        super();

        export = false;

        this.setTitle("Export Verilog Code");
        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        Group root = new Group();

        Scene scene = new Scene(root, 400, 150);

        VBox vBox = new VBox(3);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        // checkbox for FIFOs between PEs
        CheckBox fifoBetweenPesCheckbox = new CheckBox();
        fifoBetweenPesCheckbox.setText("FIFOs between PEs");

        // Label, TextField and Choose Button for Verilog File Path
        Label pathLabel = new Label("Path to Verilog File:");

        TextField pathTextField = new TextField();
        pathTextField.setMinWidth(312);

        verilogFile = new File(crcDescriptionFilePath.substring(0, crcDescriptionFilePath.lastIndexOf('.')) + ".v");

        pathTextField.setText(verilogFile.getAbsolutePath());

        Button choosePathButton = new Button("Choose");

        // open file chooser dialog when "Choose" button was pressed
        choosePathButton.setOnAction( event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Verilog (*.v)", "*.v"));
            fileChooser.setInitialFileName("*.v");
            fileChooser.setTitle("Choose Verilog File");

            verilogFile = fileChooser.showOpenDialog(scene.getWindow());

            pathTextField.setText(verilogFile.getAbsolutePath());
        });

        HBox pathChooserHBox = new HBox(2);
        pathChooserHBox.getChildren().addAll(pathTextField,choosePathButton);

        VBox pathChooserVBox = new VBox(2);
        pathChooserVBox.getChildren().addAll(pathLabel,pathChooserHBox);

        // Cancel and Export Buttons
        Button cancelButton = new Button("Cancel");
        Button exportButton = new Button("Export");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction( event -> this.close() );

        // generate verilog file when "Export" was pressed
        exportButton.setOnAction( event -> {
            verilogFile = new File(pathTextField.getText());
            fifosBetweenPes = fifoBetweenPesCheckbox.isSelected();
            export = true;
            this.close();
        });

        HBox buttonHBox = new HBox(2);
        buttonHBox.setSpacing(15);
        buttonHBox.getChildren().addAll(cancelButton, exportButton);

        // adding all together
        vBox.getChildren().add(fifoBetweenPesCheckbox);
        vBox.getChildren().add(pathChooserVBox);
        vBox.getChildren().add(buttonHBox);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }

    public File getVerilogFile() {
        return verilogFile;
    }

    public boolean areFifosBetweenPes() {
        return fifosBetweenPes;
    }

    public boolean wasExportPressed() {
        return export;
    }
}

package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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
    private int interPeFifoLength;
    private int inputFifoLength;
    private int outputFifoLength;
    private boolean generateTestbenchAndQuestaSimScript;
    private boolean export;

    public ExportVerilogDialog(String crcDescriptionFilePath) {
        super();

        export = false;

        this.setTitle("Export Verilog Code");
        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        Group root = new Group();

        Scene scene = new Scene(root, 400, 450);

        VBox vBox = new VBox(7);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        // checkbox for FIFOs between PEs
        CheckBox fifoBetweenPesCheckbox = new CheckBox();
        fifoBetweenPesCheckbox.setText("FIFOs between PEs");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Label and TextField for inter pe fifo length
        Label interPeFifoLengthLabel = new Label("Length of FIFOs between PEs:");

        NumberTextField interPeFifoLengthTextField = new NumberTextField();
        interPeFifoLengthTextField.setMaxDigits(2);
        interPeFifoLengthTextField.setMinNumber(0);
        interPeFifoLengthTextField.setMaxNumber(0);
        interPeFifoLengthTextField.setMinWidth(40);
        interPeFifoLengthTextField.setMaxWidth(40);
        interPeFifoLengthTextField.setEditable(false);

        fifoBetweenPesCheckbox.setOnAction( event -> {
            if(fifoBetweenPesCheckbox.isSelected()) {
                interPeFifoLengthTextField.setEditable(true);
                interPeFifoLengthTextField.setMinNumber(2);
                interPeFifoLengthTextField.setMaxNumber(100);
            } else {
                interPeFifoLengthTextField.setEditable(false);
                interPeFifoLengthTextField.setMinNumber(0);
                interPeFifoLengthTextField.setMaxNumber(0);
            }
        });

        // Label and TextField for input fifo length
        Label inputFifoLengthLabel = new Label("Length of input FIFOs:");

        NumberTextField inputFifoLengthTextField = new NumberTextField();
        inputFifoLengthTextField.setMaxDigits(2);
        // TODO minNumber = 0 => no FIFOs will be generated
        inputFifoLengthTextField.setMinNumber(2);
        inputFifoLengthTextField.setMinWidth(40);
        inputFifoLengthTextField.setMaxWidth(40);


        // Label and TextField for output fifo length
        Label outputFifoLengthLabel = new Label("Length of output FIFOs:");

        NumberTextField outputFifoLengthTextField = new NumberTextField();
        outputFifoLengthTextField.setMaxDigits(2);
        // TODO minNumber = 0 => no FIFOs will be generated
        outputFifoLengthTextField.setMinNumber(2);
        outputFifoLengthTextField.setMinWidth(40);
        outputFifoLengthTextField.setMaxWidth(40);


        // add fifo length inputs to grid pane
        GridPane.setConstraints(interPeFifoLengthLabel, 0, 0);
        GridPane.setConstraints(interPeFifoLengthTextField, 1, 0);

        GridPane.setConstraints(inputFifoLengthLabel, 0, 1);
        GridPane.setConstraints(inputFifoLengthTextField, 1, 1);

        GridPane.setConstraints(outputFifoLengthLabel, 0, 2);
        GridPane.setConstraints(outputFifoLengthTextField, 1, 2);

        gridPane.getChildren().addAll(
                interPeFifoLengthLabel,
                interPeFifoLengthTextField,
                inputFifoLengthLabel,
                inputFifoLengthTextField,
                outputFifoLengthLabel,
                outputFifoLengthTextField
        );

        // Label, TextField and Choose Button for Verilog File Path
        Label pathToVerilogFileLabel = new Label("Path to Verilog File:");

        TextField pathToVerilogFileTextField = new TextField();
        pathToVerilogFileTextField.setMinWidth(312);


        // crc description file does not exist yet
        if(crcDescriptionFilePath != "") {
            verilogFile = new File(crcDescriptionFilePath.substring(0, crcDescriptionFilePath.lastIndexOf('.')) + ".v");
        }

        // crc description file exists
        else {
            verilogFile = new File("crc.v");
        }

        pathToVerilogFileTextField.setText(verilogFile.getAbsolutePath());

        Button choosePathToVerilogFileButton = new Button("Choose");

        // open file chooser dialog when "Choose" button was pressed
        choosePathToVerilogFileButton.setOnAction( event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Verilog (*.v)", "*.v"));
            fileChooser.setInitialFileName("*.v");
            fileChooser.setTitle("Choose Verilog File");

            verilogFile = fileChooser.showOpenDialog(scene.getWindow());

            pathToVerilogFileTextField.setText(verilogFile.getAbsolutePath());
        });

        HBox pathChooserHBox = new HBox(2);
        pathChooserHBox.getChildren().addAll(pathToVerilogFileTextField,choosePathToVerilogFileButton);

        VBox pathToVerilogFileChooserVBox = new VBox(2);
        pathToVerilogFileChooserVBox.getChildren().addAll(pathToVerilogFileLabel,pathChooserHBox);

        // check box for generation of testbench and QuestaSim Script
        CheckBox generateTestBenchAndQuestaSimScriptCheckBox = new CheckBox("Generate test bench and QuestaSim script");

        // label for text field for path to testbench file
        Label pathToTestBenchFileLabel = new Label("Path to test bench file:");

        // read only text field for path to testbench file
        TextField pathToTestBenchFileTextField = new TextField();
        pathToTestBenchFileTextField.setEditable(false);
        pathToTestBenchFileTextField.setText("...");

        VBox pathToTestBenchFileVBox = new VBox();
        pathToTestBenchFileVBox.getChildren().addAll(pathToTestBenchFileLabel, pathToTestBenchFileTextField);

        // label for text field for path to QuestaSim Script
        Label pathToQuestaSimScriptLabel = new Label("Path to QuestaSim script:");

        // read only text field for path to QuestaSim Script
        TextField pathToQuestaSimScriptTextField = new TextField();
        pathToQuestaSimScriptTextField.setEditable(false);
        pathToQuestaSimScriptTextField.setText("...");

        VBox pathToQuestaSimScriptVBox = new VBox();
        pathToQuestaSimScriptVBox.getChildren().addAll(pathToQuestaSimScriptLabel, pathToQuestaSimScriptTextField);

        // insert/remove path to test bench file and QuestaSim script when checkbox is toggled
        generateTestBenchAndQuestaSimScriptCheckBox.setOnAction( event -> {
            if(generateTestBenchAndQuestaSimScriptCheckBox.isSelected()) {
                pathToTestBenchFileTextField.setText(this.getTestBenchFilePath());
                pathToQuestaSimScriptTextField.setText(this.getQuestaSimScriptPath());
            } else {
                pathToTestBenchFileTextField.setText("...");
                pathToQuestaSimScriptTextField.setText("...");
            }
        });

        // update test bench file and QuestaSim script text field when path to verilog file changes
        pathToVerilogFileTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(generateTestBenchAndQuestaSimScriptCheckBox.isSelected()) {
                verilogFile = new File(pathToVerilogFileTextField.getText());
                pathToTestBenchFileTextField.setText(this.getTestBenchFilePath());
                pathToQuestaSimScriptTextField.setText(this.getQuestaSimScriptPath());
            }
        });

        // Cancel and Export Buttons
        Button cancelButton = new Button("Cancel");
        Button exportButton = new Button("Export");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction( event -> this.close() );

        // generate verilog file when "Export" was pressed
        exportButton.setOnAction( event -> {
            fifosBetweenPes = fifoBetweenPesCheckbox.isSelected();
            interPeFifoLength = interPeFifoLengthTextField.getNumber();
            inputFifoLength = inputFifoLengthTextField.getNumber();
            outputFifoLength = outputFifoLengthTextField.getNumber();
            generateTestbenchAndQuestaSimScript = generateTestBenchAndQuestaSimScriptCheckBox.isSelected();
            export = true;
            this.close();
        });

        HBox buttonHBox = new HBox(2);
        buttonHBox.setSpacing(15);
        buttonHBox.getChildren().addAll(cancelButton, exportButton);

        // adding all together
        vBox.getChildren().add(fifoBetweenPesCheckbox);
        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(pathToVerilogFileChooserVBox);
        vBox.getChildren().add(generateTestBenchAndQuestaSimScriptCheckBox);
        vBox.getChildren().add(pathToTestBenchFileVBox);
        vBox.getChildren().add(pathToQuestaSimScriptVBox);
        vBox.getChildren().add(buttonHBox);

        root.getChildren().add(vBox);

        this.setScene(scene);
    }

    private String getTestBenchFilePath() {
        return verilogFile.getAbsolutePath().substring(0, verilogFile.getAbsolutePath().lastIndexOf('.')) + "_tb.sv";
    }

    private String getQuestaSimScriptPath() {
        return verilogFile.getAbsolutePath().substring(0, verilogFile.getAbsolutePath().lastIndexOf('.')) + "_tb_questa_rtl.do";
    }

    public File getVerilogFile() {
        return verilogFile;
    }

    public File getTestBenchFile() {
        File testBenchFile = new File(this.getTestBenchFilePath());
        return testBenchFile;
    }

    public File getQuestaSimScript() {
        File questaSimScript = new File(this.getQuestaSimScriptPath());
        return questaSimScript;
    }

    public boolean areFifosBetweenPes() {
        return fifosBetweenPes;
    }

    public int getInterPeFifoLength() {
        return interPeFifoLength;
    }

    public int getInputFifoLength() {
        return inputFifoLength;
    }

    public int getOutputFifoLength() {
        return outputFifoLength;
    }

    public boolean generateTestbenchAndQuestaSimScript() {
        return generateTestbenchAndQuestaSimScript;
    }

    public boolean wasExportPressed() {
        return export;
    }
}

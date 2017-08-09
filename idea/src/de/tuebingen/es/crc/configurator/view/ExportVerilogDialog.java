package de.tuebingen.es.crc.configurator.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.prefs.Preferences;

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
    private boolean generatePreprocessor;
    private int clockCycle;
    private boolean export;

    public ExportVerilogDialog(String crcDescriptionFilePath) {
        super();

        export = false;

        this.setTitle("Export Verilog Code");
        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        Preferences userPreferences = Preferences.userNodeForPackage(this.getClass());
        this.fifosBetweenPes = userPreferences.getBoolean("fifosBetweenPes", false);
        this.interPeFifoLength = userPreferences.getInt("interPeFifoLength", 2);
        this.inputFifoLength = userPreferences.getInt("inputFifoLength", 2);
        this.outputFifoLength = userPreferences.getInt("outputFifoLength", 2);
        this.generateTestbenchAndQuestaSimScript = userPreferences.getBoolean("generateTestbenchAndQuestaSimScript", false);
        this.generatePreprocessor = userPreferences.getBoolean("generatePreprocessor", false);
        this.clockCycle = userPreferences.getInt("clockCycle", 20);

        Group root = new Group();

        Scene scene = new Scene(root, 400, 575);

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        // checkbox for FIFOs between PEs
        CheckBox fifoBetweenPesCheckbox = new CheckBox();
        fifoBetweenPesCheckbox.setText("FIFOs between PEs");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Label and TextField for inter pe fifo length
        Label interPeFifoLengthLabel = new Label("Length of FIFOs between PEs");

        NumberTextField interPeFifoLengthTextField = new NumberTextField();
        interPeFifoLengthTextField.setMaxDigits(2);
        interPeFifoLengthTextField.setMinNumber(2);
        interPeFifoLengthTextField.setMinWidth(40);
        interPeFifoLengthTextField.setMaxWidth(40);
        interPeFifoLengthTextField.setText("" + interPeFifoLength);

        // cheap trick to make it final for lambda but still writable
        final int[] temporaryInterPeFifoLenth = {interPeFifoLength};

        fifoBetweenPesCheckbox.setOnAction( event -> {
            if(fifoBetweenPesCheckbox.isSelected()) {
                interPeFifoLengthTextField.setEditable(true);
            } else {
                interPeFifoLengthTextField.setEditable(false);
            }
        });

        fifoBetweenPesCheckbox.setSelected(fifosBetweenPes);
        interPeFifoLengthTextField.setEditable(fifosBetweenPes);

        // Label and TextField for input fifo length
        Label inputFifoLengthLabel = new Label("Length of input FIFOs (W)");

        NumberTextField inputFifoLengthTextField = new NumberTextField();
        inputFifoLengthTextField.setMaxDigits(2);
        // TODO minNumber = 0 => no FIFOs will be generated
        inputFifoLengthTextField.setMinNumber(2);
        inputFifoLengthTextField.setMinWidth(40);
        inputFifoLengthTextField.setMaxWidth(40);
        inputFifoLengthTextField.setText("" + inputFifoLength);


        // Label and TextField for output fifo length
        Label outputFifoLengthLabel = new Label("Length of output FIFOs (E)");

        NumberTextField outputFifoLengthTextField = new NumberTextField();
        outputFifoLengthTextField.setMaxDigits(2);
        // TODO minNumber = 0 => no FIFOs will be generated
        outputFifoLengthTextField.setMinNumber(2);
        outputFifoLengthTextField.setMinWidth(40);
        outputFifoLengthTextField.setMaxWidth(40);
        outputFifoLengthTextField.setText("" + outputFifoLength);


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
        Label pathToVerilogFileLabel = new Label("Path to Verilog File");

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
        Label pathToTestBenchFileLabel = new Label("Path to test bench file");

        // read only text field for path to testbench file
        TextField pathToTestBenchFileTextField = new TextField();
        pathToTestBenchFileTextField.setEditable(false);
        pathToTestBenchFileTextField.setText("...");

        VBox pathToTestBenchFileVBox = new VBox();
        pathToTestBenchFileVBox.getChildren().addAll(pathToTestBenchFileLabel, pathToTestBenchFileTextField);

        // label for text field for path to QuestaSim Script
        Label pathToQuestaSimScriptLabel = new Label("Path to QuestaSim script");

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

        generateTestBenchAndQuestaSimScriptCheckBox.setSelected(generateTestbenchAndQuestaSimScript);
        if(generateTestbenchAndQuestaSimScript) {
            pathToTestBenchFileTextField.setText(this.getTestBenchFilePath());
            pathToQuestaSimScriptTextField.setText(this.getQuestaSimScriptPath());
        }

        // check box for the generation of a preprocessor.v
        CheckBox generatePreprocessorCheckBox = new CheckBox("Generate preprocessor.v");

        // label for text field for path to testbench file
        Label pathToPreprocessorLabel = new Label("Path to preprocessor.v");

        // read only text field for path to preprocessor.v
        TextField pathToPreprocessorTextField = new TextField();
        pathToPreprocessorTextField.setEditable(false);
        pathToPreprocessorTextField.setText("...");

        VBox pathToPreprocessorVBox = new VBox();
        pathToPreprocessorVBox.getChildren().addAll(pathToPreprocessorLabel, pathToPreprocessorTextField);

        // label for clock cycle
        Label clockCycleLabel = new Label("Clock Cycle ");
        clockCycleLabel.setMinHeight(20);
        clockCycleLabel.setAlignment(Pos.BOTTOM_LEFT);

        // number text field for clock cycle
        NumberTextField clockCycleTextField = new NumberTextField();
        clockCycleTextField.setEditable(false);
        clockCycleTextField.setMinNumber(1);
        clockCycleTextField.setMaxNumber(999);
        clockCycleTextField.setMaxDigits(3);
        clockCycleTextField.setText("" + clockCycle);
        clockCycleTextField.setMaxWidth(45);
        clockCycleTextField.setMinWidth(45);

        // nano seconds label
        Label nanoSecondsLabel = new Label("ns");
        nanoSecondsLabel.setMinHeight(20);
        nanoSecondsLabel.setAlignment(Pos.BOTTOM_LEFT);

        HBox clockCycleHBox = new HBox(3);
        clockCycleHBox.getChildren().addAll(clockCycleLabel, clockCycleTextField, nanoSecondsLabel);

        generatePreprocessorCheckBox.setOnAction(event -> {
            if(generatePreprocessorCheckBox.isSelected()) {
                verilogFile = new File(pathToVerilogFileTextField.getText());
                pathToPreprocessorTextField.setText(this.getPreprocessorPath());
                clockCycleTextField.setEditable(true);
            } else {
                pathToPreprocessorTextField.setText("...");
                clockCycleTextField.setEditable(false);
            }
        });

        generatePreprocessorCheckBox.setSelected(generatePreprocessor);
        if(generatePreprocessor) {
            pathToPreprocessorTextField.setText(this.getPreprocessorPath());
            clockCycleTextField.setEditable(true);
        }

        // update test bench file and QuestaSim script text field when path to verilog file changes
        pathToVerilogFileTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            verilogFile = new File(pathToVerilogFileTextField.getText());
            if(generateTestBenchAndQuestaSimScriptCheckBox.isSelected()) {
                pathToTestBenchFileTextField.setText(this.getTestBenchFilePath());
                pathToQuestaSimScriptTextField.setText(this.getQuestaSimScriptPath());
            }
            if(generatePreprocessorCheckBox.isSelected()) {
                pathToPreprocessorTextField.setText(this.getPreprocessorPath());
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
            generatePreprocessor = generatePreprocessorCheckBox.isSelected();
            clockCycle = clockCycleTextField.getNumber();

            userPreferences.putBoolean("fifosBetweenPes", fifosBetweenPes);
            userPreferences.putInt("interPeFifoLength", interPeFifoLength);
            userPreferences.putInt("inputFifoLength", inputFifoLength);
            userPreferences.putInt("outputFifoLength", outputFifoLength);
            userPreferences.putBoolean("generateTestbenchAndQuestaSimScript", generateTestbenchAndQuestaSimScript);
            userPreferences.putBoolean("generatePreprocessor", generatePreprocessor);
            userPreferences.putInt("clockCycle", clockCycle);

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
        vBox.getChildren().add(generatePreprocessorCheckBox);
        vBox.getChildren().add(pathToPreprocessorVBox);
        vBox.getChildren().add(clockCycleHBox);
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

    private String getPreprocessorPath() {
        return verilogFile.getAbsolutePath().substring(0, verilogFile.getAbsolutePath().lastIndexOf('/')) + "/preprocessor.v";
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

    public File getPreprocessorFile() {
        File preprocessorFile = new File(this.getPreprocessorPath());
        return preprocessorFile;
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

    public int getClockCycle() {
        return clockCycle;
    }

    public boolean generateTestbenchAndQuestaSimScript() {
        return generateTestbenchAndQuestaSimScript;
    }

    public boolean generatePreprocessor() {
        return generatePreprocessor;
    }

    public boolean wasExportPressed() {
        return export;
    }
}

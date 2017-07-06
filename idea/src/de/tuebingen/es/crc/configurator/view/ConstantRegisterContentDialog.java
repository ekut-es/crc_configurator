package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.PE;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static de.tuebingen.es.crc.configurator.model.Truncator.truncateNumber;

/**
 * Created by luebeck on 7/4/17.
 */
public class ConstantRegisterContentDialog extends Stage {

    private PE pe;
    private int row;
    private int column;

    private long constantRegisterDataContent;
    private boolean constantRegisterFlagContent;

    public boolean apply;

    private final String allowedPrefixStringDecimal = "[0-9]+'d";
    private final String allowedPrefixStringHexadecimal = "[0-9]+'h, 0x";
    private final String allowedPrefixStringBinary = "[0-9]+'b, 0b";

    public long getConstantRegisterDataContent() {
        return constantRegisterDataContent;
    }
    public boolean getConstantRegisterFlagContent() {
        return constantRegisterFlagContent;
    }

    private String removeFormat(String number) {
        number = number.replaceAll("(^[0-9]+'d|^[0-9]+'h|^[0-9]+'b|^0x|^0b|_| )", "");

        return number;
    }

    public ConstantRegisterContentDialog(PE pe, int row, int column, int dataWidth) {
        super();

        this.pe = pe;
        this.row = row;
        this.column = column;

        this.setTitle("Constant Register Content PE " + row + "," + column);
        this.setResizable(false);
        this.initStyle(StageStyle.UNIFIED);
        this.initModality(Modality.APPLICATION_MODAL);

        apply = false;

        Group root = new Group();

        Scene scene = new Scene(root, 280, 200);

        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        TextField contentTextField = new TextField("0x" + Long.toHexString(pe.getConstantRegisterDataContent()));

        final ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButtonDecimal = new RadioButton("Decimal");
        radioButtonDecimal.setToggleGroup(toggleGroup);

        RadioButton radioButtonHexadecimal = new RadioButton("Hexadecimal");
        radioButtonHexadecimal.setToggleGroup(toggleGroup);
        radioButtonHexadecimal.setSelected(true);

        RadioButton radioButtonBinary = new RadioButton("Binary");
        radioButtonBinary.setToggleGroup(toggleGroup);

        HBox hBoxRadixRadioButtons = new HBox(3);
        hBoxRadixRadioButtons.getChildren().addAll(
                radioButtonDecimal,
                radioButtonHexadecimal,
                radioButtonBinary
        );

        Label allowedPrefixLabel = new Label("Allowed Prefix: " + allowedPrefixStringHexadecimal);
        allowedPrefixLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.NORMAL, 10));


        // change allowed prefix when radix was changed
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(radioButtonDecimal.isSelected()) {

                contentTextField.setText(removeFormat(contentTextField.getText()));

                // convert radix
                Integer value = 0;
                if(oldValue == radioButtonHexadecimal) {
                    value = Integer.parseInt(contentTextField.getText(),16);
                } else if(oldValue == radioButtonBinary) {
                    value = Integer.parseInt(contentTextField.getText(), 2);
                }

                contentTextField.setText(value.toString());
                allowedPrefixLabel.setText("Allowed Prefix: " + allowedPrefixStringDecimal);
            }
            else if(radioButtonHexadecimal.isSelected()) {
                contentTextField.setText(removeFormat(contentTextField.getText()));

                // convert radix
                Integer value = 0;
                if(oldValue == radioButtonDecimal) {
                    value = Integer.parseInt(contentTextField.getText());
                } else if(oldValue == radioButtonBinary) {
                    value = Integer.parseInt(contentTextField.getText(), 2);
                }

                contentTextField.setText("0x" + Integer.toHexString(value));
                allowedPrefixLabel.setText("Allowed Prefix: " + allowedPrefixStringHexadecimal);
            }
            else if(radioButtonBinary.isSelected()) {

                contentTextField.setText(removeFormat(contentTextField.getText()));

                // convert radix
                Integer value = 0;
                if(oldValue == radioButtonDecimal) {
                    value = Integer.parseInt(contentTextField.getText());
                } else if(oldValue == radioButtonHexadecimal) {
                    value = Integer.parseInt(contentTextField.getText(), 16);
                }

                contentTextField.setText("0b" + Integer.toBinaryString(value));
                allowedPrefixLabel.setText("Allowed Prefix: " + allowedPrefixStringBinary);
            }
        });

        CheckBox flagCheckBox = new CheckBox("Set Flag");
        flagCheckBox.setSelected(pe.getConstantRegisterFlagContent());

        Button cancelButton = new Button("Cancel");
        Button applyButton = new Button("Apply");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        // set const reg content and close when "Apply" button was pressed
        applyButton.setOnAction(event -> {

            if (radioButtonDecimal.isSelected()) {
                constantRegisterDataContent = truncateNumber(Long.parseLong(removeFormat(contentTextField.getText()), 10), dataWidth);
            } else if (radioButtonHexadecimal.isSelected()) {
                constantRegisterDataContent = truncateNumber(Long.parseLong(removeFormat(contentTextField.getText()), 16), dataWidth);
            } else if (radioButtonBinary.isSelected()) {
                constantRegisterDataContent = truncateNumber(Long.parseLong(removeFormat(contentTextField.getText()), 2), dataWidth);
            }

            constantRegisterFlagContent = flagCheckBox.isSelected();

            apply = true;
            this.close();
        });


        HBox hBoxCancelApply = new HBox(2);
        hBoxCancelApply.setSpacing(15);

        hBoxCancelApply.getChildren().addAll(cancelButton,applyButton);

        vBox.getChildren().add(contentTextField);
        vBox.getChildren().add(allowedPrefixLabel);
        vBox.getChildren().add(hBoxRadixRadioButtons);
        vBox.getChildren().add(flagCheckBox);
        vBox.getChildren().add(hBoxCancelApply);

        root.getChildren().add(vBox);

        this.setScene(scene);

    }

}

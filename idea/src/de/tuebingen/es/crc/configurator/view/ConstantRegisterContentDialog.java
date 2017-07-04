package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.PE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by luebeck on 7/4/17.
 */
public class ConstantRegisterContentDialog extends Stage {

    private PE pe;
    private int row;
    private int column;

    private int constantRegisterContent;

    public boolean apply;

    private final String allowedPrefixStringDecimal = "[0-9]+'d";
    private final String allowedPrefixStringHexadecimal = "[0-9]+'h, 0x";
    private final String allowedPrefixStringBinary = "[0-9]+'b, 0b";

    public int getConstantRegisterContent() {
        return constantRegisterContent;
    }

    private String removePrefix(String number) {
        number = number.replaceAll("(^[0-9]+'d|^[0-9]+'h|^[0-9]+'b|^0x|^0b)", "");
        return number;
    }

    public ConstantRegisterContentDialog(PE pe, int row, int column) {
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

        Scene scene = new Scene(root, 280, 165);

        VBox vBox = new VBox(4);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(20);

        TextField contentTextField = new TextField("0x" + Integer.toHexString(pe.getConstantRegContent()));

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
                allowedPrefixLabel.setText("Allowed Prefix: " + allowedPrefixStringDecimal);
                contentTextField.setText(removePrefix(contentTextField.getText()));
            }
            else if(radioButtonHexadecimal.isSelected()) {
                allowedPrefixLabel.setText("Allowed Prefix: " + allowedPrefixStringHexadecimal);
                contentTextField.setText(removePrefix(contentTextField.getText()));
            }
            else if(radioButtonBinary.isSelected()) {
                allowedPrefixLabel.setText("Allowed Prefix: " + allowedPrefixStringBinary);
                contentTextField.setText(removePrefix(contentTextField.getText()));
            }
        });


        Button cancelButton = new Button("Cancel");
        Button applyButton = new Button("Apply");

        // close dialog when "Cancel" button was pressed
        cancelButton.setOnAction(event -> this.close());

        // set const reg content and close when "Apply" button was pressed
        applyButton.setOnAction(event -> {

            if (radioButtonDecimal.isSelected()) {
                constantRegisterContent = Integer.parseInt(removePrefix(contentTextField.getText()), 10);
            } else if (radioButtonHexadecimal.isSelected()) {
                constantRegisterContent = Integer.parseInt(removePrefix(contentTextField.getText()), 16);
            } else if (radioButtonBinary.isSelected()) {
                constantRegisterContent = Integer.parseInt(removePrefix(contentTextField.getText()), 2);
            }

            apply = true;
            this.close();
        });

        HBox hBoxCancelApply = new HBox(2);
        hBoxCancelApply.setSpacing(15);

        hBoxCancelApply.getChildren().addAll(cancelButton,applyButton);

        vBox.getChildren().add(contentTextField);
        vBox.getChildren().add(allowedPrefixLabel);
        vBox.getChildren().add(hBoxRadixRadioButtons);
        vBox.getChildren().add(hBoxCancelApply);

        root.getChildren().add(vBox);

        this.setScene(scene);

    }

}

package de.tuebingen.es.crc.configurator.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Created by Konstantin (Konze) Lübeck on 27/07/16.
 */
public class NumberTextField extends TextField {

    private Integer maxDigits;
    private Integer minNumber;
    private Integer maxNumber;

    public NumberTextField() {
        super();
        maxDigits = 0;
        minNumber = null;
        maxNumber = null;
        this.setText("0");

        // if minNumber of maxNumber is not null and a value was X not in [minNumber, maxNumber] was entered it will be
        // replaced with the minNumber or maxNumber
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // is triggered when NumberTextField lost focus
            if(!newValue) {
                if(!this.getText().isEmpty() && (minNumber != null || maxNumber != null)) {
                    Integer number = Integer.parseInt(this.getText());

                    if(minNumber != null && number < minNumber) {
                        this.setText(minNumber.toString());
                    }

                    if(maxNumber != null && number > maxNumber) {
                        this.setText(maxNumber.toString());
                    }
                }
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    public void setMinNumber(Integer minNumber) {
        this.minNumber = minNumber;
        this.setText(minNumber.toString());
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (this.validate(text)) {
            super.replaceText(start, end, text);
            this.adaptText();
        }
    }


    @Override
    public void replaceSelection(String text) {
        if (this.validate(text)) {
            super.replaceSelection(text);
            this.adaptText();
        }
    }

    private boolean validate(String text) {
        return text.matches("[0-9]*");
    }

    private void adaptText() {
        if(maxDigits > 0) {
            if(this.getText().length() > maxDigits) {
                this.setText(getText().substring(0, maxDigits));
            }
        }
    }

    public int getNumber() {
        return Integer.parseInt(this.getText());
    }

}

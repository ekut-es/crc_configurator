package de.tuebingen.es.crc.configurator.view;

import javafx.scene.control.TextField;

/**
 * Created by Konstantin (Konze) Lübeck on 27/07/16.
 */
public class NumberTextField extends TextField {

    private int maxDigits;

    public NumberTextField() {
        super();
        maxDigits = 0;
    }

    @SuppressWarnings("SameParameterValue")
    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (this.validate(text)) {
            super.replaceText(start, end, text);
            this.shrink();
        }
    }


    @Override
    public void replaceSelection(String text) {
        if (this.validate(text)) {
            super.replaceSelection(text);
            this.shrink();
        }
    }

    private boolean validate(String text) {
        return text.matches("[0-9]*");
    }

    private void shrink() {
        if(maxDigits > 0) {
            if(this.getText().length() > maxDigits) {
                this.setText(getText().substring(0, maxDigits));
            }
        }
    }
}

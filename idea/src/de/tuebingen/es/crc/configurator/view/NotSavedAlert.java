package de.tuebingen.es.crc.configurator.view;

import javafx.scene.control.*;

import java.util.Optional;

/**
 * Created by Konstantin (Konze) Lübeck on 27/07/16.
 */
public class NotSavedAlert extends Alert {

    public enum ButtonPressed {
        SAVE, DONT_SAVE, CANCEL
    }

    public NotSavedAlert() {
        super(AlertType.WARNING);

        this.setTitle("Warning");
        this.setContentText("CRC Description File was not saved!");
    }

    public ButtonPressed displayAndWait() {
        this.getButtonTypes().clear();
        ButtonType dontSave = new ButtonType("Don't Save");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType save = new ButtonType("Save");

        this.getButtonTypes().setAll(dontSave,cancel,save);

        setDefaultButton(save);

        Optional<ButtonType> result = this.showAndWait();

        if(result.get() == dontSave) {
            return ButtonPressed.DONT_SAVE;
        } else if(result.get() == save) {
            return ButtonPressed.SAVE;
        } else {
            return ButtonPressed.CANCEL;
        }
    }

    private void setDefaultButton (ButtonType defaultButton) {

        DialogPane pane = this.getDialogPane();

        for ( ButtonType t : this.getButtonTypes() ) {
            ((Button) pane.lookupButton(t)).setDefaultButton(t == defaultButton);
        }
    }
}

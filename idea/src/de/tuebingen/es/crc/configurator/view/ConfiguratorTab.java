package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.Model;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public abstract class ConfiguratorTab extends Tab {

    protected Model model;

    protected Canvas canvas;

    public static final int PE_DRAW_SIZE = 200;
    public static final int CANVAS_PADDING = 40;
    public static final int INTER_PE_DISTANCE = 80;

    protected final int peDrawSizeTwentieth = (PE_DRAW_SIZE/20);

    public ConfiguratorTab() {
        this.setOnSelectionChanged(event -> selectionChanged());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    protected abstract void selectionChanged();

    public abstract void update();

    protected void drawFU(GraphicsContext gc, double x, double y, boolean showFUText) {

        gc.strokePolygon(
                new double[] {
                        0+7*peDrawSizeTwentieth+x,
                        6*peDrawSizeTwentieth+7*peDrawSizeTwentieth+x,
                        6*peDrawSizeTwentieth+7*peDrawSizeTwentieth+x,
                        0+7*peDrawSizeTwentieth+x,
                        0+7*peDrawSizeTwentieth+x,
                        2*peDrawSizeTwentieth+7*peDrawSizeTwentieth+x,
                        0+7*peDrawSizeTwentieth+x
                },
                new double[] {
                        0+4*peDrawSizeTwentieth+y,
                        3*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        11*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        13*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        8*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        7*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y,
                        6*peDrawSizeTwentieth+3*peDrawSizeTwentieth+y
                },
                7
        );

        if(showFUText) {
            gc.fillText("FU", 9 * peDrawSizeTwentieth + x, 13 * peDrawSizeTwentieth + y);
        }
   }

   protected TextArea addCommentTextArea(VBox outerVBox) {
        VBox innerVBox = new VBox(2);
        innerVBox.setPadding(new Insets(10,10,10,10));

        Label commentLabel = new Label("Comment");

        innerVBox.getChildren().add(commentLabel);

        TextArea commentTextArea = new TextArea();
        commentTextArea.setFont(Font.font("Courier", 14));
        commentTextArea.wrapTextProperty().set(true);
        commentTextArea.setMinHeight(100);
        commentTextArea.setMaxHeight(100);

        innerVBox.getChildren().add(commentTextArea);

        outerVBox.getChildren().add(innerVBox);

        return commentTextArea;
   }
}

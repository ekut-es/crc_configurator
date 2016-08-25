package de.tuebingen.es.crc.configurator.view;

import de.tuebingen.es.crc.configurator.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;

/**
 * Created by Konstantin (Konze) Lübeck on 26/07/16.
 */
public abstract class ConfiguratorTab extends Tab {

    protected Model model;

    public static final int PE_DRAW_SIZE = 200;
    public static final int CANVAS_PADDING = 40;
    public static final int INTER_PE_DISTANCE = 80;

    protected final int peDrawSizeTwentieth = (PE_DRAW_SIZE/20);

    public abstract void update();

    protected void drawFU(GraphicsContext gc, double x, double y) {

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

        gc.fillText("FU", 9*peDrawSizeTwentieth+x, 13*peDrawSizeTwentieth+y);

    }
}

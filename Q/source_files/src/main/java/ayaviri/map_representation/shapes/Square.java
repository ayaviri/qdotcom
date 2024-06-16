package ayaviri.map_representation.shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Square extends AShape {
    public Square() {
        super("square", 2);
    }

    public Shape draw(double topLeftX, double topLeftY) {
        return new Rectangle2D.Double(
                topLeftX + 3, topLeftY + 3, this.IMAGE_WIDTH - 6, this.IMAGE_HEIGHT - 6);
    }
}

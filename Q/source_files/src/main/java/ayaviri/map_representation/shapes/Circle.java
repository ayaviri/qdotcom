package ayaviri.map_representation.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Circle extends AShape {
    public Circle() {
        super("circle", 3);
    }

    public Shape draw(double topLeftX, double topLeftY) {
        return new Ellipse2D.Double(
                topLeftX + 3, topLeftY + 3, this.IMAGE_WIDTH - 6, this.IMAGE_HEIGHT - 6);
    }
}

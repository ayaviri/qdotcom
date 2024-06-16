package ayaviri.map_representation.shapes;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Clover extends AShape {
    public Clover() {
        super("clover", 4);
    }

    // TODO: hardcoded values based on image size 50
    public Shape draw(double topLeftX, double topLeftY) {
        Area center = new Area(new Rectangle2D.Double(topLeftX + 14, topLeftY + 14, 22, 22));
        Area top = new Area(new Ellipse2D.Double(topLeftX + 14, topLeftY + 3, 22, 22));
        Area bottom = new Area(new Ellipse2D.Double(topLeftX + 14, topLeftY + 25, 22, 22));
        Area left = new Area(new Ellipse2D.Double(topLeftX + 3, topLeftY + 14, 22, 22));
        Area right = new Area(new Ellipse2D.Double(topLeftX + 25, topLeftY + 14, 22, 22));
        center.add(top);
        center.add(bottom);
        center.add(left);
        center.add(right);
        return center;
    }
}

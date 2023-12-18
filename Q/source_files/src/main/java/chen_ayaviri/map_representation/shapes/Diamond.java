package chen_ayaviri.map_representation.shapes;

import java.awt.*;
import java.awt.geom.Path2D;

public class Diamond extends AShape {
    public Diamond() {
        super("diamond", 5);
    }

     public Shape draw(double topLeftX, double topLeftY) {
        Path2D path = new Path2D.Double();
        path.moveTo(this.CENTER_X + topLeftX, topLeftY + 3);
        path.lineTo(topLeftX + 3, this.CENTER_Y + topLeftY);
        path.lineTo(this.CENTER_X + topLeftX, this.IMAGE_HEIGHT + topLeftY - 3);
        path.lineTo(this.IMAGE_WIDTH + topLeftX - 3, this.CENTER_Y + topLeftY);
        path.lineTo(this.CENTER_X + topLeftX, topLeftY + 3);
        return path;
     }
}

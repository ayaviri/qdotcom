package chen_ayaviri.map_representation.shapes;

import java.awt.*;
import java.awt.geom.Path2D;

public class AStar extends AShape {
     private final double OUTER_RADIUS = this.IMAGE_WIDTH / 2;
     private final double INNER_RADIUS = this.OUTER_RADIUS / 2.63;
     private int numberOfRays;
     // the angle between the x-axis and the first vertex above and to the right of the x-axis in radians
     private double startingAngleRad;

    protected AStar(int numberOfRays, double startingAngleRad, String name, int ordinal) {
         super(name, ordinal);
         this.numberOfRays = numberOfRays;
         this.startingAngleRad = startingAngleRad;
    }

     public Shape draw(double topLeftX, double topLeftY) {
       // pulled from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
       Path2D path = new Path2D.Double();
       double deltaAngleRad = Math.PI / this.numberOfRays;
       for (int index = 0; index < this.numberOfRays * 2; index++) {
         double currentAngleRad = this.startingAngleRad + (deltaAngleRad * index);
         double relativeX = Math.cos(currentAngleRad);
         double relativeY = Math.sin(currentAngleRad);
         if ((index % 2) == 0) {
           relativeX *= this.OUTER_RADIUS;
           relativeY *= this.OUTER_RADIUS;
         } else {
           relativeX *= this.INNER_RADIUS;
           relativeY *= this.INNER_RADIUS;
         }

         if (index == 0) {
           path.moveTo(topLeftX + this.CENTER_X + relativeX, topLeftY + this.CENTER_Y + relativeY);
         } else {
           path.lineTo(topLeftX + this.CENTER_X + relativeX, topLeftY + this.CENTER_Y + relativeY);
         }
       }
       path.closePath();
       return path;
     }
}

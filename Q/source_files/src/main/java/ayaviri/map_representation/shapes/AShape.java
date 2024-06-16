package ayaviri.map_representation.shapes;

import ayaviri.map_representation.Tile;
import java.awt.*;

public abstract class AShape {
    protected final double IMAGE_WIDTH = Tile.SIZE;
    protected final double IMAGE_HEIGHT = Tile.SIZE;
    protected final double CENTER_X = this.IMAGE_WIDTH / 2;
    protected final double CENTER_Y = this.IMAGE_HEIGHT / 2;
    // NOTE: every shape has a unique name and ordinal
    protected final String name;
    // TODO: shapes just render themselves and define a canonical ordering on themselves
    // Consider giving the responsibility of rendering to its own component and simply using
    // an enum to define shapes
    protected final int ordinal;

    protected AShape(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    // Returns the shape drawn with the top left corner at the given coordinates
    public abstract Shape draw(double topLeftX, double topLeftY);

    public boolean equals(Object other) {
        return other instanceof AShape && this.name.equals(((AShape) other).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name;
    }
}

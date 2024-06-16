package ayaviri.visuals;

import ayaviri.map_representation.Posn;
import ayaviri.map_representation.Tile;
import java.util.Map;

// Represents bounds for the map, where the bounds form the tighest rectangle around the tiles.
// The bounds are represented using Cartesian coordinates.
public class QMapBounds {
    public final int maxY;
    public final int minY;
    public final int maxX;
    public final int minX;

    private QMapBounds(int maxY, int minY, int maxX, int minX) {
        this.maxY = maxY;
        this.minY = minY;
        this.maxX = maxX;
        this.minX = minX;
    }

    // Calculates the bounds from the places in the given map.
    public static QMapBounds create(Map<Posn, Tile> tiles) {
        int currMaxY = 0;
        int currMinY = 0;
        int currMaxX = 0;
        int currMinX = 0;
        for (Posn pos : tiles.keySet()) {
            currMaxY = Math.max(currMaxY, pos.getY());
            currMinY = Math.min(currMinY, pos.getY());
            currMaxX = Math.max(currMaxX, pos.getX());
            currMinX = Math.min(currMinX, pos.getX());
        }
        return new QMapBounds(currMaxY, currMinY, currMaxX, currMinX);
    }

    // Calculates the number of rows between the top and bottom bound.
    public int numRows() {
        return this.maxY - this.minY + 1;
    }

    // Calculates the number of columns between the left and right bound.
    public int numCols() {
        return this.maxX - this.minX + 1;
    }
}

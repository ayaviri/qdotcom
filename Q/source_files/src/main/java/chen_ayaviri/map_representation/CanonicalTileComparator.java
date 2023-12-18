package chen_ayaviri.map_representation;

import chen_ayaviri.map_representation.shapes.CanonicalShapeComparator;

import java.util.Comparator;

public class CanonicalTileComparator implements Comparator<Tile> {

    public int compare(Tile first, Tile second) {
        // using enum values, but the color one might need an enumeration of
        // its own to facilitate
        int diff = new CanonicalShapeComparator().compare(first.getShape(), second.getShape());

        if (diff == 0) {
            return new CanonicalQColorComparator().compare(first.getColor(), second.getColor());
        }

        return diff;
    }
}

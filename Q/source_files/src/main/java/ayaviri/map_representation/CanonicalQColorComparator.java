package ayaviri.map_representation;

import ayaviri.map_representation.Tile.QColor;
import java.util.Comparator;

public class CanonicalQColorComparator implements Comparator<QColor> {
    @Override
    public int compare(QColor o1, QColor o2) {
        return o1.ordinal() - o2.ordinal();
    }
}

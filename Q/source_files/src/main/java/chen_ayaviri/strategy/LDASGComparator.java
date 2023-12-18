package chen_ayaviri.strategy;

import chen_ayaviri.common.QMap;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.map_representation.RowColOrderComparator;

import java.util.Comparator;

// Comparator on positions WITHIN THE CONTEXT OF THIS MAP
// Sorts positions in order of descending number of neighbors with tiles
// Ties are then broken by row-column order on positions
public class LDASGComparator implements Comparator<Posn> {
    private final QMap map;

    public LDASGComparator(QMap map) {
        this.map = map;
    }

    public int compare(Posn first, Posn second) {
        int diff = map.getNumberOfNeighbors(second) - map.getNumberOfNeighbors(first);

        if (diff == 0) {
            diff = new RowColOrderComparator().compare(first, second);
        }

        return diff;
    }
}

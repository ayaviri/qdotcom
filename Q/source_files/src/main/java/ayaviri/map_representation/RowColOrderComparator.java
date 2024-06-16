package ayaviri.map_representation;

import java.util.Comparator;

public class RowColOrderComparator implements Comparator<Posn> {
    public int compare(Posn a, Posn b) {
        int diff = a.getY() - b.getY();

        if (diff == 0) {
            return a.getX() - b.getX();
        }

        return diff;
    }
}

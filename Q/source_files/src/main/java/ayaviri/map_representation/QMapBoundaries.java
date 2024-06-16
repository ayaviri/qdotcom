package ayaviri.map_representation;

import ayaviri.common.QMap;
import ayaviri.utils.Pair;
import java.util.ArrayList;
import java.util.List;

// Represents the positions of top left and bottom right corners of the smallest rectangle
// that outlines the given QMap
public class QMapBoundaries {
    private final Posn topLeft;
    private final Posn bottomRight;

    public QMapBoundaries(QMap map) {
        Pair<Posn, Posn> mapBoundaries = this.computeMapBoundaries(map);
        this.topLeft = mapBoundaries.getFirst();
        this.bottomRight = mapBoundaries.getSecond();
    }

    public Posn getTopLeft() {
        return this.topLeft;
    }

    public Posn getBottomRight() {
        return this.bottomRight;
    }

    private Pair<Posn, Posn> computeMapBoundaries(QMap map) {
        List<Posn> positions = new ArrayList<>(map.getTiles().keySet());
        positions.sort(new RowColOrderComparator());

        // The X values for each of these must be found through iteration of map positions
        Posn topLeft = positions.get(0);
        Posn bottomRight = positions.get(positions.size() - 1);

        for (Posn pos : positions) {
            topLeft = topLeft.computeNewWithSmallestX(pos);
            bottomRight = bottomRight.computeNewWithLargestX(pos);
        }

        return new Pair<Posn, Posn>(topLeft, bottomRight);
    }
}

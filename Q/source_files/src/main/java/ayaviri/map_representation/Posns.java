package ayaviri.map_representation;

import java.util.List;

// Represents a class of utility functions that can be performed on a list of Posn objects
public class Posns {
    public static boolean allSameRowXorColumn(List<Posn> positions) {
        if (positions.isEmpty()) {
            return true;
        }

        Posn firstPos = positions.get(0);
        boolean sameRow = true;
        boolean sameColumn = true;

        for (Posn pos : positions) {
            sameRow = sameRow && pos.sameRow(firstPos);
            sameColumn = sameColumn && pos.sameColumn(firstPos);

            if (!sameRow && !sameColumn) {
                return false;
            }
        }

        return true;
    }
}

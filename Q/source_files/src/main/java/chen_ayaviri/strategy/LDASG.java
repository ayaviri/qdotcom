package chen_ayaviri.strategy;

import chen_ayaviri.common.QMap;
import chen_ayaviri.map_representation.Posn;

import java.util.List;

// A DAG strategy that sorts the current hand by canonical order and the valid positions
// on which a tile can be placed, first by descending number of neighbors with tiles
// and second by row-column order
public class LDASG extends ADAG {
    @Override
    protected void sortCandidatePositions(QMap currentMap, List<Posn> candidatePositions) {
        // Comparator receives a reference to the map of the current state, so it is updated
        // as the strategy computes placements
        candidatePositions.sort(new LDASGComparator(currentMap));
    }

    @Override
    public String toString() {
        return "lsdag";
    }
}

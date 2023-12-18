package chen_ayaviri.strategy;

import chen_ayaviri.common.QMap;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.map_representation.RowColOrderComparator;

import java.util.List;

// A DAG strategy that sorts the current hand by canonical order and the
// valid positions on which a tile can be placed by row-column order
public class DAG extends ADAG {
    @Override
    protected void sortCandidatePositions(QMap currentMap, List<Posn> candidatePositions) {
        candidatePositions.sort(new RowColOrderComparator());
    }

    @Override
    public String toString() {
        return "dag";
    }
}

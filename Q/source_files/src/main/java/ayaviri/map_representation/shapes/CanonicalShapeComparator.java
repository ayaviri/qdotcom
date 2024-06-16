package ayaviri.map_representation.shapes;

import java.util.Comparator;

public class CanonicalShapeComparator implements Comparator<AShape> {
    @Override
    public int compare(AShape o1, AShape o2) {
        return o1.ordinal - o2.ordinal;
    }
}

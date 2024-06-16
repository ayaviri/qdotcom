package ayaviri.common;

import static org.junit.Assert.*;

import ayaviri.map_representation.Posn;
import ayaviri.map_representation.RowColOrderComparator;
import org.junit.Before;
import org.junit.Test;

public class RowColOrderComparatorTest {
    RowColOrderComparator comparator;
    Posn first;
    Posn second;
    Posn third;
    Posn fourth;

    @Before
    public void setUp() {
        comparator = new RowColOrderComparator();
        first = new Posn(0, 0);
        second = new Posn(3, 2);
        third = new Posn(5, 2);
        fourth = new Posn(10, 2);
    }

    @Test
    public void testPosnsOnDifferentRow() {
        assertTrue(comparator.compare(first, second) < 0);
    }

    @Test
    public void testPosnsOnSameRow() {
        assertTrue(comparator.compare(second, third) < 0);
        assertTrue(comparator.compare(fourth, third) > 0);
        assertTrue(comparator.compare(new Posn(3, 2), second) == 0);
    }
}

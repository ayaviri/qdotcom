package ayaviri.map_representation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class PosnTest {
    private Posn posn0;
    private Posn posn0A;
    private Posn posn1;
    private Posn posn2;

    @Before
    public void setUp() {
        posn0 = new Posn(0, 0);
        posn0A = new Posn(0, 0);
        posn1 = new Posn(0, 1);
        posn2 = new Posn(1, 0);
    }

    @Test
    public void testSameRow() {
        assertTrue(posn0.sameRow(posn2));
        assertFalse(posn0.sameRow(posn1));
    }

    @Test
    public void testSameCol() {
        assertTrue(posn0.sameColumn(posn1));
        assertFalse(posn0.sameColumn(posn2));
    }

    @Test
    public void testGetRowNeighbors() {
        List<Posn> expected = new ArrayList<>();
        expected.add(posn2);
        expected.add(new Posn(-1, 0));
        assertEquals(posn0.getNeighboringRowPositions(), expected);
    }

    @Test
    public void testGetColumnNeighbors() {
        List<Posn> expected = new ArrayList<>();
        expected.add(new Posn(0, -1));
        expected.add(posn1);
        assertEquals(posn0.getNeighboringColumnPositions(), expected);
    }

    @Test
    public void testGetNeighboringPositions() {
        List<Posn> expected = new ArrayList<>();
        expected.add(posn2);
        expected.add(new Posn(-1, 0));
        expected.add(new Posn(0, -1));
        expected.add(posn1);
        assertEquals(posn0.getNeighboringPositions(), expected);
    }

    @Test
    public void testTranslateRelativeTo() {
        Posn posn = new Posn(3, 4);
        assertEquals(posn.translateRelativeTo(new Posn(-1, -2)), new Posn(4, 6));
        assertEquals(posn.translateRelativeTo(new Posn(0, 3)), new Posn(3, 1));
        assertEquals(posn.translateRelativeTo(new Posn(2, 4)), new Posn(1, 0));
    }

    // TranslateDirection function objects

    @Test
    public void testTranslateUp() {
        Posn.TranslateDirection translate = new Posn.TranslateUp();

        assertEquals(translate.apply(posn0, 1), new Posn(0, -1));
        assertEquals(translate.apply(posn0, 3), new Posn(0, -3));
    }

    @Test
    public void testTranslateDown() {
        Posn.TranslateDirection translate = new Posn.TranslateDown();

        assertEquals(translate.apply(posn0, 1), new Posn(0, 1));
        assertEquals(translate.apply(posn0, 3), new Posn(0, 3));
    }

    @Test
    public void testTranslateRight() {
        Posn.TranslateDirection translate = new Posn.TranslateRight();

        assertEquals(translate.apply(posn0, 1), new Posn(1, 0));
        assertEquals(translate.apply(posn0, 3), new Posn(3, 0));
    }

    @Test
    public void testTranslateLeft() {
        Posn.TranslateDirection translateUp = new Posn.TranslateLeft();

        assertEquals(translateUp.apply(posn0, 1), new Posn(-1, 0));
        assertEquals(translateUp.apply(posn0, 3), new Posn(-3, 0));
    }

    @Test
    public void testEquals() {
        // reflexivity
        assertEquals(posn0, posn0);

        // symmetry
        assertTrue(posn0.equals(posn0A) && posn0A.equals(posn0));

        // transitivity
        assertTrue(
                posn0.equals(posn0A)
                        && posn0A.equals(new Posn(0, 0))
                        && posn0.equals(new Posn(0, 0)));

        // negation
        assertFalse(posn0.equals(posn1));
    }

    @Test
    public void testHashCode() {
        // reflexivity
        assertEquals(posn0.hashCode(), posn0.hashCode());

        // symmetry
        assertTrue(posn0.hashCode() == posn0A.hashCode() && posn0A.hashCode() == posn0.hashCode());

        // transitivity
        assertTrue(
                posn0.hashCode() == posn0A.hashCode()
                        && posn0A.hashCode() == new Posn(0, 0).hashCode()
                        && posn0.hashCode() == new Posn(0, 0).hashCode());

        // negation
        assertFalse(posn0.hashCode() == posn1.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(posn0.toString(), "{X: 0, Y: 0}");
    }
}

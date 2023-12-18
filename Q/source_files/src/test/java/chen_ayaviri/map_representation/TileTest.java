package chen_ayaviri.map_representation;

import chen_ayaviri.map_representation.shapes.Star;
import chen_ayaviri.map_representation.shapes.Square;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TileTest {
    private Tile redStar;
    private Tile blueSquare;
    private Tile redStar2;

    @Before
    public void setUp() {
        redStar = new Tile("red", "star");
        blueSquare = new Tile("blue", "square");
        redStar2 = new Tile("red", "star");
    }

    @Test
    public void testGetColor() {
        assertEquals(Tile.QColor.RED, redStar.getColor());
        assertEquals(Tile.QColor.BLUE, blueSquare.getColor());
    }

    @Test
    public void testGetShape() {
        assertEquals(new Star(), redStar.getShape());
        assertEquals(new Square(), blueSquare.getShape());
    }

    @Test
    public void testMatchesColor() {
        assertTrue(redStar.matchesColor(new Tile("red", "circle")));
        assertFalse(redStar.matchesColor(blueSquare));
    }

    @Test
    public void testMatchesShape() {
        assertTrue(blueSquare.matchesShape(new Tile("red", "square")));
        assertFalse(redStar.matchesColor(blueSquare));
    }

    @Test
    public void testEquals() {
        // reflexivity
        assertEquals(redStar, redStar);

        // symmetry
        assertTrue(redStar.equals(redStar2) && redStar2.equals(redStar));

        // transitivity
        assertTrue(
                redStar.equals(redStar2) &&
                        redStar2.equals(new Tile("red", "star")) &&
                        redStar.equals(new Tile("red", "star"))
        );

        // negation
        assertNotEquals(redStar, blueSquare);
    }

    @Test
    public void testHashCode() {
        // reflexivity
        assertEquals(redStar.hashCode(), redStar.hashCode());

        // symmetry
        assertTrue(redStar.hashCode() == redStar2.hashCode() && redStar2.hashCode() == redStar.hashCode());

        // transitivity
        assertTrue(
                redStar.hashCode() == redStar2.hashCode() &&
                        redStar2.hashCode() == new Tile("red", "star").hashCode() &&
                        redStar.hashCode() == new Tile("red", "star").hashCode()
        );

        // negation
        assertFalse(redStar.hashCode() == blueSquare.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("{shape: star, color: RED}", redStar.toString());
        assertEquals("{shape: square, color: BLUE}", blueSquare.toString());
    }
}

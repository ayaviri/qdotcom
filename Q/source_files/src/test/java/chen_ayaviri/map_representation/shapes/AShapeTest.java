package chen_ayaviri.map_representation.shapes;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AShapeTest {
    private AShape circle;
    private AShape eightStar;
    private AShape circle2;

    @Before
    public void setUp() {
        circle = new Circle();
        eightStar = new EightStar();
        circle2 = new Circle();
    }

    @Test
    public void testEquals() {
        // reflexivity
        assertEquals(circle, circle);

        // symmetry
        assertTrue(circle.equals(circle2) && circle2.equals(circle));

        // transitivity
        assertTrue(
                circle.equals(circle2) &&
                        circle2.equals(new Circle()) &&
                        new Circle().equals(circle)
        );

        // negation
        assertFalse(circle.equals(eightStar));
    }

    @Test
    public void testHashCode() {
        // reflexivity
        assertEquals(circle.hashCode(), circle.hashCode());

        // symmetry
        assertTrue(circle.hashCode() == circle2.hashCode() && circle2.hashCode() == circle.hashCode());

        // transitivity
        assertTrue(
                circle.hashCode() == circle2.hashCode() &&
                        circle2.hashCode() == new Circle().hashCode() &&
                        new Circle().hashCode() == circle.hashCode()
        );

        // negation
        assertFalse(circle.hashCode() == eightStar.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("circle", circle.toString());
        assertEquals("8star", eightStar.toString());
    }
}

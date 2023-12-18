package chen_ayaviri.common;

import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.map_representation.Placement;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class QMapTest {
    private Tile refereeTile;
    private QMap testMap;

    @Before
    public void setUp() {
        refereeTile = new Tile("red", "square");
        testMap = new QMap(refereeTile);
    }

    // constructor tests
    @Test
    public void testConstructorWithoutPos() {
        QMap map = new QMap(refereeTile);
        Optional<Tile> actualTile = map.getTileAt(new Posn(0, 0));
        assertTrue(actualTile.isPresent());
        assertEquals(refereeTile, actualTile.get());
    }

    // placeTile
    @Test
    public void testValidByPosPlaceTile() {
        Tile validTile = new Tile("red", "circle");
        Posn validPos = new Posn(0, 1);
        testMap.placeTile(new Placement(validTile, validPos));
        Optional<Tile> actualTile = testMap.getTileAt(validPos);
        assertTrue(actualTile.isPresent());
        assertEquals(new Tile("red", "circle"), actualTile.get());
    }

    // Ensures that an invalid tile can be placed in a valid Posn
    @Test
    public void testInvalidTileByPosPlaceTile() {
        Tile invalidTile = new Tile("blue", "circle");
        Posn validPos = new Posn(0, 1);
        testMap.placeTile(new Placement(invalidTile, validPos));
        Optional<Tile> actualTile = testMap.getTileAt(validPos);
        assertTrue(actualTile.isPresent());
        assertEquals(new Tile("blue", "circle"), actualTile.get());
    }

    // Invalid placement, not contiguous
    @Test
    public void testInvalidByPosPlaceTile() {
        Tile validTile = new Tile("red", "square");
        Posn invalidPos = new Posn(1, 1);
        try {
            testMap.placeTile(new Placement(validTile, invalidPos));
            fail("Missing exception");
        } catch (RuntimeException e) {
            assertEquals(String.format("Placement of tile %s at position %s is invalid", validTile, invalidPos), e.getMessage());
        }
    }

    // Invalid placement, overlapping existing tile
    @Test
    public void testInvalidByPosOverlapPlaceTile() {
        Tile validTile = new Tile("red", "circle");
        Posn invalidPos = new Posn(0, 0);
        try {
            testMap.placeTile(new Placement(validTile, invalidPos));
            fail("Missing exception");
        } catch (RuntimeException e) {
            assertEquals(String.format("Placement of tile %s at position %s is invalid", validTile, invalidPos), e.getMessage());
        }
    }

    // getValidTilePositions
    @Test
    public void testGetValidTilePositions1() {
        Set<Posn> testSet = new HashSet<>();
        testSet.add(new Posn(0, -2));
        testSet.add(new Posn(-1, -1));
        testSet.add(new Posn(1, -1));
        testSet.add(new Posn(-1, 1));
        testSet.add(new Posn(2, 1));
        testSet.add(new Posn(0, 2));
        testSet.add(new Posn(1, 2));

        testMap.placeTile(new Placement(new Tile("red", "8star"), new Posn(0,1)));
        testMap.placeTile(new Placement(new Tile("orange", "8star"), new Posn(1,1)));
        testMap.placeTile(new Placement(new Tile("blue", "square"), new Posn(0,-1)));

        assertEquals(testSet, testMap.getValidTilePositions(new Tile("blue", "8star")));
    }

    @Test
    public void testGetValidTilePositions2() {
        Set<Posn> testSet = new HashSet<>();
        testSet.add(new Posn(-1, 1));
        testSet.add(new Posn(1, -1));
        testSet.add(new Posn(1, 1));
        testSet.add(new Posn(1, 3));
        testSet.add(new Posn(3, 1));

        testMap.placeTile(new Placement(new Tile("blue", "square"), new Posn(0,1)));
        testMap.placeTile(new Placement(new Tile("red", "square"), new Posn(0,2)));
        testMap.placeTile(new Placement(new Tile("red", "diamond"), new Posn(1,0)));
        testMap.placeTile(new Placement(new Tile("red", "square"), new Posn(2,0)));
        testMap.placeTile(new Placement(new Tile("red", "diamond"), new Posn(1,2)));
        testMap.placeTile(new Placement(new Tile("red", "square"), new Posn(2,2)));
        testMap.placeTile(new Placement(new Tile("blue", "diamond"), new Posn(2,1)));

        assertEquals(testSet, testMap.getValidTilePositions(new Tile("blue", "diamond")));
    }



}

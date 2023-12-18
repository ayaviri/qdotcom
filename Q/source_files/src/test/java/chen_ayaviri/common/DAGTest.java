package chen_ayaviri.common;

import chen_ayaviri.common.GameState.ExchangeAction;
import chen_ayaviri.common.GameState.PassAction;
import chen_ayaviri.common.GameState.PlaceAction;
import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.strategy.ADAG;
import chen_ayaviri.strategy.DAG;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Posn;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DAGTest {
    private ADAG strategy;
    private String player;
    private QMap map;
    private Tile redStar;
    private Tile red8Star;
    private Tile redCircle;
    private Tile blueStar;
    private Tile blue8Star;
    private Tile blueSquare;
    private Tile blueCircle;
    private Tile blueDiamond;
    private Tile blueClover;
    private Tile purpleStar;
    private Tile purpleSquare;
    private Tile greenStar;
    private Tile greenCircle;
    private Tile yellowStar;
    private Tile yellowCircle;
    private Tile orangeStar;
    private Tile orange8Star;

    @Before
    public void setUp() {
        redStar = new Tile("red", "star");
        red8Star = new Tile("red", "8star");
        redCircle = new Tile("red", "circle");
        blueStar = new Tile("blue", "star");
        blue8Star = new Tile("blue", "8star");
        blueSquare = new Tile("blue", "square");
        blueCircle = new Tile("blue", "circle");
        blueDiamond = new Tile("blue", "diamond");
        blueClover = new Tile("blue", "clover");
        purpleStar = new Tile("purple", "star");
        purpleSquare = new Tile("purple", "square");
        greenStar = new Tile("green", "star");
        greenCircle = new Tile("green", "circle");
        yellowStar = new Tile("yellow", "star");
        yellowCircle = new Tile("yellow", "circle");
        orangeStar = new Tile("orange", "star");
        orange8Star = new Tile("orange", "8star");

        strategy = new DAG();
        player = "player";
        map = new QMap(redStar);
    }

    @Test
    public void testPass() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(purpleSquare);
        tiles.add(blueCircle);
        tiles.add(greenCircle);

        ActivePlayerInfo state = new ActivePlayerInfo(map, new PlayerState(null, "", tiles, 0), 2, new ArrayList<>());

        assertEquals(new PassAction(), strategy.computeTurnAction(state));
    }

    @Test
    public void testExchange() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(purpleSquare);
        tiles.add(blueCircle);

        ActivePlayerInfo state = new ActivePlayerInfo(map, new PlayerState(null, "", tiles, 0), 2, new ArrayList<>());

        assertEquals(new ExchangeAction(), strategy.computeTurnAction(state));
    }

    @Test
    public void testSimplePlacement() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(red8Star);
        tiles.add(redCircle);

        ActivePlayerInfo state = new ActivePlayerInfo(map, new PlayerState(null, "", tiles, 0), 2, new ArrayList<>());

        List<Placement> expectedPlacements = new ArrayList<Placement>(Arrays.asList(
                        new Placement(red8Star, new Posn(0, -1)),
                        new Placement(redCircle, new Posn(0, -2))
                    ));
        PlaceAction.Builder placeActionBuilder = new PlaceAction.Builder();
        for (Placement placement : expectedPlacements) {
            placeActionBuilder.addPlacement(placement);
        }

        assertEquals(placeActionBuilder.tryBuild().get(), strategy.computeIteratedTurnAction(state));
    }

    @Test
    public void testSkippedPlacement() {
        List<Tile> tiles = new ArrayList<>();
        map = new QMap(redCircle);
        tiles.add(yellowCircle);
        tiles.add(redStar);
        tiles.add(red8Star);

        ActivePlayerInfo state = new ActivePlayerInfo(map, new PlayerState(null, "", tiles, 0), 2, new ArrayList<>());

        List<Placement> expectedPlacements = new ArrayList<Placement>(Arrays.asList(
                new Placement(redStar, new Posn(0, -1)),
                new Placement(red8Star, new Posn(0, -2)),
                new Placement(yellowCircle, new Posn(-1, 0))
        ));
        PlaceAction.Builder placeActionBuilder = new PlaceAction.Builder();
        for (Placement placement : expectedPlacements) {
            placeActionBuilder.addPlacement(placement);
        }

        assertEquals(placeActionBuilder.tryBuild().get(), strategy.computeIteratedTurnAction(state));
    }

    @Test
    public void testMostNeighborsPlacement() {
        List<Tile> tiles = new ArrayList<>();
        map = new QMap(redStar);
        map.placeTile(new Placement(orangeStar, new Posn(1,0)));
        map.placeTile(new Placement(orange8Star, new Posn(1,1)));
        tiles.add(red8Star);

        ActivePlayerInfo state = new ActivePlayerInfo(map, new PlayerState(null, "", tiles, 0), 2, new ArrayList<>());

        List<Placement> expectedPlacements = new ArrayList<Placement>(Arrays.asList(
                new Placement(red8Star, new Posn(0, -1))
        ));
        PlaceAction.Builder placeActionBuilder = new PlaceAction.Builder();
        for (Placement placement : expectedPlacements) {
            placeActionBuilder.addPlacement(placement);
        }

        assertEquals(placeActionBuilder.tryBuild().get(), strategy.computeIteratedTurnAction(state));
    }
}

package chen_ayaviri.referee;

import chen_ayaviri.common.GameState;
import chen_ayaviri.common.QMap;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.player.LocalPlayer;
import chen_ayaviri.strategy.DAG;
import org.junit.Before;

import java.util.*;

public class ObserverTest {
    GameState gameState;
    Map<String, List<Tile>> playerTiles;
    List<IPlayer> players;
    QMap map;
    List<Tile> refereeTiles;
    String player1Name;
    String player2Name;
    String player3Name;

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
        // tiles
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

        player1Name = "player1";
        player2Name = "player2";
        player3Name = "player3";

        IPlayer player1 = new LocalPlayer(player1Name, new DAG());
        IPlayer player2 = new LocalPlayer(player2Name, new DAG());
        IPlayer player3 = new LocalPlayer(player3Name, new DAG());

        playerTiles = new HashMap<String, List<Tile>>();
        playerTiles.put(player2Name, new ArrayList<Tile>(Arrays.asList(redStar, blue8Star, purpleSquare, redCircle)));
        playerTiles.put(player3Name, new ArrayList<Tile>(Arrays.asList(yellowStar, redCircle, redStar)));
        playerTiles.put(player1Name, new ArrayList<Tile>(Arrays.asList(purpleSquare, orange8Star)));

        players = new ArrayList<>();
        players.add(player2);
        players.add(player3);
        players.add(player1);

        refereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare, red8Star, blueStar));

        map = new QMap(redStar);
        map.placeTile(new Placement(red8Star, new Posn(0, 1)));
        map.placeTile(new Placement(blueStar, new Posn(0, -1)));
        map.placeTile(new Placement(orange8Star, new Posn(1, 1)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);
    }
}

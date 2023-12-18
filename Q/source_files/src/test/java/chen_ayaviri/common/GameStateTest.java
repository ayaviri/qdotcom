package chen_ayaviri.common;

import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.player.LocalPlayer;
import chen_ayaviri.strategy.DAG;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameStateTest {
    GameState gameState;
    Map<String, List<Tile>> playerTiles;
    List<IPlayer> players;
    QMap map;
    List<Tile> refereeTiles;
    String player1;
    String player2;
    String player3;

    // tiles
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

        player1 = "player1";
        player2 = "player2";
        player3 = "player3";

        playerTiles = new HashMap<String, List<Tile>>();
        playerTiles.put(player2, new ArrayList<Tile>(Arrays.asList(redStar, blue8Star, purpleSquare, redCircle)));
        playerTiles.put(player3, new ArrayList<Tile>(Arrays.asList(yellowStar, redCircle, redStar)));
        playerTiles.put(player1, new ArrayList<Tile>(Arrays.asList(purpleSquare, orange8Star)));

        players = new ArrayList<IPlayer>();
        players.add(new LocalPlayer(player2, new DAG()));
        players.add(new LocalPlayer(player3, new DAG()));
        players.add(new LocalPlayer(player1, new DAG()));

        refereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare, red8Star, blueStar));

        map = new QMap(redStar);
        map.placeTile(new Placement(red8Star, new Posn(0, 1)));
        map.placeTile(new Placement(blueStar, new Posn(0, -1)));
        map.placeTile(new Placement(orange8Star, new Posn(1, 1)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);
    }

    @Test
    public void testGetMap() {
        QMap testMap = new QMap(redStar);
        testMap.placeTile(new Placement(red8Star, new Posn(0, 1)));
        testMap.placeTile(new Placement(blueStar, new Posn(0, -1)));
        testMap.placeTile(new Placement(orange8Star, new Posn(1, 1)));

        ActivePlayerInfo gameStateInfo = gameState.getInfoForActivePlayer();
        assertEquals(testMap.getTiles(), gameStateInfo.getMap().getTiles());
    }

    @Test
    public void testGetActivePlayerHand() {
        ActivePlayerInfo gameStateInfo = gameState.getInfoForActivePlayer();
        assertEquals(new ArrayList<Tile>(Arrays.asList(redStar, blue8Star, purpleSquare, redCircle)), gameStateInfo.getTiles());
    }

    @Test
    public void testGetRefereeTiles() {
        assertEquals(new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare, red8Star, blueStar)), gameState.getRefereeTiles());
    }

    @Test
    public void testPassTurn() {
        TurnAction passAction = new GameState.PassAction();
        assertEquals(this.player2, gameState.getActivePlayerState().getName());
        gameState.performCheckedTurnAction(passAction);
        assertEquals(this.player3, gameState.getActivePlayerState().getName());
        gameState.performCheckedTurnAction(passAction);
        assertEquals(this.player1, gameState.getActivePlayerState().getName());
        gameState.performCheckedTurnAction(passAction);
        assertEquals(this.player2, gameState.getActivePlayerState().getName());
    }

    // exchangeActivePlayerTiles, refereeTiles > activePlayerHand
    @Test
    public void testExchangeTurnValid() {
        assertTrue(gameState.checkLegalityOf(new GameState.ExchangeAction()));
        gameState.performCheckedTurnAction(new GameState.ExchangeAction());
        assertEquals(new ArrayList<Tile>(Arrays.asList(blueStar, redStar, blue8Star, purpleSquare, redCircle)), gameState.getRefereeTiles());
        // TODO: figure out how to assert that the player has gotten their tiles
        //assertEquals(new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare, red8Star)), prevPlayer.getTiles());
    }

    // exchangeActivePlayerTiles, refereeTiles < activePlayerHand
    @Test
    public void testExchangeTurnInvalid() {
        refereeTiles.remove(0);
        refereeTiles.remove(1);
        gameState = new GameState(players, playerTiles, map, refereeTiles);

        assertFalse(gameState.checkLegalityOf(new GameState.ExchangeAction()));
    }

    @Test
    public void testPlaceTurnValid1() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2, 1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        assertTrue(gameState.checkLegalityOf(placeAction));
        gameState.performCheckedTurnAction(placeAction);
        ActivePlayerInfo gameStateInfo = gameState.getInfoForActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(new ArrayList<Tile>(Arrays.asList(orange8Star, purpleSquare, red8Star, blueStar)), gameState.getRefereeTiles());

        QMap testMap = new QMap(redStar);
        testMap.placeTile(new Placement(red8Star, new Posn(0, 1)));
        testMap.placeTile(new Placement(blueStar, new Posn(0, -1)));
        testMap.placeTile(new Placement(orange8Star, new Posn(1, 1)));
        testMap.placeTile(new Placement(blue8Star, new Posn(2, 1)));
        assertEquals(testMap.getTiles(), gameStateInfo.getMap().getTiles());

        //assertEquals(new ArrayList<Tile>(Arrays.asList(redStar, purpleSquare, redCircle, greenCircle)), prevPlayer.getTiles());
    }
    @Test
    public void testPlaceTurnValid2() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(redCircle, new Posn(-1,1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        assertTrue(gameState.checkLegalityOf(placeAction));
        gameState.performCheckedTurnAction(placeAction);
        ActivePlayerInfo gameStateInfo = gameState.getInfoForActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(new ArrayList<Tile>(Arrays.asList(purpleSquare, red8Star, blueStar)), gameState.getRefereeTiles());

        QMap testMap = new QMap(redStar);
        testMap.placeTile(new Placement(red8Star, new Posn(0,1)));
        testMap.placeTile(new Placement(blueStar, new Posn(0,-1)));
        testMap.placeTile(new Placement(orange8Star, new Posn(1,1)));
        testMap.placeTile(new Placement(blue8Star, new Posn(2,1)));
        testMap.placeTile(new Placement(redCircle, new Posn(-1,1)));
        assertEquals(testMap.getTiles(), gameStateInfo.getMap().getTiles());

        //assertEquals(new ArrayList<Tile>(Arrays.asList(redStar, purpleSquare, greenCircle, orange8Star)), prevPlayer.getTiles());
    }

    @Test
    public void testPlaceTurnValid3() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(redCircle, new Posn(-1,1)));
        testPlacement.add(new Placement(redStar, new Posn(-2,1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        assertTrue(gameState.checkLegalityOf(placeAction));
        gameState.performCheckedTurnAction(placeAction);
        ActivePlayerInfo gameStateInfo = gameState.getInfoForActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(new ArrayList<Tile>(Arrays.asList(red8Star, blueStar)), gameState.getRefereeTiles());

        QMap testMap = new QMap(redStar);
        testMap.placeTile(new Placement(red8Star, new Posn(0,1)));
        testMap.placeTile(new Placement(blueStar, new Posn(0,-1)));
        testMap.placeTile(new Placement(orange8Star, new Posn(1,1)));
        testMap.placeTile(new Placement(blue8Star, new Posn(2,1)));
        testMap.placeTile(new Placement(redCircle, new Posn(-1,1)));
        testMap.placeTile(new Placement(redStar, new Posn(-2,1)));
        assertEquals(testMap.getTiles(), gameStateInfo.getMap().getTiles());

        //assertEquals(new ArrayList<Tile>(Arrays.asList(purpleSquare, greenCircle, orange8Star, purpleSquare)), prevPlayer.getTiles());
    }


    @Test
    public void testPlaceTurnInvalidNoMatch() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(1,1)));
        testPlacement.add(new Placement(redCircle, new Posn(1,-1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        assertFalse(gameState.checkLegalityOf(placeAction));

        gameState.removeActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(2, gameState.getNumberOfPlayers());

        List<Tile> testRefereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare,
                                                                        red8Star, blueStar, redStar, blue8Star,
                                                                        purpleSquare, redCircle));
        assertEquals(testRefereeTiles, gameState.getRefereeTiles());
    }

    /*
    As of 7, changed PlaceAction to only accept sequence of placements in same row or column

    @Test
    public void testPlaceTurnInvalidNotInline1() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(redCircle, new Posn(-1,0)));
        TurnAction placeAction = new GameState.PlaceAction(testPlacement);

        assertFalse(gameState.checkLegalityOf(placeAction));

        gameState.removeActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(2, gameState.getNumberOfPlayers());

        List<Tile> testRefereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare,
                                                                        red8Star, blueStar, redStar, blue8Star,
                                                                        purpleSquare, redCircle));
        assertEquals(testRefereeTiles, gameState.getRefereeTiles());
    }

    @Test
    public void testPlaceTurnInvalidNotInline2() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(redCircle, new Posn(-1,1)));
        testPlacement.add(new Placement(redStar, new Posn(-1,0)));
        TurnAction placeAction = new GameState.PlaceAction(testPlacement);

        assertFalse(gameState.checkLegalityOf(placeAction));

        gameState.removeActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(2, gameState.getNumberOfPlayers());

        List<Tile> testRefereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare,
                                                                        red8Star, blueStar, redStar, blue8Star,
                                                                        purpleSquare, redCircle));
        assertEquals(testRefereeTiles, gameState.getRefereeTiles());
    }

    @Test
    public void testPlaceTurnInvalidNotContiguous() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(redCircle, new Posn(3,5)));
        TurnAction placeAction = new GameState.PlaceAction(testPlacement);

        assertFalse(gameState.checkLegalityOf(placeAction));

        gameState.removeActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(2, gameState.getNumberOfPlayers());

        List<Tile> testRefereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare,
                                                                        red8Star, blueStar, redStar, blue8Star,
                                                                        purpleSquare, redCircle));
        assertEquals(testRefereeTiles, gameState.getRefereeTiles());
    }
     */

    @Test
    public void testPlaceTurnInvalidNotInHand() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(1,2)));
        testPlacement.add(new Placement(yellowStar, new Posn(1,0)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        assertFalse(gameState.checkLegalityOf(placeAction));

        gameState.removeActivePlayer();
        assertEquals(player3, gameState.getActivePlayerState().getName());
        assertEquals(2, gameState.getNumberOfPlayers());

        List<Tile> testRefereeTiles = new ArrayList<Tile>(Arrays.asList(greenCircle, orange8Star, purpleSquare,
                                                                        red8Star, blueStar, redStar, blue8Star,
                                                                        purpleSquare, redCircle));
        assertEquals(testRefereeTiles, gameState.getRefereeTiles());
    }

    // scorePlacement

    // trivial case, placing 1 tile
    @Test
    public void testScoreTrivial() {
        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 4);
        assertEquals(4, score); // placed 1 + 3 contiguous tiles
    }

    // place all tiles in hand bonus
    @Test
    public void testScorePlaceAllInHand() {
        map = new QMap(redStar);
        map.placeTile(new Placement(red8Star, new Posn(0,1)));
        map.placeTile(new Placement(blueStar, new Posn(0,-1)));
        map.placeTile(new Placement(orange8Star, new Posn(1,1)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(blueStar, new Posn(3,1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 2);
        assertEquals(6 + gameState.END_OF_GAME_BONUS, score); // placed 2 + 4 contiguous tiles + end bonus
    }

    // place 2 tiles in same contiguous sequence (test for double counting)
    @Test
    public void testScoreSameSequence() {
        map = new QMap(redStar);
        map.placeTile(new Placement(red8Star, new Posn(0,1)));
        map.placeTile(new Placement(blueStar, new Posn(0,-1)));
        map.placeTile(new Placement(orange8Star, new Posn(1,1)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar, yellowStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(blueStar, new Posn(3,1)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 3);
        assertEquals(6, score); // placed 2 + 4 contiguous tiles
    }

    // place 2 tiles in different contiguous sequence
    /*
        BST
        RST     bst
        R8S O8S b8s <- does this get double-counted?
     */
    @Test
    public void testScoreDifferentSequence() {
        map = new QMap(redStar);
        map.placeTile(new Placement(red8Star, new Posn(0,1)));
        map.placeTile(new Placement(blueStar, new Posn(0,-1)));
        map.placeTile(new Placement(orange8Star, new Posn(1,1)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar, yellowStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blue8Star, new Posn(2,1)));
        testPlacement.add(new Placement(blueStar, new Posn(2,0)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 3);
        assertEquals(7, score); // placed 2 + 3 contiguous row + 2 contiguous column (corner is double-counted)
    }

    // place 1 tile to complete 1 Q by common shape
    @Test
    public void testScoreQPlace1x1Shape() {
        map = new QMap(redStar);
        map.placeTile(new Placement(orangeStar, new Posn(0,1)));
        map.placeTile(new Placement(greenStar, new Posn(0,2)));
        map.placeTile(new Placement(purpleStar, new Posn(0,3)));
        map.placeTile(new Placement(yellowStar, new Posn(0,4)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar, yellowStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blueStar, new Posn(0,5)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 3);
        assertEquals(7 + gameState.Q_BONUS, score); // placed 1 + 6 contiguous + bonus Q
    }

    // place 1 tile to complete 1 Q by common color
    @Test
    public void testScoreQPlace1x1Color() {
        map = new QMap(blueClover);
        map.placeTile(new Placement(blueCircle, new Posn(1, 0)));
        map.placeTile(new Placement(blueSquare, new Posn(2, 0)));
        map.placeTile(new Placement(blueDiamond, new Posn(3, 0)));
        map.placeTile(new Placement(blue8Star, new Posn(4, 0)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar, yellowStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blueStar, new Posn(5,0)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 3);
        assertEquals(7 + gameState.Q_BONUS, score); // placed 1 + 6 contiguous + 6 bonus Q
    }

    // place 2 tiles to complete 1 Q by common shape
    @Test
    public void testScoreQPlace2x1() {
        map = new QMap(redStar);
        map.placeTile(new Placement(orangeStar, new Posn(0,1)));
        map.placeTile(new Placement(greenStar, new Posn(0,2)));
        map.placeTile(new Placement(purpleStar, new Posn(0,3)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar, yellowStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(yellowStar, new Posn(0,4)));
        testPlacement.add(new Placement(blueStar, new Posn(0,5)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 3);
        assertEquals(8 + gameState.Q_BONUS, score); // placed 2 + 6 contiguous + bonus Q
    }

    // place 1 tile to complete 2 Q, one by shape and one by color
    /*
            RST
            OST
            GST
            PST
            YST YCI
       ? -> bst BCI BSQ BDI B8S BCL
     */
    @Test
    public void testScoreQPlace1x2() {
        map = new QMap(redStar);
        map.placeTile(new Placement(orangeStar, new Posn(0,1)));
        map.placeTile(new Placement(greenStar, new Posn(0,2)));
        map.placeTile(new Placement(purpleStar, new Posn(0,3)));
        map.placeTile(new Placement(yellowStar, new Posn(0,4)));
        map.placeTile(new Placement(yellowCircle, new Posn(1, 4)));
        map.placeTile(new Placement(blueCircle, new Posn(1, 5)));
        map.placeTile(new Placement(blueSquare, new Posn(2, 5)));
        map.placeTile(new Placement(blueDiamond, new Posn(3, 5)));
        map.placeTile(new Placement(blue8Star, new Posn(4, 5)));
        map.placeTile(new Placement(blueClover, new Posn(5, 5)));

        playerTiles.put(player2, new ArrayList<>(Arrays.asList(blue8Star, blueStar, yellowStar)));

        gameState = new GameState(players, playerTiles, map, refereeTiles);

        List<Placement> testPlacement = new ArrayList<>();
        testPlacement.add(new Placement(blueStar, new Posn(0,5)));
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        for (Placement placement : testPlacement) {
            placeActionBuilder.addPlacement(placement);
        }
        TurnAction placeAction = placeActionBuilder.tryBuild().get();

        gameState.performCheckedTurnAction(placeAction);

        int score = gameState.scorePlacement(testPlacement, 3);
        assertEquals(13 + 2 * gameState.Q_BONUS, score); // placed 1 + 12 contiguous + 12 bonus Q (double-counted placed tile)
    }
}

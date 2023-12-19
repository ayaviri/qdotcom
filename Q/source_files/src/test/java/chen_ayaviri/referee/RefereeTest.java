package chen_ayaviri.referee;

import chen_ayaviri.common.GameState;
import chen_ayaviri.common.QMap;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.player.*;
import chen_ayaviri.player.cheaters.*;
import chen_ayaviri.player.communication_failures.NewTilesCommFailurePlayer;
import chen_ayaviri.player.communication_failures.SetupCommFailurePlayer;
import chen_ayaviri.player.communication_failures.TakeTurnCommFailurePlayer;
import chen_ayaviri.player.communication_failures.WinCommFailurePlayer;
import chen_ayaviri.strategy.DAG;
import chen_ayaviri.strategy.ExchangeStrategy;
import chen_ayaviri.strategy.LDASG;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RefereeTest {
    GameState gameState;
    Map<String, List<Tile>> playerTiles;
    List<IPlayer> players;
    QMap map;

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
        playerTiles = new HashMap<>();
        players = new ArrayList<>();

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

        map = new QMap(redStar);
    }

    @Test
    public void testSetupCommFailure() {
        IPlayer exnPlayer = new SetupCommFailurePlayer("eliminated", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(blueStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testTakeTurnCommFailure() {
        IPlayer exnPlayer = new TakeTurnCommFailurePlayer("eliminated", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(blueStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testNewTilesCommFailurePlayer() {
        IPlayer exnPlayer = new NewTilesCommFailurePlayer("eliminated", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(orangeStar)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(blueStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(yellowCircle)));

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testWinCommFailurePlayerLost() {
        IPlayer exnPlayer = new WinCommFailurePlayer("eliminated", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(blueStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testWinCommFailurePlayerWon() {
        IPlayer exnPlayer = new WinCommFailurePlayer("eliminated", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(blueStar)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testWinCommFailurePlayerWon2() {
        IPlayer exnPlayer = new WinCommFailurePlayer("eliminated", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(blueStar, yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(orangeStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testPlayerBreaksRules() {
        IPlayer exnPlayer = new LocalPlayer("eliminated", new ExchangeStrategy());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(exnPlayer);
        players.add(player1);
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(blueStar, yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(orangeStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testAllCommFailurePlayers() {
        IPlayer exnPlayer1 = new SetupCommFailurePlayer("eliminated1", new DAG());
        IPlayer exnPlayer2 = new TakeTurnCommFailurePlayer("eliminated2", new DAG());
        players.add(exnPlayer1);
        players.add(exnPlayer2);
        playerTiles.put("eliminated1", new ArrayList<>(Arrays.asList(blueStar, yellowCircle)));
        playerTiles.put("eliminated2", new ArrayList<>(Arrays.asList(orangeStar)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated1", "eliminated2")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testEndByAllPass() {
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        IPlayer player2 = new LocalPlayer("player2", new LDASG());
        players.add(player1);
        players.add(player2);
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(blueStar, yellowCircle)));
        playerTiles.put("player2", new ArrayList<>(Arrays.asList(orangeStar, greenCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>());

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player2")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList()), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testPlaceCommFailureContinues() {
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        IPlayer player2 = new NewTilesCommFailurePlayer("eliminated", new LDASG());
        IPlayer player3 = new LocalPlayer("player3", new LDASG());
        players.add(player1);
        players.add(player2);
        players.add(player3);
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(yellowCircle, greenCircle, blueSquare, purpleSquare)));
        playerTiles.put("eliminated", new ArrayList<>(Arrays.asList(orangeStar, greenCircle)));
        playerTiles.put("player3", new ArrayList<>(Arrays.asList(purpleSquare)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blue8Star, greenStar)));

        /*
            ref tiles: blue8Star, greenStar

            round 1:
            player1 - pass
            player2 - place, receive blue8Star, new tiles exn
            player3 - exchange, receive greenStar

            ref tiles: [empty]

            round 2:
            player1 - pass
            player3 - place, end game, win
         */

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player3")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("eliminated")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testEndByAllExchange() {
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        IPlayer player2 = new LocalPlayer("player2", new LDASG());
        players.add(player1);
        players.add(player2);
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        playerTiles.put("player2", new ArrayList<>(Arrays.asList(orange8Star, greenCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1", "player2")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testCheatingPlayer() {
        NonAdjacentCoordinatePlayer p = new NonAdjacentCoordinatePlayer("Joshua");
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(p);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(orange8Star, greenCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testTileNotOwnedPlayer() {
        TileNotOwnedPlayer p = new TileNotOwnedPlayer("Joshua", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(p);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(orange8Star, greenCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua")), gameResult.getEliminatedPlayers());
    }
    @Test
    public void testNotALinePlayer() {
        NotALinePlayer p = new NotALinePlayer("Joshua", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(p);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(blueStar, yellowStar)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testBadAskForTile() {
        BadAskForTilePlayer p = new BadAskForTilePlayer("Joshua", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(p);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(orange8Star, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testNoFit() {
        NoFitPlayer p = new NoFitPlayer("Joshua", new DAG());
        IPlayer player1 = new LocalPlayer("player1", new DAG());
        players.add(p);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testNoFitAndBadAsk() {
        NoFitPlayer p = new NoFitPlayer("Joshua", new DAG());
        BadAskForTilePlayer player1 = new BadAskForTilePlayer("player1", new DAG());
        players.add(p);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testNoFitAndNoFitAndBadAsk() {
        NoFitPlayer p = new NoFitPlayer("Joshua", new DAG());
        NoFitPlayer k = new NoFitPlayer("k", new DAG());
        BadAskForTilePlayer player1 = new BadAskForTilePlayer("player1", new DAG());
        players.add(p);
        players.add(k);
        players.add(player1);
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("k", new ArrayList<>(Arrays.asList(redStar, greenCircle, yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua", "k")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testDAGAndNoFitAndNoFitAndBadAsk() {
        IPlayer goodplayer = new LocalPlayer("goodplayer", new DAG());
        NoFitPlayer p = new NoFitPlayer("Joshua", new DAG());
        NoFitPlayer k = new NoFitPlayer("k", new DAG());
        BadAskForTilePlayer player1 = new BadAskForTilePlayer("player1", new DAG());
        players.add(goodplayer);
        players.add(p);
        players.add(k);
        players.add(player1);
        playerTiles.put("goodplayer", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("k", new ArrayList<>(Arrays.asList(redStar, greenCircle, yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));


        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("goodplayer")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("Joshua", "k")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testDAGAndTakeTurnCommFailureAndNoFitAndNoFitAndBadAsk() {
        IPlayer goodplayer = new LocalPlayer("goodplayer", new DAG());
        TakeTurnCommFailurePlayer ttexn = new TakeTurnCommFailurePlayer("ttexn", new DAG());
        NoFitPlayer p = new NoFitPlayer("Joshua", new DAG());
        NoFitPlayer k = new NoFitPlayer("k", new DAG());
        BadAskForTilePlayer player1 = new BadAskForTilePlayer("player1", new DAG());
        players.add(goodplayer);
        players.add(ttexn);
        players.add(p);
        players.add(k);
        players.add(player1);
        playerTiles.put("goodplayer", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("ttexn", new ArrayList<>(Arrays.asList(redStar)));
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("k", new ArrayList<>(Arrays.asList(redStar, greenCircle, yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("player1")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("ttexn","Joshua", "k")), gameResult.getEliminatedPlayers());
    }

    @Test
    public void testDAGAndTakeTurnCommFailureAndNoFitAndNoFitAndBadAskButLDASG() {
        IPlayer goodplayer = new LocalPlayer("goodplayer", new DAG());
        TakeTurnCommFailurePlayer ttexn = new TakeTurnCommFailurePlayer("ttexn", new DAG());
        NoFitPlayer p = new NoFitPlayer("Joshua", new DAG());
        NoFitPlayer nofitp = new NoFitPlayer("nofitp", new DAG());
        BadAskForTilePlayer player1 = new BadAskForTilePlayer("player1", new DAG());
        IPlayer mcldasg = new LocalPlayer("mcldasg", new LDASG());
        players.add(goodplayer);
        players.add(ttexn);
        players.add(p);
        players.add(nofitp);
        players.add(player1);
        players.add(mcldasg);
        playerTiles.put("goodplayer", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("ttexn", new ArrayList<>(Arrays.asList(redStar)));
        playerTiles.put("Joshua", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, greenCircle, greenCircle)));
        playerTiles.put("nofitp", new ArrayList<>(Arrays.asList(redStar, greenCircle, yellowCircle)));
        playerTiles.put("player1", new ArrayList<>(Arrays.asList(greenCircle, yellowCircle)));
        playerTiles.put("mcldasg", new ArrayList<>(Arrays.asList(blueSquare, redStar, greenCircle, redStar, greenCircle, yellowCircle)));
        gameState = new GameState(players, playerTiles, map, new ArrayList<>(Arrays.asList(blueSquare, purpleSquare)));

        Referee referee = new Referee(gameState);

        GameResult gameResult = referee.playToCompletion();

        assertEquals(new ArrayList<>(Arrays.asList("mcldasg")), gameResult.getWinningPlayers());
        assertEquals(new ArrayList<>(Arrays.asList("ttexn","Joshua", "nofitp")), gameResult.getEliminatedPlayers());
    }
}

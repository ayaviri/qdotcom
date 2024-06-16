package ayaviri.common;

import static org.junit.Assert.*;

import ayaviri.map_representation.Tile;
import ayaviri.player.LocalPlayer;
import ayaviri.strategy.DAG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class PlayerStateTest {
    private PlayerState playerState1;

    @Before
    public void setUp() {
        List<Tile> player1Tiles = new ArrayList<>();
        player1Tiles.add(new Tile("red", "star"));
        player1Tiles.add(new Tile("orange", "8star"));
        player1Tiles.add(new Tile("yellow", "diamond"));

        playerState1 =
                new PlayerState(new LocalPlayer("player1", new DAG()), "player1", player1Tiles);
    }

    @Test
    public void testHasAllTiles() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile("red", "star"));
        tiles.add(new Tile("yellow", "diamond"));

        assertTrue(playerState1.hasAllTiles(tiles));

        tiles.add(new Tile("green", "square"));

        assertFalse(playerState1.hasAllTiles(tiles));
    }

    @Test
    public void testExchangeTiles() {
        List<Tile> returned = new ArrayList<>();
        returned.add(new Tile("red", "star"));
        returned.add(new Tile("orange", "8star"));
        returned.add(new Tile("yellow", "diamond"));

        List<Tile> exchange = new ArrayList<>();
        exchange.add(new Tile("green", "square"));
        exchange.add(new Tile("blue", "circle"));
        exchange.add(new Tile("purple", "clover"));

        assertEquals(playerState1.exchangeTiles(exchange), returned);
        assertEquals(playerState1.getTiles(), exchange);
    }

    @Test
    public void testRemoveTile() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile("red", "star"));
        tiles.add(new Tile("yellow", "diamond"));

        playerState1.removeTiles(new ArrayList<>(Arrays.asList(new Tile("orange", "8star"))));

        assertEquals(playerState1.getTiles(), tiles);
    }

    @Test
    public void testAddTile() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile("red", "star"));
        tiles.add(new Tile("orange", "8star"));
        tiles.add(new Tile("yellow", "diamond"));
        tiles.add(new Tile("yellow", "diamond"));

        playerState1.addTiles(new ArrayList<>(Arrays.asList(new Tile("yellow", "diamond"))));

        assertEquals(playerState1.getTiles(), tiles);
    }
}

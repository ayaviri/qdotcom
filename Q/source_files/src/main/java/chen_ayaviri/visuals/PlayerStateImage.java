package chen_ayaviri.visuals;

import chen_ayaviri.map_representation.Tile;

import java.util.List;
import java.util.Optional;

//TODO: this ideally shouldn't be optionals

/**
 * Information to produce images involving Player information
 */
public class PlayerStateImage {
    private final String id;
    private final int score;
    private final List<Optional<Tile>> tiles;

    /**
     * Information to produce images involving Player information
     * @param id the player's id
     * @param score the player's score
     * @param tiles the tiles of the player
     */
    public PlayerStateImage(String id, int score, List<Optional<Tile>> tiles) {
        this.id = id;
        this.score = score;
        this.tiles = tiles;
    }

    public String id() {
        return this.id;
    }

    public int score() {
        return this.score;
    }

    public List<Optional<Tile>> tiles() {
        return this.tiles;
    }
}

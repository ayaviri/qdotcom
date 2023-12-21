package chen_ayaviri.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Tiles;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.visuals.PlayerStateImage;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

// An immutable representation of the knowledge on a player in the game
// TODO: see if it is necessary parameterize class by the communication object type
public class PlayerState {
    private final IPlayer proxy;
    private final String name;
    private int score;
    private List<Tile> tiles;

    public PlayerState(IPlayer proxy, String playerName, List<Tile> tiles) {
        if (tiles.isEmpty()) {
            throw new IllegalArgumentException("Cannot have an empty list of tiles");
        }

        this.proxy = proxy;
        this.name = playerName;
        this.tiles = new ArrayList<Tile>(tiles);
        this.score = 0;
    }

    // Creates a copy of the given PlayerState with the same proxy
    protected PlayerState(PlayerState playerState) {
        this.name = playerState.name;
        this.score = playerState.score;
        this.tiles = new ArrayList<Tile>(playerState.tiles);
        this.proxy = playerState.proxy;
    }

    // Creates a copy of the given PlayerState with the given proxy
    protected PlayerState(PlayerState playerState, IPlayer proxy) {
        this.name = playerState.name;
        this.score = playerState.score;
        this.tiles = new ArrayList<Tile>(playerState.tiles);
        this.proxy = proxy;
    }

    // TODO: what is this constructor needed for ? can we get rid of it ?
    protected PlayerState(IPlayer proxy, String name, List<Tile> tiles, int score) {
        this.proxy = proxy;
        this.name = name;
        this.tiles = new ArrayList<Tile>(tiles);
        this.score = score;
    }

    public PlayerStateImage visualize() {
        List<Optional<Tile>> tiles = new ArrayList<>();

        for (Tile tile : this.tiles) {
            tiles.add(Optional.of(tile));
        }

        return new PlayerStateImage(
                this.name,
                this.score,
                tiles
            );
    }

    // Constructs a player state from json input that includes score and tiles
    public static PlayerState fromJson(JsonObject jPlayer, JsonArray jActorSpec) {
        String name = jPlayer.get("name").getAsString();
        int score = jPlayer.get("score").getAsInt();
        List<Tile> tiles = Tiles.fromJson(jPlayer.get("tile*").getAsJsonArray());

        return new PlayerState(IPlayer.fromJson(jActorSpec), name, tiles, score);
    }

    // TODO: abstract with the method above
    // Constructs a PROXYLESS player state from a jPlayer
    public static PlayerState fromJson(JsonObject jPlayer) {
        String name = jPlayer.get("name").getAsString();
        int score = jPlayer.get("score").getAsInt();
        List<Tile> tiles = Tiles.fromJson(jPlayer.get("tile*").getAsJsonArray());

        return new PlayerState(null, name, tiles, score);
    }

    // Converts this player state to a JPlayer
    public JsonObject toJson() {
        JsonObject jPlayer = new JsonObject();
        jPlayer.add("score", new JsonPrimitive(this.score));
        jPlayer.add("name", new JsonPrimitive(this.name));
        jPlayer.add("tile*", Tiles.toJson(this.tiles));
        return jPlayer;
    }

    // Returns the reference to the client player, on which the referee will call methods from the player API
    public IPlayer getProxy() {
        return this.proxy;
    }

    public String getName() { return this.name; }

    // Returns a copy of this player's tiles
    public List<Tile> getTiles() {
        return new ArrayList<Tile>(this.tiles);
    }

    public int getScore() {
        return this.score;
    }

    protected void addToScore(int score) {
        this.score += score;
    }

    protected int getTileCount() {
        return this.tiles.size();
    }

    protected boolean hasAllTiles(List<Tile> tiles) {
        List<Tile> handCopy = new ArrayList<>(this.tiles);

        for (Tile tile : tiles) {
            boolean handHadTile = handCopy.remove(tile);

            if (!handHadTile) {
                return false;
            }
        }

        return true;
    }

    // Returns the list of tiles that were replaced
    protected List<Tile> exchangeTiles(List<Tile> receivedTiles) {
        List<Tile> oldTiles = this.getTiles();
        this.tiles = new ArrayList<Tile>(receivedTiles);

        return oldTiles;
    }

    // Removes the given list of tiles from this player's hand and returns the original hand
    // NOTE: Assumes that all elements of the given list are contained in this player's hand
    protected List<Tile> removeTiles(List<Tile> tiles) {
        List<Tile> oldTiles = new ArrayList<>(this.tiles);

        for (Tile tile : tiles) {
            this.tiles.remove(tile);
        }
        return oldTiles;
    }

    // Removes all of the tiles from this player's hand and returns the removed tiles
    protected List<Tile> removeAllTiles() {
        List<Tile> oldTiles = new ArrayList<>(this.tiles);
        this.tiles = new ArrayList<Tile>();

        return oldTiles;
    }

    protected void addTiles(List<Tile> tiles) {
        this.tiles.addAll(tiles);
    }

    public boolean equals(Object other) {
        return other instanceof PlayerState && this.name.equals(((PlayerState) other).name);
    }

    public int hashCode() {
        return Objects.hash(this.score, this.tiles, this.name);
    }
}

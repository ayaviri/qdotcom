package chen_ayaviri.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Tiles;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO: possibly redo this component to be parameterized by player, not necessarily the active one
// Represents the information sent to the currently active player at the START of their turn
public class ActivePlayerInfo {
    private final QMap map;
    // Represents the active player's tiles
    private final PlayerState player;
    private final int remainingRefereeTiles;
    // Represents the scores of all players in this game in order of turn taking, beginning with the active player
    private final List<Integer> playerScores;

    // The validity of these parameters is not checked here as this object's constructor is protected (only created by the server/game state)
    // TODO: should this constructor be public ? necessary for TileNotOwnedPlayer
    public ActivePlayerInfo(QMap map, PlayerState player, int remainingRefereeTiles, List<Integer> playerScores) {
        this.map = map;
        this.remainingRefereeTiles = remainingRefereeTiles;
        this.playerScores = playerScores;
        this.player = player;
    }

    // Creates a new ActivePlayerInfo object with a copy of each of the fields of the given original
    public ActivePlayerInfo(ActivePlayerInfo copy) {
        this.map = new QMap(copy.getMap());
        this.remainingRefereeTiles = copy.getRemainingRefereeTiles();
        this.playerScores = new ArrayList<Integer>(copy.getPlayerScores());
        this.player = new PlayerState(copy.player);
    }
   
    // TODO: What are the consequences of returning references of the map and hand here ?
    public QMap getMap() {
        return this.map;
    }

    public PlayerState getPlayerState() {
        return this.player;
    }
   
    // TODO: This needs to be deprecated as the tiles should be obtained through the player state
    public List<Tile> getTiles() {
        return this.player.getTiles();
    }

    public int getRemainingRefereeTiles() {
        return this.remainingRefereeTiles;
    }

    public List<Integer> getPlayerScores() {
        return this.playerScores;
    }

    public static ActivePlayerInfo fromJson(JsonObject jPub) {
        QMap map = QMap.fromJson(jPub.get("map").getAsJsonArray());
        List<Tile> tiles = Tiles.fromJson(
                jPub.get("players")
                        .getAsJsonArray()
                        .iterator()
                        .next()
                        .getAsJsonObject()
                        .get("tile*")
                        .getAsJsonArray()
        );
        String name = jPub.get("players")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("name")
                .getAsString();
        int remainingRefereeTiles = jPub.get("tile*").getAsInt();
        List<Integer> playerScores = new ArrayList<Integer>();
        Iterator<JsonElement> playerScoresIterator = jPub.get("players").getAsJsonArray().iterator();
        playerScores.add(playerScoresIterator.next().getAsJsonObject().get("score").getAsInt());
        while (playerScoresIterator.hasNext()) {
            playerScores.add(playerScoresIterator.next().getAsJsonPrimitive().getAsInt());
        }

        return new ActivePlayerInfo(map,
                new PlayerState(null, name, tiles, playerScores.get(0)),
                remainingRefereeTiles,
                playerScores);
    }

    public JsonObject toJson() {
        JsonObject jpub = new JsonObject();
        jpub.add("map", this.map.toJson());
        jpub.addProperty("tile*", this.remainingRefereeTiles);
        JsonArray players = new JsonArray();
        players.add(this.player.toJson());
        for (int i = 1; i < this.playerScores.size(); i++) {
            players.add(new JsonPrimitive(this.playerScores.get(i)));
        }
        jpub.add("players", players);
        return jpub;
    }
}

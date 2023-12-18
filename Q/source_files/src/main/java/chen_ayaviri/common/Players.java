package chen_ayaviri.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.visuals.PlayerStateImage;
import chen_ayaviri.visuals.PlayersImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Manages an ordered FIFO queue of player states in which the head of the queue is at the front
class Players {
    private final LinkedList<PlayerState> playerStates;

    // NOTE: Assumes that the playerTiles map has tiles for every player in the passed in list of players
    protected Players(List<IPlayer> players, Map<String, List<Tile>> playerTiles) {
        this.playerStates = new LinkedList<PlayerState>();

        for (IPlayer player : players) {
            String name = player.name();
            List<Tile> tiles = playerTiles.get(name);
            this.playerStates.add(new PlayerState(player, name, tiles, 0));
        }
    }

    // Creates a new Players object with a copy of each state in the given Players
    // object, injecting the correspondingly indexed proxy from the given list
    // NOTE: Assumes that the two lists are the same length and in the same order
    protected Players(Players players, List<IPlayer> playerProxies) {
        this.playerStates = new LinkedList<PlayerState>();

        for (int index = 0; index < players.playerStates.size(); index++) {
            PlayerState state = players.playerStates.get(index);
            IPlayer proxy = playerProxies.get(index);
            this.playerStates.add(new PlayerState(state, proxy));
        }
    }

    // Creates a new Players object with a copy of each state in the given Players object
    // NOTE: the original player proxies are kept
    protected Players(Players players) {
        this.playerStates = new LinkedList<PlayerState>();

        for (PlayerState playerState : players.playerStates) {
            this.playerStates.add(new PlayerState(playerState));
        }
    }

    // Used for constructing Players from Json
    protected Players(List<PlayerState> playerStates) {
        this.playerStates = new LinkedList<PlayerState>(playerStates);
    }

    public PlayersImage visualize() {
        List<PlayerStateImage> playerStateImages = new ArrayList<>();

        for (PlayerState playerState : this.playerStates) {
            playerStateImages.add(playerState.visualize());
        }

        return new PlayersImage(playerStateImages, 0);
    }

    // NOTE: relies on the assumption that both arrays are of the same length and in the
    // same order
    public static Players fromJson(JsonArray jPlayers, JsonArray jActorSpecs) {
        Iterator<JsonElement> jsonPlayers = jPlayers.iterator();
        Iterator<JsonElement> jsonActorSpecs = jActorSpecs.iterator();
        List<PlayerState> playerStates = new ArrayList<>();

        while (jsonPlayers.hasNext()) {
            PlayerState activePlayer = PlayerState.fromJson(
                jsonPlayers.next().getAsJsonObject(),
                jsonActorSpecs.next().getAsJsonArray()
            );
            playerStates.add(activePlayer);
        }

        return new Players(playerStates);
    }

    // Constructs a new Players object from an array of JPlayers WITHOUT PROXIES
    public static Players fromJson(JsonArray jPlayers) {
        Iterator<JsonElement> jsonPlayers = jPlayers.iterator();
        List<PlayerState> playerStates = new ArrayList<>();

        while (jsonPlayers.hasNext()) {
            PlayerState playerState = PlayerState.fromJson(jsonPlayers.next().getAsJsonObject());
            playerStates.add(playerState);
        }

        return new Players(playerStates);
    }

    // Converts this collection of players to a JsonArray of JPlayers
    public JsonArray toJson() {
        JsonArray players = new JsonArray();

        for (PlayerState playerState : this.playerStates) {
            players.add(playerState.toJson());
        }

        return players;
    }

    // Returns a copy of the list of player states in turn order
    protected List<PlayerState> getPlayerStates() {
        return new ArrayList<PlayerState>(this.playerStates);
    }

    // Returns a map from a player's proxy to their score (for convenience)
    protected Map<IPlayer, Integer> getScoreMap() {
        Map<IPlayer, Integer> playerScores = new HashMap<IPlayer, Integer>();

        for (PlayerState playerState : this.playerStates) {
            playerScores.put(playerState.getProxy(), playerState.getScore());
        }

        return playerScores;
    }

    // Returns a list of the players' scores (including the active player) in the order they take turns
    protected List<Integer> getScoreList() {
        List<Integer> scores = new ArrayList<Integer>();

        for (PlayerState playerState : this.playerStates) {
            scores.add(playerState.getScore());
        }

        return scores;
    }

    // Returns the order of the players as a list of their names, starting with the active player
    protected List<String> getOrdering() {
        List<String> playerOrdering = new ArrayList<String>();

        for (PlayerState playerState : this.playerStates) {
            playerOrdering.add(playerState.getName());
        }

        return playerOrdering;
    }

    // Returns a copy of the list of player proxies
    protected List<IPlayer> getPlayerProxies() {
        List<IPlayer> playerProxies = new ArrayList<IPlayer>();

        for (PlayerState playerState : this.playerStates) {
            playerProxies.add(playerState.getProxy());
        }

        return playerProxies;
    }

    // NOTE: updating active player and removing active player should not both be invoked when performing a turn
    // so there is assumed to be at least one player in the queue
    protected void updateActive() {
        this.playerStates.addLast(this.playerStates.removeFirst());
    }

    // Removes the player with the given name, returning their tiles
    // Throws an exception if there is no player with the given game
    protected List<Tile> removePlayer(String name) {
        for (int index = 0; index < this.playerStates.size(); index++) {
            PlayerState playerState = this.playerStates.get(index);
            if (playerState.getName().equals(name)) {
                this.playerStates.remove(index);
                return playerState.getTiles();
            }
        }

        throw new RuntimeException("There is no player with the given name");
    }

    protected PlayerState getActivePlayerState() {
        return this.playerStates.getFirst();
    }
}

package ayaviri.player;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.Tile;
import ayaviri.player.cheaters.*;
import ayaviri.player.communication_failures.NewTilesCommFailurePlayer;
import ayaviri.player.communication_failures.SetupCommFailurePlayer;
import ayaviri.player.communication_failures.TakeTurnCommFailurePlayer;
import ayaviri.player.communication_failures.WinCommFailurePlayer;
import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Iterator;
import java.util.List;

// Represents a player conforming to the player API
public interface IPlayer {
    // Returns the name of this player
    // Assumed to never fail
    String name();

    // Sets up this player with a copy of the ActivePlayerInfo and their hand of tiles
    void setup(ActivePlayerInfo currentState, List<Tile> tiles);

    // Sends the player their updated hand of tiles
    void newTiles(List<Tile> tiles);

    // Calls upon this player to take their turn (returning a turn action), with the given public
    // information from
    // the game state
    TurnAction takeTurn(ActivePlayerInfo currentState);

    // Informs this player if they won or lost
    void win(boolean win);

    static IPlayer fromJson(JsonArray jActorSpec) {
        Iterator<JsonElement> jActorSpecIterator = jActorSpec.iterator();
        String name = jActorSpecIterator.next().getAsString();
        Strategy strategy = Strategy.fromJson(jActorSpecIterator.next().getAsString());
        IPlayer player = new LocalPlayer(name, strategy);

        if (jActorSpecIterator.hasNext()) {
            String jExn = jActorSpecIterator.next().getAsString();

            if (jExn.equals("a cheat")) {
                String jCheat = jActorSpecIterator.next().getAsString();
                player = selectCheatPlayer(name, strategy, jCheat);
            } else {
                player = selectCommunicationFailurePlayer(name, strategy, jExn, jActorSpecIterator);
            }
        }

        return player;
    }

    // Constructs a cheating player with the given name, strategy, and cheat
    static IPlayer selectCheatPlayer(String name, Strategy strategy, String jCheat) {
        switch (jCheat) {
            case "non-adjacent-coordinate":
                return new NonAdjacentCoordinatePlayer(name);
            case "tile-not-owned":
                return new TileNotOwnedPlayer(name, strategy);
            case "not-a-line":
                return new NotALinePlayer(name, strategy);
            case "bad-ask-for-tiles":
                return new BadAskForTilePlayer(name, strategy);
            case "no-fit":
                return new NoFitPlayer(name, strategy);
            default:
                throw new RuntimeException("Illegal JCheat given");
        }
    }

    // Constructs an IPlayerProxy with the given name, strategy, and exception, using the given
    // iterator over
    // JSON elements to determine whether it has a count (meaning that it is a slow player) or not
    // (meaning that it is an exception-raising player)
    // NOTE: Assumes that the iterator has already seen the first three elements of a JActorSpecB
    static IPlayer selectCommunicationFailurePlayer(
            String name, Strategy strategy, String jExn, Iterator<JsonElement> jActorSpecIterator) {
        if (jActorSpecIterator.hasNext()) {
            int count = jActorSpecIterator.next().getAsInt();

            switch (jExn) {
                case "setup":
                    return new SetupCommFailurePlayer(name, strategy, count);
                case "take-turn":
                    return new TakeTurnCommFailurePlayer(name, strategy, count);
                case "new-tiles":
                    return new NewTilesCommFailurePlayer(name, strategy, count);
                case "win":
                    return new WinCommFailurePlayer(name, strategy, count);
            }
        } else {
            switch (jExn) {
                case "setup":
                    return new SetupCommFailurePlayer(name, strategy);
                case "take-turn":
                    return new TakeTurnCommFailurePlayer(name, strategy);
                case "new-tiles":
                    return new NewTilesCommFailurePlayer(name, strategy);
                case "win":
                    return new WinCommFailurePlayer(name, strategy);
            }
        }

        throw new RuntimeException("Illegal JExn given");
    }

    public JsonElement toJson();
}

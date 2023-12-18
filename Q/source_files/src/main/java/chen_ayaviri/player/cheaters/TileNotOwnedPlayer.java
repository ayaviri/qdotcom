package chen_ayaviri.player.cheaters;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.PlayerState;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Set;
import java.util.ArrayList;

// Represents a player that requests the valid placement of a tile that they do not own
public class TileNotOwnedPlayer extends ACheatingPlayer {
    public TileNotOwnedPlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    // Returns true if the active player does not own every single unique tile
    protected boolean canCheat(ActivePlayerInfo currentState) {
        return !this.getTilesNotOwned(currentState).isEmpty();
    }

    protected TurnAction thenCheat(ActivePlayerInfo currentState) {
        ActivePlayerInfo desiredState = new ActivePlayerInfo(
            currentState.getMap(),
            new PlayerState(this, this.name, new ArrayList<>(this.getTilesNotOwned(currentState))),
            currentState.getRemainingRefereeTiles(),
            currentState.getPlayerScores()
        );
        return this.strategy.computeIteratedTurnAction(desiredState);
    }

    protected Set<Tile> getTilesNotOwned(ActivePlayerInfo currentState) {
        Set<Tile> tilesNotOwned = Tile.getAllDistinct();
        tilesNotOwned.removeAll(currentState.getTiles());

        return tilesNotOwned;
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("a cheat");
        a.add("tile-not-owned");
        return a;
    }
}

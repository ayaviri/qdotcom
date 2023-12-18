package chen_ayaviri.player.cheaters;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.GameState;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

// Represents a player that requests an exchange of tiles when it has more tiles than the referee has remaining
public class BadAskForTilePlayer extends ACheatingPlayer {
    public BadAskForTilePlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    // Returns true if the referee has less tiles than the active player
    protected boolean canCheat(ActivePlayerInfo currentState) {
        return currentState.getRemainingRefereeTiles() < currentState.getTiles().size();
    }

    protected TurnAction thenCheat(ActivePlayerInfo currentState) {
        return new GameState.ExchangeAction();
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("a cheat");
        a.add("bad-ask-for-tiles");
        return a;
    }
}

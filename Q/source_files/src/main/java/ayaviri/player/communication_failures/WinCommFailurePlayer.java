package ayaviri.player.communication_failures;

import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

// Player that causes a communication failure during a call to its NewTiles method
public class WinCommFailurePlayer extends ACommFailurePlayer {
    public WinCommFailurePlayer(String name, Strategy strategy, int count) {
        super(name, strategy, count);
    }

    public WinCommFailurePlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    @Override
    public void win(boolean win) {
        super.possibleExplosion();
        super.win(win);
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("win");
        return a;
    }
}

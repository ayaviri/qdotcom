package ayaviri.player.communication_failures;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.TurnAction;
import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

// Player that causes a communication failure during a call to its TakeTurn method
public class TakeTurnCommFailurePlayer extends ACommFailurePlayer {
    public TakeTurnCommFailurePlayer(String name, Strategy strategy, int count) {
        super(name, strategy, count);
    }

    public TakeTurnCommFailurePlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    @Override
    public TurnAction takeTurn(ActivePlayerInfo currentState) {
        super.possibleExplosion();
        return super.takeTurn(currentState);
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("take-turn");
        return a;
    }
}

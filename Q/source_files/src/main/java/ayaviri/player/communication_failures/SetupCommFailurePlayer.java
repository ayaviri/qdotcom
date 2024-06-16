package ayaviri.player.communication_failures;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.map_representation.Tile;
import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.List;

// Player that causes a communication failure during a call to its setup method
public class SetupCommFailurePlayer extends ACommFailurePlayer {
    public SetupCommFailurePlayer(String name, Strategy strategy, int count) {
        super(name, strategy, count);
    }

    public SetupCommFailurePlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    @Override
    public void setup(ActivePlayerInfo state, List<Tile> tiles) {
        super.possibleExplosion();
        super.setup(state, tiles);
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("setup");
        return a;
    }
}

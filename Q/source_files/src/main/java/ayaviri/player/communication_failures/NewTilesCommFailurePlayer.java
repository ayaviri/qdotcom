package ayaviri.player.communication_failures;

import ayaviri.map_representation.Tile;
import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.List;

// Player that causes a communication failure during a call to its NewTiles method
public class NewTilesCommFailurePlayer extends ACommFailurePlayer {
    public NewTilesCommFailurePlayer(String name, Strategy strategy, int count) {
        super(name, strategy, count);
    }

    public NewTilesCommFailurePlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    @Override
    public void newTiles(List<Tile> tiles) {
        super.possibleExplosion();
        super.newTiles(tiles);
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("new-tiles");
        return a;
    }
}

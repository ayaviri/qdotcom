package chen_ayaviri.player;

import chen_ayaviri.map_representation.Tiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.JsonSerializable;
import chen_ayaviri.map_representation.Tile;

import java.util.List;
import java.util.concurrent.Callable;

// A function object that represents a call to the given player proxy's 
// setup method with the given ActivePlayerInfo and list of tiles
public class Setup implements Callable<Void>, JsonSerializable {
    private final IPlayer playerProxy;
    private final ActivePlayerInfo currentState;
    private final List<Tile> tiles;

    public Setup(IPlayer playerProxy, ActivePlayerInfo currentState, List<Tile> tiles) {
        this.playerProxy = playerProxy;
        this.currentState = currentState;
        this.tiles = tiles;
    }

    public Void call() {
        this.playerProxy.setup(this.currentState, this.tiles);
        return null;
    }

    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(new JsonPrimitive("setup"));
        JsonArray arguments = new JsonArray();
        arguments.add(this.currentState.toJson());
        arguments.add(Tiles.toJson(this.tiles));
        json.add(arguments);
        return json;
    }
}

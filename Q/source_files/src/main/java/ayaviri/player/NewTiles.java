package ayaviri.player;

import ayaviri.common.JsonSerializable;
import ayaviri.map_representation.Tile;
import ayaviri.map_representation.Tiles;
import com.google.gson.JsonArray;
import java.util.List;
import java.util.concurrent.Callable;

// A function object that represents a call to the given player proxy's
// newTiles method with the given list of tiles
public class NewTiles implements Callable<Void>, JsonSerializable {
    private final IPlayer playerProxy;
    private final List<Tile> tiles;

    public NewTiles(IPlayer playerProxy, List<Tile> tiles) {
        this.playerProxy = playerProxy;
        this.tiles = tiles;
    }

    public Void call() {
        this.playerProxy.newTiles(this.tiles);
        return null;
    }

    public JsonArray toJson() {
        JsonArray json = new JsonArray();
        json.add("new-tiles");
        json.add(Tiles.toJson(this.tiles));
        return json;
    }
}

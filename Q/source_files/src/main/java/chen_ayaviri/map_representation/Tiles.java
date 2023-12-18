package chen_ayaviri.map_representation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

// Represents a class of utility functions that can be performed on a list of Tile objects
public class Tiles {
    
    // Gets the set of tiles in which each element doesn't match AT LEAST ONE 
    // of the tiles in the given list in BOTH SHAPE AND COLOR
    public static Set<Tile> getNonMatchingTiles(List<Tile> tiles) {
        Set<Tile> nonMatchingTiles = new HashSet<>();

        for (Tile tile : Tile.getAllDistinct()) {
            if (tile.doesNotMatchAtLeastOne(tiles)) {
                nonMatchingTiles.add(tile);
            }
        }

        return nonMatchingTiles;
    }

    public static List<Tile> fromJson(JsonArray jTiles) {
        List<Tile> tiles = new ArrayList<Tile>();
        Iterator<JsonElement> jsonIterator = jTiles.iterator();

        while (jsonIterator.hasNext()) {
            JsonObject tile = jsonIterator.next().getAsJsonObject();
            tiles.add(Tile.fromJson(tile));
        }

        return tiles;
    }

    public static JsonArray toJson(List<Tile> tiles) {
        JsonArray jTileArray = new JsonArray();

        for (Tile tile : tiles) {
            jTileArray.add(Tile.toJson(tile));
        }

        return jTileArray;
    }
}

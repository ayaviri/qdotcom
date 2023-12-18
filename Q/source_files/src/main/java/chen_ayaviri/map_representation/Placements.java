package chen_ayaviri.map_representation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

// Represents a class of utility functions that can be performed on a list of Placement objects
public class Placements {

    public static List<Placement> fromJson(JsonArray jPlacements) {
        List<Placement> placements = new ArrayList<Placement>();
        for (JsonElement onePlacement : jPlacements) {
            Placement placement = Placement.fromJson(onePlacement.getAsJsonObject());
            placements.add(placement);
        }
        return placements;
    }

    public static JsonElement toJson(List<Placement> placements) {
        JsonArray jPlacements = new JsonArray();
        for(Placement placement : placements) {
            jPlacements.add(Placement.toJson(placement));
        }
        return jPlacements;
    }

    public static List<Posn> getPositions(List<Placement> placements) {
        List<Posn> positions = new ArrayList<Posn>();

        for (Placement placement : placements) {
            positions.add(placement.getPosition());
        }

        return positions;
    }

    public static List<Tile> getTiles(List<Placement> placements) {
        List<Tile> tiles = new ArrayList<Tile>();

        for (Placement placement : placements) {
            tiles.add(placement.getTile());
        }

        return tiles;
    }
}

package ayaviri.map_representation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Objects;

// Represents a potential placement on a map during the active player's turn, consisting of the tile
// and position
public class Placement {
    private final Tile tile;
    private final Posn position;

    public Placement(Tile tile, Posn position) {
        this.tile = tile;
        this.position = position;
    }

    public static Placement fromJson(JsonObject jPlacement) {
        JsonObject jCoord = jPlacement.get("coordinate").getAsJsonObject();
        JsonObject jTile = jPlacement.get("1tile").getAsJsonObject();
        int column = jCoord.get("column").getAsInt();
        int row = jCoord.get("row").getAsInt();
        Posn position = new Posn(column, row);
        Tile tile = Tile.fromJson(jTile);
        return new Placement(tile, position);
    }

    public static JsonElement toJson(Placement placement) {
        JsonObject onePlacement = new JsonObject();
        onePlacement.add("coordinate", Posn.toJson(placement.getPosition()));
        onePlacement.add("1tile", Tile.toJson(placement.getTile()));
        return onePlacement;
    }

    public Tile getTile() {
        return this.tile;
    }

    public Posn getPosition() {
        return this.position;
    }

    public boolean equals(Object other) {
        return other instanceof Placement
                && ((Placement) other).getTile().equals(this.getTile())
                && ((Placement) other).getPosition().equals(this.getPosition());
    }

    public int hashCode() {
        return Objects.hash(this.tile, this.position);
    }

    public String toString() {
        return String.format("[%s,%s]", this.tile.toString(), this.position.toString());
    }
}

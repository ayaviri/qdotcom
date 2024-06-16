package ayaviri.player.cheaters;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.GameState;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.Placement;
import ayaviri.map_representation.Posn;
import ayaviri.map_representation.QMapBoundaries;
import ayaviri.strategy.DAG;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

// Represents a player that requests the placement of a tile that is not adjacent to a placed tile
public class NonAdjacentCoordinatePlayer extends ACheatingPlayer {
    public NonAdjacentCoordinatePlayer(String name) {
        super(name, new DAG()); // Independent of strategy
    }

    protected boolean canCheat(ActivePlayerInfo currentState) {
        return true;
    }

    protected TurnAction thenCheat(ActivePlayerInfo currentState) {
        QMapBoundaries mapBoundaries = new QMapBoundaries(currentState.getMap());
        Posn topLeft = mapBoundaries.getTopLeft();
        Posn nonAdjacentPosition = topLeft.translateRelativeTo(new Posn(-1, -1));
        Placement placement = new Placement(currentState.getTiles().get(0), nonAdjacentPosition);

        return new GameState.PlaceAction(placement);
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("a cheat");
        a.add("non-adjacent-coordinate");
        return a;
    }
}

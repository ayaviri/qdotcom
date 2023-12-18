package chen_ayaviri.player.cheaters;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.GameState;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.map_representation.QMapBoundaries;
import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.strategy.DAG;
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

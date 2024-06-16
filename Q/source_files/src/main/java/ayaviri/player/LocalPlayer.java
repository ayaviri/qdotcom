package ayaviri.player;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.QMap;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.Tile;
import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

// Represents a well-behaving local player implementation
public class LocalPlayer implements IPlayer {
    protected final String name;
    private QMap initialMap;
    private List<Tile> tiles;
    protected Strategy strategy;

    public LocalPlayer(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    @Override
    public void setup(ActivePlayerInfo activePlayerInfo, List<Tile> tiles) {
        this.initialMap = new QMap(activePlayerInfo.getMap());
        this.tiles = new ArrayList<Tile>(tiles);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void newTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    @Override
    public TurnAction takeTurn(ActivePlayerInfo currentState) {
        return this.strategy.computeIteratedTurnAction(currentState);
    }

    @Override
    public void win(boolean win) {
        return;
    }

    public String toString() {
        return String.format("{Player: %s}", this.name());
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = new JsonArray();
        a.add(this.name);
        a.add(this.strategy.toString());
        return a;
    }
}

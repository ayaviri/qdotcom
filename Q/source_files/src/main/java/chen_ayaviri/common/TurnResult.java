package chen_ayaviri.common;

import chen_ayaviri.map_representation.Tile;

import java.util.ArrayList;
import java.util.List;

// Represents the result of a player's turn with relevant information for the referee and the player that
// conducted the turn
public class TurnResult {
    private final boolean placedAllTiles;
    private final List<Tile> newTiles;
    private final TurnAction turnAction;

    private TurnResult(Builder builder) {
        this.placedAllTiles = builder.placedAllTiles;
        this.newTiles = builder.newTiles;
        this.turnAction = builder.turnAction;
    }

    public static class Builder {
        private boolean placedAllTiles;
        private List<Tile> newTiles;
        private final TurnAction turnAction;

        // Sets the default values for each field in the TurnResult class
        // turnAction must exist for every turn result and thus is passed into the builder upon construction
        public Builder(TurnAction turnAction) {
            this.placedAllTiles = false;
            this.newTiles = new ArrayList<Tile>();
            this.turnAction = turnAction;
        }

        public Builder placedAllTiles(boolean placedAllTiles) {
            this.placedAllTiles = placedAllTiles;
            return this;
        }

        public Builder newTiles(List<Tile> newTiles) {
            this.newTiles = newTiles;
            return this;
        }

        public TurnResult build() {
            return new TurnResult(this);
        }
    }

    public boolean placedAllTiles() {
        return this.placedAllTiles;
    }

    public List<Tile> getNewTiles() {
        return this.newTiles;
    }

    public TurnAction getTurnAction() {
        return this.turnAction;
    }
}

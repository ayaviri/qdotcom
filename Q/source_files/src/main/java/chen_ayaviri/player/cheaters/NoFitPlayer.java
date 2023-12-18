package chen_ayaviri.player.cheaters;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.GameState;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Tiles;
import chen_ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.Optional;
import java.util.Set;

// Represents a player that requests a placement of a tile that does not match its neighbors in response
// to being granted a turn
public class NoFitPlayer extends ACheatingPlayer {
    public NoFitPlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    protected boolean canCheat(ActivePlayerInfo currentState) {
        return this.getNonMatchingPlacement(currentState).isPresent();
    }

    protected TurnAction thenCheat(ActivePlayerInfo currentState) {
        return new GameState.PlaceAction(
            this.getNonMatchingPlacement(currentState).get()
        );
    }

    // Replaces the tile from the first placement returned by this player's strategy with one
    // that does not match its neighbors, if such a tile is present in the given state's hand.
    // Returns the placement if possible, an empty object otherwise.
    // NOTE: Creates a copy of the given state to mutate the map and hand as necessary
    protected Optional<Placement> getNonMatchingPlacement(ActivePlayerInfo currentState) {
        ActivePlayerInfo stateCopy = new ActivePlayerInfo(currentState);
        TurnAction turnAction = this.strategy.computeTurnAction(stateCopy);

        if (turnAction instanceof GameState.PlaceAction) {
            // NOTE: relies on the invariant held by PlaceAction that the sequence of placements is nonempty
            Placement placement = ((GameState.PlaceAction) turnAction).getPlacements().get(0);
            List<Tile> neighboringTiles = stateCopy.getMap().getNeighboringTiles(placement.getPosition());
            Set<Tile> nonMatchingTiles = Tiles.getNonMatchingTiles(neighboringTiles);
            Optional<Tile> chosenNonMatchingTile = this.selectFromHand(stateCopy.getTiles(), nonMatchingTiles);

            if (chosenNonMatchingTile.isPresent()) {
                return Optional.of(
                    new Placement(chosenNonMatchingTile.get(), placement.getPosition())
                );
            }
        }

        return Optional.empty();
    }

    // Returns the first tile from the given hand that is contained in the given set _toChooseFrom_.
    // If there is none, returns an empty object
    protected Optional<Tile> selectFromHand(List<Tile> hand, Set<Tile> toChooseFrom) {
        for (Tile tile : hand) {
            if (toChooseFrom.contains(tile)) {
                return Optional.of(tile);
            }
        }

        return Optional.empty();
    }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("a cheat");
        a.add("no-fit");
        return a;
    }
}

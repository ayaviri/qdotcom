package ayaviri.player.cheaters;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.GameState;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.Placement;
import ayaviri.map_representation.Placements;
import ayaviri.map_representation.Posn;
import ayaviri.map_representation.Posns;
import ayaviri.map_representation.Tile;
import ayaviri.strategy.Strategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Represents a player that requests placements that are not in a line upon being granted a turn
public class NotALinePlayer extends ACheatingPlayer {
    private final int MINIMUM_PLACEMENTS_NEEDED = 2;

    public NotALinePlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    protected boolean canCheat(ActivePlayerInfo currentState) {
        return this.getPlacementsNotInLine(currentState).size() == this.MINIMUM_PLACEMENTS_NEEDED;
    }

    protected TurnAction thenCheat(ActivePlayerInfo currentState) {
        return new GameState.UncheckedPlaceAction(this.getPlacementsNotInLine(currentState));
    }

    // Returns a list of placements (whose size is bounded by MINIMUM_PLACEMENTS_NEEDED) in which
    // the elements disobey the line invariant.
    // NOTE: Creates a copy of the given state in order to mutate the map as necessary
    protected List<Placement> getPlacementsNotInLine(ActivePlayerInfo currentState) {
        ActivePlayerInfo stateCopy = new ActivePlayerInfo(currentState);
        List<Placement> placements = new ArrayList<>();
        Optional<Placement> nextPlacement = this.getNextPlacement(placements, currentState);

        while (placements.size() < this.MINIMUM_PLACEMENTS_NEEDED || nextPlacement.isPresent()) {
            placements.add(nextPlacement.get());
            nextPlacement = this.getNextPlacement(placements, currentState);
        }

        return placements;
    }

    // Gets a next possible placement that ensures the line invariant with the given accumulated
    // list of placements remains broken. Uses the given ActivePlayerInfo and mutates its map if
    // such a placement exists
    protected Optional<Placement> getNextPlacement(
            List<Placement> placementsSoFar, ActivePlayerInfo currentState) {
        List<Posn> positionsSoFar = Placements.getPositions(placementsSoFar);

        for (Tile tile : currentState.getTiles()) {
            Set<Posn> validPositions = currentState.getMap().getValidTilePositions(tile);
            Optional<Placement> possiblePlacement =
                    this.getFirstLineBreakingPlacement(positionsSoFar, tile, validPositions);

            if (possiblePlacement.isPresent()) {
                currentState.getMap().placeTile(possiblePlacement.get());

                return possiblePlacement;
            }
        }

        return Optional.empty();
    }

    // Gets the first position from the given set of valid positions that breaks the line invariant
    // with the given accumulated
    // list of positions, and constructs a placement with it and the given tile. Returns empty
    // if such a position does not exist
    protected Optional<Placement> getFirstLineBreakingPlacement(
            List<Posn> positionsSoFar, Tile tile, Set<Posn> validPositions) {
        for (Posn position : validPositions) {
            List<Posn> possiblePositions = new ArrayList<>(positionsSoFar);
            possiblePositions.add(position);

            if (!Posns.allSameRowXorColumn(possiblePositions)) {
                return Optional.of(new Placement(tile, position));
            }
        }

        return Optional.empty();
    }

    // public TurnAction takeTurn(ActivePlayerInfo currentState) {
    //     TurnAction turnAction = super.takeTurn(currentState);

    //     if (turnAction instanceof GameState.PlaceAction) {
    //         List<Placement> placements = ((GameState.PlaceAction) turnAction).getPlacements();
    //         if (placements.size() == 1) {
    //             return turnAction;
    //         }
    //         // TODO: could force a player to cheat with a single placement when it shouldn't
    //         List<Placement> invalidPlacements = this.ruinSameRowOrColumnInvariant(placements);
    //         return new GameState.UncheckedPlaceAction(invalidPlacements);
    //     }

    //     return turnAction;
    // }

    // // Creates a new list of placements in which the first placement is moved out of line
    // // so that the same row or column invariant for this sequence is no longer held
    // // NOTE: Assumes a non-empty list
    // protected List<Placement> ruinSameRowOrColumnInvariant(List<Placement> placements) {
    //     List<Placement> invalidPlacements = new ArrayList<>();
    //     Iterator<Placement> placementsIterator = placements.iterator();
    //     invalidPlacements.add(this.constructInvalidFirstPlacement(placementsIterator.next()));

    //     while (placementsIterator.hasNext()) {
    //         invalidPlacements.add(placementsIterator.next());
    //     }

    //     return invalidPlacements;
    // }

    // // Constructs an invalid placement from the given one by translating one unit in a direction
    // private Placement constructInvalidFirstPlacement(Placement oldPlacement) {
    //     // TODO: update to move one unit in both axes
    //     Posn newPosition = new Posn.TranslateUp().apply(
    //         oldPlacement.getPosition(),
    //         1
    //     );

    //     return new Placement(oldPlacement.getTile(), newPosition);
    // }

    @Override
    public JsonElement toJson() {
        JsonArray a = super.toJson().getAsJsonArray();
        a.add("a cheat");
        a.add("not-a-line");
        return a;
    }
}

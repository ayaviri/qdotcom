package ayaviri.strategy;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.GameState;
import ayaviri.common.GameState.PlaceAction;
import ayaviri.common.QMap;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.CanonicalTileComparator;
import ayaviri.map_representation.Placement;
import ayaviri.map_representation.Posn;
import ayaviri.map_representation.Tile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

// Represents an abstract implementation of Dumb And Greedy game playing strategy that computes the
// next turn action by
// - mapping all tiles to their possible placement positions (sorted) and filtering out those that
// cannot be placed
// - sorting the remaining candidate tiles and choosing the smallest to create a placement
// If a placement is impossible, attempts to exchange if there are sufficient remaining referee
// tiles, otherwise pass
public abstract class ADAG implements Strategy {
    // A comparator to sort candidate tiles so that the first element of the sorted list is used for
    // placement first
    private final Comparator<Tile> initialTileSelectionComparator;

    public ADAG() {
        this.initialTileSelectionComparator = new CanonicalTileComparator();
    }

    // Produces a pass, exchange, or single placement given the current state using this strategy
    public TurnAction computeTurnAction(ActivePlayerInfo currentState) {
        return this.computeTurnActionHelper(currentState, new OnePlacementConstructor());
    }

    // Produces a pass, exchange, or exhausted sequence of placements given the current state using
    // this strategy
    public TurnAction computeIteratedTurnAction(ActivePlayerInfo currentState) {
        return this.computeTurnActionHelper(currentState, new PlacementSequenceConstructor());
    }

    // Computes a turn action given the current state and a function dictating how to construct a
    // place action
    // If a place action is not possible, computes either a pass or exchange action
    protected TurnAction computeTurnActionHelper(
            ActivePlayerInfo currentState,
            BiFunction<QMap, List<Tile>, Optional<PlaceAction>> placeActionConstructor) {
        Optional<PlaceAction> placeAction =
                placeActionConstructor.apply(currentState.getMap(), currentState.getTiles());

        if (!placeAction.isPresent()) {
            return this.determinePassOrExchange(currentState);
        }

        return placeAction.get();
    }

    // Returns an optional place action that contains a sequence of placements, if it is present
    protected Optional<PlaceAction> constructPlacementSequence(QMap map, List<Tile> hand) {
        GameState.PlaceAction.Builder placeActionBuilder = new GameState.PlaceAction.Builder();
        Optional<Placement> potentialPlacement = this.constructNextPlacement(map, hand);

        while (potentialPlacement.isPresent()) {
            Placement placement = potentialPlacement.get();
            boolean additionSuccess = placeActionBuilder.addPlacement(placement);

            if (!additionSuccess) {
                break;
            }

            map.placeTile(placement);
            hand.remove(placement.getTile());
            potentialPlacement = this.constructNextPlacement(map, hand);
        }

        return placeActionBuilder.tryBuild();
    }

    // Returns an optional place action that contains a single placement, if it is present
    // public for use in NoFitPlayer
    protected Optional<PlaceAction> constructOnePlacement(QMap currentMap, List<Tile> currentHand) {
        Optional<Placement> potentialPlacement =
                this.constructNextPlacement(currentMap, currentHand);

        if (!potentialPlacement.isPresent()) {
            return Optional.empty();
        }

        PlaceAction.Builder placeActionBuilder = new PlaceAction.Builder();
        placeActionBuilder.addPlacement(potentialPlacement.get());

        return placeActionBuilder.tryBuild();
    }

    // Constructs the next smallest possible placement from given map and hand
    // Returns an empty optional object if there is no possible placement
    public Optional<Placement> constructNextPlacement(QMap currentMap, List<Tile> currentHand) {
        Map<Tile, List<Posn>> filteredHandPositions =
                this.getValidPositionsForTiles(currentMap, currentHand);
        List<Tile> candidateTiles = new ArrayList<>(filteredHandPositions.keySet());

        if (candidateTiles.isEmpty()) {
            return Optional.empty();
        }

        candidateTiles.sort(this.initialTileSelectionComparator);
        Tile smallestTile = candidateTiles.get(0);
        Posn smallestTilePosition = filteredHandPositions.get(smallestTile).get(0);
        Placement placement = new Placement(smallestTile, smallestTilePosition);

        return Optional.of(placement);
    }

    // Returns a map from each tile in the given hand to the list of positions on which it can be
    // placed in the given
    // QMap, excluding the tiles with no valid placements
    // Each list of positions is sorted according to this strategy's comparator from most favorable
    // to least
    protected Map<Tile, List<Posn>> getValidPositionsForTiles(
            QMap currentMap, List<Tile> currentHand) {
        Map<Tile, List<Posn>> allTilesToValidPositions = new HashMap<Tile, List<Posn>>();

        for (Tile tile : currentHand) {
            List<Posn> validPositions = new ArrayList<Posn>(currentMap.getValidTilePositions(tile));

            if (!validPositions.isEmpty()) {
                this.sortCandidatePositions(currentMap, validPositions);
                allTilesToValidPositions.put(tile, validPositions);
            }
        }

        return allTilesToValidPositions;
    }

    // This helper is abstracted so that subclasses can implement position sorting. Some strategies
    // use the current
    // state of the map to compare positions while others do not.
    // Modifies the given list of positions.
    protected abstract void sortCandidatePositions(QMap currentMap, List<Posn> candidatePositions);

    protected TurnAction determinePassOrExchange(ActivePlayerInfo currentState) {
        List<Tile> currentHand = currentState.getTiles();

        if (currentState.getRemainingRefereeTiles() >= currentHand.size()) {
            return new GameState.ExchangeAction();
        }

        return new GameState.PassAction();
    }

    // Returns an optional place action that contains a single placement, if it is present
    private class OnePlacementConstructor
            implements BiFunction<QMap, List<Tile>, Optional<PlaceAction>> {
        public Optional<PlaceAction> apply(QMap map, List<Tile> hand) {
            return constructOnePlacement(map, hand);
        }
    }

    // Returns an optional place action that contains a sequence of placements, if it is present
    private class PlacementSequenceConstructor
            implements BiFunction<QMap, List<Tile>, Optional<PlaceAction>> {
        public Optional<PlaceAction> apply(QMap map, List<Tile> hand) {
            return constructPlacementSequence(map, hand);
        }
    }
}

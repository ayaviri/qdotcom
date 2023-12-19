package chen_ayaviri.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.map_representation.Placements;
import chen_ayaviri.map_representation.Posn;
import chen_ayaviri.map_representation.Posns;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Tiles;
import chen_ayaviri.map_representation.shapes.AShape;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.visuals.GameStateImage;
import chen_ayaviri.visuals.GameStateRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

// Represents the referee's knowledge of a game, including the map, each player's hand
// and score, and the list of referee tiles
public class GameState {
    protected int Q_BONUS;
    protected int END_OF_GAME_BONUS;
    private final QMap map;
    // NOTE: It is assumed that no two players have the same name
    private final Players players;
    private final List<Tile> refereeTiles;

    // TODO: change the type of the players input to just receive the player proxies, rerequest
    // the player name from them when necessary
    // Sets up the game state with the following pieces of information:
    // - the order of the players, identified by their unique names
    // - a map of each player's name to their initial tiles
    // - the initial map (containing one placed referee tile)
    // - the referee tiles
    // NOTE: with the advent of the xserver script, this is has been relegated to testing only
    // given that an initial state will always be given
    public GameState(List<IPlayer> players,
                     Map<String, List<Tile>> playerTiles,
                     QMap map,
                     List<Tile> refereeTiles) {
        this.players = new Players(players, playerTiles);
        this.map = new QMap(map);
        this.refereeTiles = new ArrayList<Tile>(refereeTiles);
        this.initializeScoringConstants(new GameStateConfig());
    }

    // Creates a copy from the given game state (under the assumption that it has everything BUT
    // the proxies to communicate with remote players) with the given player proxies
    // NOTE: Assumes that the given state has player information in the same order as the given 
    // list of player proxies
    public GameState(GameState proxylessState, List<IPlayer> playerProxies) {
        this.map = new QMap(proxylessState.map);
        this.refereeTiles = new ArrayList<Tile>(proxylessState.refereeTiles);
        this.Q_BONUS = proxylessState.Q_BONUS;
        this.END_OF_GAME_BONUS = proxylessState.END_OF_GAME_BONUS;
        this.players = new Players(proxylessState.players, playerProxies);
    }

    // Creates a new GameState with a COPY of each field from the given game state
    public GameState(GameState gameState) {
        this.map = new QMap(gameState.map);
        this.refereeTiles = new ArrayList<Tile>(gameState.refereeTiles);
        this.Q_BONUS = gameState.Q_BONUS;
        this.END_OF_GAME_BONUS = gameState.END_OF_GAME_BONUS;
        // TODO: this will give an observer access to the original player proxies...
        this.players = new Players(gameState.players);
    }

    // TODO: Check if this is still necessary, otherwise REMOVE please
    // Constructs a well-formed game state with the information from a JPub
    protected GameState(QMap map, Players players, List<Tile> refereeTiles) {
        this.players = players;
        this.map = new QMap(map);
        this.refereeTiles = new ArrayList<Tile>(refereeTiles);
        this.initializeScoringConstants(new GameStateConfig());
    }

    // Sets the scoring constants for this state using the given config
    public void initializeScoringConstants(GameStateConfig config) {
        this.Q_BONUS = config.getQBonus();
        this.END_OF_GAME_BONUS = config.getEndOfGameBonus();
    }

    public GameStateImage visualize() {
        return new GameStateImage(
                this.map.visualize(),
                this.players.visualize(),
                this.getRemainingRefereeTiles()
            );
    }

    public void render() {
        GameStateRenderer.render(this.visualize());
    }

    // TODO: this is from a JSTATE NOT A JPUB
    // Constructs a game state from the given JPub and JActors array
    public static GameState fromJson(JsonObject jPub, JsonArray jActors) {
        QMap map = QMap.fromJson(jPub.get("map").getAsJsonArray());
        List<Tile> remainingRefereeTiles = Tiles.fromJson(jPub.get("tile*").getAsJsonArray());
        Players players = Players.fromJson(jPub.get("players").getAsJsonArray(), jActors);
        return new GameState(map, players, remainingRefereeTiles);
    }

    // Constructs a game state (with NO player proxies) from the given JState
    public static GameState fromJson(JsonElement jState) {
        JsonObject json = jState.getAsJsonObject();
        QMap map = QMap.fromJson(json.get("map").getAsJsonArray());
        List<Tile> remainingRefereeTiles = Tiles.fromJson(json.get("tile*").getAsJsonArray());
        Players players = Players.fromJson(json.get("players").getAsJsonArray());

        return new GameState(map, players, remainingRefereeTiles);
    }

    // Converts this game state to a JState
    public JsonObject toJson() {
        JsonObject jState = new JsonObject();
        jState.add("map", this.map.toJson());
        jState.add("tile*", Tiles.toJson(this.getActivePlayerState().getTiles()));
        jState.add("players", this.players.toJson());
        return jState;
    }

    // Returns a COPY of the player state information with the ORIGINAL
    // player communication object
    public PlayerState getActivePlayerState() {
        return this.players.getActivePlayerState();
    }

    public int getNumberOfPlayers() {
        return this.players.getOrdering().size();
    }

    public List<String> getPlayerOrder() {
        return this.players.getOrdering();
    }

    // Info for the active player containing copies of public information from the game state
    public ActivePlayerInfo getInfoForActivePlayer() {
        return this.getInfoForPlayer(this.players.getActivePlayerState());
    }

    // TODO: Is this needed anymore ? The player setup issue in the referee has been resolved by advancing the queue
    // after each call to setup
    // Info for the GIVEN player containing copies of public information the game state
    public ActivePlayerInfo getInfoForPlayer(PlayerState player) {
        return new ActivePlayerInfo(
            new QMap(this.map),
            player,
            this.getRemainingRefereeTiles(),
            // NOTE: the call to setup of the Player API in which each player gets the same score list 
            // won't be correct if points have already been accumulated
            this.players.getScoreList()
        );
    }

    // Advances the player queue, moving the current active player to the back of the queue
    public void advancePlayerQueue() {
        this.players.updateActive();
    }

    // Returns a copy of the list of player states
    public List<PlayerState> getPlayerStates() {
        return this.players.getPlayerStates();
    }

    protected List<IPlayer> getPlayerProxies() {
        return this.players.getPlayerProxies();
    }

    public boolean checkLegalityOf(TurnAction action) {
        return action.isLegalFor(this);
    }

    // Performs and scores the given action
    // Updates the map and the score of the active player
    // Advances the active player
    // NOTE: must be called AFTER the legality is checked for the given action
    public TurnResult performCheckedTurnAction(TurnAction action) {
        TurnResult.Builder turnResultBuilder = new TurnResult.Builder(action);
        action.performCheckedOn(this, this.players.getActivePlayerState(), turnResultBuilder);
        return turnResultBuilder.build();
    }

    // Removes the active player and appends their tiles to the end of the referee tiles
    public void removeActivePlayer() {
        this.removePlayer(this.players.getActivePlayerState().getName());
    }

    // Removes the player with the given name and appends their tiles to the end of the referee tiles
    public void removePlayer(String name) {
        List<Tile> removedPlayerTiles = this.players.removePlayer(name);
        this.refereeTiles.addAll(removedPlayerTiles);
    }

    // Used for the purpose of testing side effects
    protected List<Tile> getRefereeTiles() {
        return new ArrayList<>(this.refereeTiles);
    }

    protected int getRemainingRefereeTiles() {
        return this.refereeTiles.size();
    }

    /* TURN ACTION DYNAMIC DISPATCH */

    // Checks if the player has less or equal tile count to the remaining referee tiles
    protected boolean isExchangeActionLegal() {
        return this.players.getActivePlayerState().getTileCount() <= this.getRemainingRefereeTiles();
    }

    // Checks validity of placements for the active player's turn based on 4 criteria:
    // 1) placements is non-empty
    // 2) active player has all the tiles in their hand
    // 3) all placements are valid
    // 4) all placements are in the same row xor column
    protected boolean isPlaceActionLegal(List<Placement> placements) {
        // TODO: consider removing the rules out of the game state ?
        return !placements.isEmpty() &&
                this.players.getActivePlayerState().hasAllTiles(Placements.getTiles(placements)) &&
                this.isValidOnCurrentMap(placements) &&
                Posns.allSameRowXorColumn(Placements.getPositions(placements));
    }

    // Exchanges the given player's tiles with referee tiles
    protected void checkedExchangeActivePlayerTiles(PlayerState activePlayer, TurnResult.Builder turnResultBuilder) {
        int tilesToRemove = activePlayer.getTileCount();
        List<Tile> oldTiles = activePlayer.removeTiles(activePlayer.getTiles());
        List<Tile> newTiles = this.removeFromRefereeAndAddToPlayer(tilesToRemove);
        refereeTiles.addAll(oldTiles);

        turnResultBuilder
            .newTiles(newTiles);
    }

    // Removes each placed tile from the active player's hand, adds tiles back from refereeTiles if there are enough
    protected void checkedPlaceActivePlayerTiles(PlayerState activePlayer,
                                                 List<Placement> placements,
                                                 TurnResult.Builder turnResultBuilder) {
        int originalHandSize = activePlayer.getTileCount();
        this.placeTilesOnMap(placements);
        activePlayer.removeTiles(Placements.getTiles(placements));

        List<Tile> newTiles = this.removeFromRefereeAndAddToPlayer(placements.size());
        this.scoreAndAddToActive(placements, originalHandSize);

        turnResultBuilder
            .placedAllTiles(originalHandSize == placements.size())
            .newTiles(newTiles);
    }

    // Removes the given number of tiles from the front of the referee tiles and returns them as a list
    protected List<Tile> removeFromRefereeTiles(int numberToRemove) {
        List<Tile> removedTiles = new ArrayList<>();

        for (int index = 0; index < numberToRemove; index++) {
            removedTiles.add(this.refereeTiles.remove(0));
        }

        return removedTiles;
    }

    // Checks validity of placement sequence on this state's map
    protected boolean isValidOnCurrentMap(List<Placement> placements) {
        QMap mapCopy = new QMap(this.map);

        for (Placement placement : placements) {
            Set<Posn> validPositions = mapCopy.getValidTilePositions(placement.getTile());

            if (!validPositions.contains(placement.getPosition())) {
                return false;
            }

            mapCopy.placeTile(placement);
        }

        return true;
    }

    // Places all the given placements onto the map
    // Assumes all placements are valid
    protected void placeTilesOnMap(List<Placement> placements) {
        for(Placement placement : placements) {
            this.map.placeTile(placement);
        }
    }

    // Removes the minimum between the given placement size and the number of remaining
    // referee tiles, adds them to the active player, and returns the active player's
    // new entire hand
    protected List<Tile> removeFromRefereeAndAddToPlayer(int numPlacements) {
        int tilesToRemove = Math.min(numPlacements, this.getRemainingRefereeTiles());
        List<Tile> returnedTiles = this.removeFromRefereeTiles(tilesToRemove);
        this.players.getActivePlayerState().addTiles(returnedTiles);
        return this.players.getActivePlayerState().getTiles();
    }

    /* SCORING */

    // Scores the given placement and adds it to the active player's score
    protected int scoreAndAddToActive(List<Placement> placements, int originalHandSize) {
        // TODO: remove scoring from the game state
        int score = this.scorePlacement(placements, originalHandSize);
        this.players.getActivePlayerState().addToScore(score);
        return score;
    }

    // Scores the given placement by the given active player on this state's map
    protected int scorePlacement(List<Placement> placements, int originalHandSize) {
        int score = 0;
        score += placements.size();
        score += this.scoreContiguousSequences(placements);
        score += this.scoreQs(placements);

        if (placements.size() == originalHandSize) {
            score += END_OF_GAME_BONUS;
        }

        return score;
    }

    // Returns the score based on the contiguous sequences that contain the given placements, where
    // one point is awarded per tile in a contiguous sequence (row or column) that contains at least one of the placements
    protected int scoreContiguousSequences(List<Placement> placements) {
        return this.scoreContiguousSequencesHelper(placements, new IsASequence(), new ScoreContiguousSequence());
    }

    // Returns the score based on the Q sequences that the given placements complete, where
    // 6 points are awarded per Q that contains at least one of the placements
    protected int scoreQs(List<Placement> placements) {
        return this.scoreContiguousSequencesHelper(placements, new IsQSequence(), new ScoreQSequence());
    }

    // Abstract method for scoreContiguousSequences and scoreQs for scoring contiguous sequences
    // Given:
    // - the list of placements,
    // - a predicate to determine validity of a contiguous sequence,
    // - a function to score a contiguous sequence
    // NOTE: only unique contiguous sequences are scored
    protected int scoreContiguousSequencesHelper(List<Placement> placements,
                                               Predicate<Set<Posn>> isValidSequence,
                                               Function<Set<Posn>, Integer> scoreSequence) {
        Set<Set<Posn>> seenSequences = new HashSet<Set<Posn>>();
        int score = 0;

        for (Placement placement : placements) {
            Posn pos = placement.getPosition();
            score += this.scoreSequence(seenSequences, this.map.getContiguousRow(pos), isValidSequence, scoreSequence);
            score += this.scoreSequence(seenSequences, this.map.getContiguousColumn(pos), isValidSequence, scoreSequence);
        }

        return score;
    }

    // Abstract method for scoreContiguousSequencesHelper for scoring unique contiguous sequences
    // Given:
    // - the list of seen contiguous sequences
    // - the contiguous sequence to be scored,
    // - a predicate to determine validity of the contiguous sequence,
    // - a function to score the contiguous sequence
    protected int scoreSequence(Set<Set<Posn>> seenSequences,
                                Set<Posn> contiguousSequence,
                                Predicate<Set<Posn>> isValidSequence,
                                Function<Set<Posn>, Integer> scoreSequence) {
        int score = 0;
        if (!seenSequences.contains(contiguousSequence) && isValidSequence.test(contiguousSequence)) {
            score += scoreSequence.apply(contiguousSequence);
            seenSequences.add(contiguousSequence);
        }
        return score;
    }

    // Assumes that the sequence is contiguous and contains tiles at every position
    protected boolean isQSequence(Set<Posn> contiguousSequence) {
        List<Tile> sequenceTiles = new ArrayList<Tile>();

        for (Posn pos : contiguousSequence) {
            sequenceTiles.add(this.map.getTileAt(pos).get());
        }
        return contiguousSequence.size() == 6 && this.sequenceContainsAllShapesOrColors(sequenceTiles);
    }

    // Determines whether this sequence of tiles contains all the shapes or colors in the game
    protected boolean sequenceContainsAllShapesOrColors(List<Tile> sequenceTiles) {
        Set<AShape> allShapes = Tile.getAllShapes();
        Set<Tile.QColor> allColors = Tile.getAllColors();
        Set<AShape> sequenceShapes = new HashSet<AShape>();
        Set<Tile.QColor> sequenceColors = new HashSet<Tile.QColor>();

        for (Tile tile : sequenceTiles) {
            sequenceShapes.add(tile.getShape());
            sequenceColors.add(tile.getColor());
        }

        return allShapes.equals(sequenceShapes) || allColors.equals(sequenceColors);
    }

    // Determines if the given set of positions is a sequence, where a sequence consists of more than 1 tile
    protected class IsASequence implements Predicate<Set<Posn>> {
        @Override
        public boolean test(Set<Posn> sequence) {
            return sequence.size() > 1;
        }
    }

    // Determines if the given sequence is a Q
    protected class IsQSequence implements Predicate<Set<Posn>> {
        @Override
        public boolean test(Set<Posn> sequence) {
            return isQSequence(sequence);
        }
    }

    // Scores the given contiguous sequence, where each tile is worth 1 point
    protected class ScoreContiguousSequence implements Function<Set<Posn>, Integer> {
        @Override
        public Integer apply(Set<Posn> sequence) {
            return sequence.size();
        }
    }

    // Scores the given Q sequence, worth 6 points
    protected class ScoreQSequence implements Function<Set<Posn>, Integer> {
        @Override
        public Integer apply(Set<Posn> sequence) {
            return Q_BONUS;
        }
    }

    public static class PassAction extends TurnAction {

        protected boolean isLegalFor(GameState state) {
            return true;
        }

        protected void performCheckedOn(GameState state, PlayerState activePlayer, TurnResult.Builder turnResultBuilder) {
            // no side effects, active player is updated by performCheckedTurnAction
        }

        public JsonElement toJson() {
            return new JsonPrimitive("pass");
        }

        public boolean equals(Object other) {
            return other instanceof PassAction;
        }

        public int hashCode() {
            return PassAction.class.hashCode();
        }

        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    public static class ExchangeAction extends TurnAction {
        protected boolean isLegalFor(GameState state) {
            return state.isExchangeActionLegal();
        }

        protected void performCheckedOn(GameState state, PlayerState activePlayer, TurnResult.Builder turnResultBuilder) {
            state.checkedExchangeActivePlayerTiles(activePlayer, turnResultBuilder);
        }

        public JsonElement toJson() {
            return new JsonPrimitive("replace");
        }

        public boolean equals(Object other) {
            return other instanceof ExchangeAction;
        }

        public int hashCode() {
            return ExchangeAction.class.hashCode();
        }

        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    // Represents an immutable, nonempty sequence of placements
    // Maintains the invariant that the sequence of placements contained by this action
    // are all in the same row or column
    public static class PlaceAction extends TurnAction {
        protected List<Placement> placements;

        // Convenience constructor a PlaceAction with a single placement that 
        // can't break any of the imposed invariants
        public PlaceAction(Placement placement) {
            this.placements = new ArrayList<>(Arrays.asList(placement));
        }

        private PlaceAction(List<Placement> placements) {
            this.placements = placements;
        }

        protected boolean isLegalFor(GameState state) {
            return state.isPlaceActionLegal(this.placements);
        }

        protected void performCheckedOn(GameState state, PlayerState activePlayer, TurnResult.Builder turnResultBuilder) {
            state.checkedPlaceActivePlayerTiles(activePlayer, this.placements, turnResultBuilder);
        }

        public List<Placement> getPlacements() {
            return new ArrayList<>(this.placements);
        }

        public JsonElement toJson() {
            return Placements.toJson(this.placements);
        }

        public boolean equals(Object other) {
            return other instanceof PlaceAction && this.placements.equals(((PlaceAction) other).placements);
        }

        public int hashCode() {
            return Objects.hash(this.placements);
        }

        public String toString() {
            return String.format("PlaceAction - placements: %s", this.placements);
        }

        // Builder class for PlaceActions, maintains the same row/column invariant upon each additional placement
        // Returns an empty object if it contains an empty list of placements
        public static class Builder  {
            private final List<Placement> placements;

            public Builder() {
                this.placements = new ArrayList<Placement>();
            }

            // Adds the given placement to this action if the same row or column variant is held.
            // Does not add otherwise. Returns true if the placement succeeds, false otherwise
            public boolean addPlacement(Placement placement) {
                boolean validPlacement = false;
                List<Posn> placementPositions = Placements.getPositions(this.placements);
                placementPositions.add(placement.getPosition());

                if (Posns.allSameRowXorColumn(placementPositions)) {
                    this.placements.add(placement);
                    validPlacement = true;
                }

                return validPlacement;
            }
            
            public Optional<PlaceAction> tryBuild() {
                return this.placements.isEmpty() ? Optional.empty() : Optional.of(new PlaceAction(this.placements));
            }
        }
    }

    // Represents a (immutable, for now) possibly empty sequence of placements that
    // does NOT maintain the invariant that the sequence of placements contained by this action
    // are all in the same row or column
    public static class UncheckedPlaceAction extends PlaceAction {

        public UncheckedPlaceAction(List<Placement> placements) {
            super(new ArrayList<>(placements));
        }
    }
}

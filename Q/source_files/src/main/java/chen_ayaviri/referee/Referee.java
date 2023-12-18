package chen_ayaviri.referee;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.GameState;
import chen_ayaviri.common.PlayerState;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.common.TurnResult;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.player.Setup;
import chen_ayaviri.player.TakeTurn;
import chen_ayaviri.player.NewTiles;
import chen_ayaviri.player.Win;
import chen_ayaviri.server.CommunicationResult;
import chen_ayaviri.server.TimedCommunication;

import java.util.*;
import java.util.concurrent.*;

// Represents a referee that can run a game to completion given a sorted list of players
// NOTE: If a player misbehaves at any point during the game (including pre-game and post-game interactions),
// they will be eliminated
// Misbehavior includes breaking of the rules (logical), taking too long to respond, or exception
// raising (these two have to do with communication)
public class Referee {
    public final static int MINIMUM_PLAYERS = 2;
    public final static int MAXIMUM_PLAYERS = 4;
    private final int COPIES_PER_TILE = 30;
    private final int HAND_SIZE = 6;
    private final int COMMUNICATION_TIMEOUT_SECONDS;
    private GameState gameState;
    // NOTE: this MUST be initialised before any communication with a remote player
    private final List<String> eliminatedPlayers; 
    private final List<IObserver> observers;

    // Creates a referee with a list of player proxy/name pairs and a configuration.
    // NOTE: assumes each player has a unique name
    public Referee(List<IPlayer> players, RefereeConfig refereeConfig) {
        this(refereeConfig.getInitialState(), refereeConfig, players);
    }

    // Create a referee with an in-progress game state to continue playing from
    // and sets up IN-HOUSE players with the starting state
    public Referee(GameState gameState) {
        this(gameState, new RefereeConfig(), new ArrayList<>());
    }

    // Creates a referee with the GIVEN game state (NOT the one from the referee config) and the
    // remaining parameters from the given configuration. Injects the given list of player proxies IF 
    // NON-EMPTY. OTHERWISE, assumes that the game state already contains these proxies
    private Referee(GameState initialState, RefereeConfig refereeConfig, List<IPlayer> players) {
        this.COMMUNICATION_TIMEOUT_SECONDS = refereeConfig.getPerTurnTimeout();
        this.eliminatedPlayers = new ArrayList<String>();
        this.observers = new ArrayList<IObserver>();

        if (!players.isEmpty()) {
            // Must inject remote player proxies into the referee's game state
            this.gameState = new GameState(initialState, players);
        } else {
            this.gameState = initialState;
        }

        this.gameState.initializeScoringConstants(refereeConfig.getGameStateConfig());
        this.possiblyAddObserver(refereeConfig.hasObserverAttached());
        this.setupPlayers(this.gameState.getPlayerStates());
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    // Plays the game to completion from the referee's current game state information
    // Returns the result of the game
    public GameResult playToCompletion() {
        Optional<List<TurnAction>> roundTurnActions;

        do {
            roundTurnActions = this.playRound();
        } while (!this.gameEndedMidRound(roundTurnActions) && !this.isTerminalRound(roundTurnActions.get()));

        List<String> winningPlayers = this.alertPlayersOfFinalStatus();
        this.sendGameOverToObservers();

        return new GameResult(winningPlayers, this.eliminatedPlayers);
    }

    // Plays a complete round of n turns, where n is the number of players at the start of round
    // Returns the list of all turn actions taken during the round if the round ended successfully
    // Returns empty object if the game ended mid-round
    protected Optional<List<TurnAction>> playRound() {
        int numberOfTurns = this.gameState.getNumberOfPlayers();
        List<TurnAction> turnActions = new ArrayList<TurnAction>();


        for(int _ = 0; _ < numberOfTurns; _++) {
            // TODO: see if one of the call to isTerminalTurn can be removed
            Optional<TurnResult> potentialTurnResult = this.playTurn();

            if (potentialTurnResult.isPresent()) {
                TurnResult turnResult = potentialTurnResult.get();

                if (this.isTerminalTurn(turnResult)) {
                    return Optional.empty();
                } else {
                    turnActions.add(turnResult.getTurnAction());
                }
            }
        }

        return Optional.of(turnActions);
    }
   
    // Plays a turn for the active player, returning the result of the turn, empty
    // if no turn action was obtained from the player
    // NOTE: If a player breaks a game rule or raises an exception during the calls to takeTurn or newTiles,
    // the player is eliminated
    protected Optional<TurnResult> playTurn() {
        this.sendStateToObservers();
        ActivePlayerInfo activePlayerInfo = this.gameState.getInfoForActivePlayer();
        PlayerState activePlayerState = this.gameState.getActivePlayerState();
        CommunicationResult<TurnAction> turnActionCommunication = this.tryCommunication(
            new TakeTurn(activePlayerState.getProxy(), activePlayerInfo),
            activePlayerState.getName()
        );

        if (turnActionCommunication.hasSucceeded()) {
            TurnResult turnResult = this.playTurnWithAction(
                turnActionCommunication.returnValue().get(),
                activePlayerState
            );

            return Optional.of(turnResult);
        } else {
            return Optional.empty();
        }
    }

    protected TurnResult playTurnWithAction(TurnAction turnAction, PlayerState activePlayerState)  {
        if (this.gameState.checkLegalityOf(turnAction)) {
            TurnResult turnResult = this.gameState.performCheckedTurnAction(turnAction);

            if (!this.isTerminalTurn(turnResult)) {
                this.tryCommunication(
                    new NewTiles(activePlayerState.getProxy(), turnResult.getNewTiles()),
                    activePlayerState.getName()
                );
            }

            return turnResult;
        } else {
            this.eliminatePlayer(activePlayerState.getName());
            return new TurnResult.Builder(turnAction).build();
        }
    }

    // Adds an observer to this referee if the given flag is set to true
    protected void possiblyAddObserver(boolean shouldAdd) {
        if (shouldAdd) {
            this.addObserver(new Observer());
        }
    }

    // Constructs the entire shuffled list of tiles with which the game is played,
    // Constructs the players hands,
    // Initialises the map with the first referee tile,
    // Initialises the game state with the players, the map, and the remaining referee tiles
    // protected GameState constructNewGameState(List<Pair<String, IPlayerProxy>> players) {
    //     List<Tile> remainingTiles = this.initialiseTiles();
    //     Map<String, List<Tile>> playerHandMap = this.constructPlayerHands(players, remainingTiles);
    //     QMap map = this.initialiseMap(remainingTiles);
    //     return new GameState(players, playerHandMap, map, remainingTiles);
    // }

    // Constructs, shuffles, and returns the entire collection of tiles for the game
    // protected List<Tile> initialiseTiles() {
    //     Set<Tile> distinctTiles = Tile.getAllDistinct();
    //     List<Tile> allTiles = new ArrayList<>();

    //     for (Tile tile : distinctTiles) {
    //         allTiles.addAll(Collections.nCopies(this.COPIES_PER_TILE, tile));
    //     }

    //     Collections.shuffle(allTiles);

    //     return allTiles;
    // }

    // Constructs a map with the first tile in the given list of tiles
    // protected QMap initialiseMap(List<Tile> tiles) {
    //     return new QMap(tiles.remove(0));
    // }

    // Sets up each player in the given list with the given map and their tiles
    // NOTE: If a player raises an exception during the call to setup, the player is eliminated
    // OTHERWISE: the order of players in the game state rotated.
    // The final state of the gamestate should be that players are in order of descending age, with some
    // players possibly lost in the process
    protected void setupPlayers(List<PlayerState> playerStates) {
        for (PlayerState playerState : playerStates) {
            ActivePlayerInfo info = this.gameState.getInfoForActivePlayer();
            Callable<Void> setupCall = new Setup(playerState.getProxy(), info, info.getTiles());
            //NOTE: if the communication result has succeeded,
            //      then the player was not removed and the players are rotated.
            CommunicationResult<Void> result = this.tryCommunication(setupCall, playerState.getName());
            if (result.hasSucceeded()) {
                this.gameState.updateActivePlayer();
            }
        }
    }

    // Constructs and returns a map of player names to their initial hands, removing 
    // from the given list of tiles to do so
    // protected Map<String, List<Tile>> constructPlayerHands(List<Pair<String, IPlayerProxy>> playerNames, List<Tile> tiles) {
    //     Map<String, List<Tile>> playerHandMap = new HashMap<>();

    //     for (Pair<String, IPlayerProxy> playerName : playerNames) {
    //         String name = playerName.getFirst();
    //         List<Tile> playerHand = this.constructPlayerHand(tiles);
    //         playerHandMap.put(name, playerHand);
    //     }

    //     return playerHandMap;
    // }

    // Constructs and returns the hand for a single player
    // Assumes an already randomised list of tiles
    // Mutates the given list of tiles by removing the players hands from it
    private List<Tile> constructPlayerHand(List<Tile> remainingTiles) {
        List<Tile> playerHand = new ArrayList<>();

        for (int index = 0; index < this.HAND_SIZE; index++) {
            playerHand.add(remainingTiles.remove(0));
        }

        return playerHand;
    }

    // Eliminates the player with the given name from the game for breaking the rules
    protected void eliminatePlayer(String name) {
        this.gameState.removePlayer(name);
        this.eliminatedPlayers.add(name);
    }

    // Returns the list of the names of the winning players
    protected List<String> determineWinningPlayers() {
        List<String> winningPlayers = new ArrayList<String>();
        int maxScore = this.calculateMaxScore();

        for (PlayerState playerState : this.gameState.getPlayerStates()) {
            if (playerState.getScore() == maxScore) {
                winningPlayers.add(playerState.getName());
            }
        }

        return winningPlayers;
    }

    // Determines the winning players, communicates to each non-eliminated player whether they won or
    // lost, and returns the list of their names in alphabetically sorted order
    // NOTE: If a player raises an exception during the call to win, the player is eliminated
    protected List<String> alertPlayersOfFinalStatus() {
        List<String> winningPlayers = this.determineWinningPlayers();
        Collections.sort(winningPlayers);
       
        for (PlayerState playerState : this.gameState.getPlayerStates()) {
            String name = playerState.getName();
            boolean isWinner = winningPlayers.contains(name);
            Callable<Void> winCall = new Win(playerState.getProxy(),isWinner);
            CommunicationResult<Void> winCommunication = this.tryCommunication(winCall, name);

            // TODO: consider making a new punishment out of this to remove this if statement
            if (!winCommunication.hasSucceeded() && isWinner) {
                winningPlayers.remove(name);
            }
        }

        return winningPlayers;
    }

    // Returns true if the given object is empty, meaning the round was unfinished. See playRound
    protected boolean gameEndedMidRound(Optional<List<TurnAction>> turnActions) {
        return !turnActions.isPresent();
    }

    // Returns true if the given list of turn actions doesn't contain a place action
    // NOTE: Assumes that the given list is nonempty
    protected boolean isTerminalRound(List<TurnAction> turnActions) {
        for (TurnAction turnAction : turnActions) {
            if (turnAction instanceof GameState.PlaceAction) {
                return false;
            }
        }

        return true;
    }

    protected boolean isTerminalTurn(TurnResult turnResult) {
        return turnResult.placedAllTiles() || !this.hasRemainingPlayers();
    }

    protected boolean hasRemainingPlayers() {
        return this.gameState.getNumberOfPlayers() > 0;
    }

    // TODO: perhaps move into GameState ?
    protected int calculateMaxScore() {
        int maxScore = 0;

        for (PlayerState playerState : this.gameState.getPlayerStates()) {
            maxScore = Math.max(maxScore, playerState.getScore());
        }

        return maxScore;
    }

    // Attempts to communicate with the remote player by calling the given Callable 
    // Returns the result of the communication, see CommunicationResult interpretation statement
    protected <T> CommunicationResult<T> tryCommunication(Callable<T> callable, String name) {
        TimedCommunication<T> timedCommunication = new TimedCommunication<T>(
            callable, 
            this.COMMUNICATION_TIMEOUT_SECONDS, 
            new PlayerRemovalPunishment(this, name)
        );

        return timedCommunication.attempt();
    }

    // Sends the current game state to each of observers of this referee
    // NOTE: assumes that the observers are server side and thus cannot misbehave 
    // _in the same way_ that remote players can
    protected void sendStateToObservers() {
        for (IObserver observer : this.observers) {
            observer.receive(new GameState(this.gameState));
        }
    }

    protected void sendGameOverToObservers() {
        this.sendStateToObservers();

        for (IObserver observer : this.observers) {
            observer.gameOver();
        }
    }
}

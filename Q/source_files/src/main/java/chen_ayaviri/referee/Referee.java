package chen_ayaviri.referee;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.GameState;
import chen_ayaviri.common.PlayerState;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.common.TurnResult;
import chen_ayaviri.common.DebuggingLogger;
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
import java.util.function.Supplier;

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
    // NOTE: This MUST be initialised before any communication with a remote player
    private final List<String> eliminatedPlayers; 
    private final List<IObserver> observers;

    private final DebuggingLogger logger;

    // Creates a referee with a list of player proxy/name pairs and a configuration.
    // NOTE: Assumes each player has a unique name
    public Referee(List<IPlayer> players, RefereeConfig refereeConfig) {
        this(refereeConfig.getInitialState(), refereeConfig, players);
    }

    // Create a referee with an in-progress game state to continue playing from
    // and sets up IN-HOUSE players with the starting state. This constructor is 
    // for testing purposes
    public Referee(GameState gameState) {
        this(gameState, new RefereeConfig(), new ArrayList<>());
    }

    // Creates a referee with the GIVEN game state (NOT the one from the referee config) and the
    // remaining parameters from the given configuration. Injects the given list of player proxies IF 
    // NON-EMPTY. OTHERWISE, assumes that the game state already contains these proxies
    private Referee(GameState initialState, RefereeConfig refereeConfig, List<IPlayer> players) {
        this.logger = refereeConfig.getLogger();
        this.COMMUNICATION_TIMEOUT_SECONDS = refereeConfig.getPerTurnTimeout();
        this.eliminatedPlayers = new ArrayList<String>();
        this.observers = new ArrayList<IObserver>();
        this.setInitialGameState(initialState, players);
        this.gameState.initializeScoringConstants(refereeConfig.getGameStateConfig());
        this.possiblyAddObserver(refereeConfig.hasObserverAttached());
        this.setupPlayers(this.gameState.getPlayerStates());
    }

    public void addObserver(IObserver observer) {
        this.observers.add(observer);
    }

    // Plays a game until it is over, alerts the remaining players of their win/lose 
    // status, and returns the result of the game
    public GameResult playToCompletion() {
        this.playGame();
        List<String> winningPlayers = this.alertPlayersOfFinalStatus();
        this.sendGameOverToObservers();

        return new GameResult(winningPlayers, this.eliminatedPlayers);
    }

    // Plays a game until it is over, accumulating a list of each player's score in this
    // referee's game state and a list of eliminated players in this referee
    protected void playGame() {
        Optional<List<TurnAction>> roundTurnActions;
        RoundResult roundResult;

        do {
            roundResult = this.playRound();
        } while (!this.isTerminalRound(roundResult));
    }

    // Plays a complete round of n turns, where n is the number of players at the start of round
    // Returns the result of the round
    protected RoundResult playRound() {
        int numberOfTurns = this.gameState.getNumberOfPlayers();
        boolean hadGameEndingTurn = false;
        List<TurnAction> turnActions = new ArrayList<TurnAction>();

        for (int _ = 0; _ < numberOfTurns; _++) {
            Optional<TurnResult> potentialTurnResult = this.playTurn();

            if (this.hasTurnAction(potentialTurnResult)) {
                TurnResult turnResult = potentialTurnResult.get();

                this.logger.println(String.format("Is this turn a terminal turn: %s", turnResult));

                if (this.isTerminalTurn(turnResult)) {
                    this.logger.println("Previous turn resulted in the end of the game");

                    hadGameEndingTurn = true;
                    break;
                } else {
                    turnActions.add(turnResult.getTurnAction());
                }
            }
        }

        return new RoundResult(hadGameEndingTurn, turnActions);
    }
   
    // Plays a turn for the active player, returning the result of the turn, empty
    // if no turn action was obtained from the player
    protected Optional<TurnResult> playTurn() {
        this.sendStateToObservers();
        ActivePlayerInfo activePlayerInfo = this.gameState.getInfoForActivePlayer();
        PlayerState activePlayerState = activePlayerInfo.getPlayerState();

        this.logger.println(String.format("It's %s's turn", activePlayerState.getName()));

        CommunicationResult<TurnAction> turnActionCommunication = this.attemptTimedCommunication(
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
            this.logger.println(String.format("Could not retrieve %s's turn action, they will be eliminated", activePlayerState.getName()));

            return Optional.empty();
        }
    }

    // Plays the given turn action for the given active player and 
    // returns its result, checking the legality of the turn before doing so
    protected TurnResult playTurnWithAction(TurnAction turnAction, PlayerState activePlayerState)  {
        this.logger.println(String.format("%s responded with the following turn action: %s", activePlayerState.getName(), turnAction));

        if (this.gameState.checkLegalityOf(turnAction)) {
            TurnResult turnResult = this.playLegalTurnWithAction(turnAction, activePlayerState);
            return turnResult;
        } else {
            this.logger.println(String.format("An illegal move was played, %s will be eliminated", activePlayerState.getName()));

            this.eliminatePlayer(activePlayerState.getName());
            return new TurnResult.Builder(turnAction).build();
        }
    }

    // Plays the given LEGAL turn action for the given active player and
    // returns its result. If the turn does not end the game, this method gives the player their
    // new tiles and advances the player queue to point to the next active player 
    protected TurnResult playLegalTurnWithAction(TurnAction turnAction, PlayerState activePlayerState) {
        TurnResult turnResult = this.gameState.performCheckedTurnAction(turnAction);

        if (!this.isTerminalTurn(turnResult)) {
            this.logger.println(String.format("Turn action was successful and does not end the game by itself, giving %s their new tiles", activePlayerState.getName()));

            // TODO: The specification does not seem to suggest that a player that performs a pass action
            // gets their NewTiles method called
            this.attemptTimedCommunicationWithReward(
                new NewTiles(activePlayerState.getProxy(), turnResult.getNewTiles()),
                activePlayerState.getName(),
                new PlayerQueueAdvancement(this)
            );
        }

        return turnResult;
    }

    protected void advancePlayerQueue() {
        this.gameState.advancePlayerQueue();
    }

    // Sets the given state to this referee's initial one, injecting the list of players into the
    // state if it is non-empty
    protected void setInitialGameState(GameState initialState, List<IPlayer> players) {
        if (players.isEmpty()) {
            this.gameState = initialState;
        } else {
            // Must inject remote player proxies into the referee's game state
            this.gameState = new GameState(initialState, players);
        }
    }

    // Adds an observer to this referee if the given flag is set to true
    protected void possiblyAddObserver(boolean shouldAdd) {
        if (shouldAdd) {
            this.addObserver(new Observer());
        }
    }

    // Sets up each player in the given list with this referee's game state's 
    // initial map and each player's tiles, advancing the game state's player queue
    // in order to obtain the correct set of tiles 
    protected void setupPlayers(List<PlayerState> playerStates) {
        for (PlayerState playerState : playerStates) {
            ActivePlayerInfo activePlayerInfo = this.gameState.getInfoForActivePlayer();
            CommunicationResult<Void> setupResult = this.attemptTimedCommunicationWithReward(
                new Setup(playerState.getProxy(), activePlayerInfo, activePlayerInfo.getTiles()), 
                playerState.getName(),
                new PlayerQueueAdvancement(this)
            );
        }
    }

    // Eliminates the player with the given name from the game for breaking the rules
    protected void eliminatePlayer(String name) {
        this.gameState.removePlayer(name);
        this.eliminatedPlayers.add(name);
    }

    // Returns the list of the names of the winning players
    protected List<String> determineWinningPlayers() {
        int maxScore = this.calculateMaxScore();
        List<String> winners = this.getPlayerNamesWithScore(maxScore);

        return winners;
    }

    // Returns a list of names corresponding to players in the game with the given score
    protected List<String> getPlayerNamesWithScore(int score) {
        List<String> playerNames = new ArrayList<>();

        for (PlayerState playerState : this.gameState.getPlayerStates()) {
            if (playerState.getScore() == score) {
                playerNames.add(playerState.getName());
            }
        }

        return playerNames;
    }

    // Determines the winning players, communicates to each non-eliminated player whether they won or
    // lost, and returns the list of their names
    protected List<String> alertPlayersOfFinalStatus() {
        List<String> winners = this.determineWinningPlayers();

        this.logger.println(String.format("Winners before telling them: %s", winners));
       
        for (PlayerState playerState : this.gameState.getPlayerStates()) {
            boolean isWinner = winners.contains(playerState.getName());
            CommunicationResult<Void> winCommunication = this.attemptTimedCommunicationWithPunishment(
                new Win(playerState.getProxy(), isWinner), 
                new MisbehavedFinisherPunishment(this, winners, playerState.getName())
            );
        }

        this.logger.println(String.format("Winners after telling them: %s", winners));

        return winners;
    }

    // Returns true if the given RoundResult summarises a round that ends the game
    protected boolean isTerminalRound(RoundResult roundResult) {
        return roundResult.hadGameEndingTurn() || this.hasNoPlaceActions(roundResult.getTurnActions());
    }

    // Returns true if the given list of turn actions doesn't contain a place action
    // NOTE: Assumes that the given list is nonempty
    protected boolean hasNoPlaceActions(List<TurnAction> turnActions) {
        for (TurnAction turnAction : turnActions) {
            if (turnAction instanceof GameState.PlaceAction) {
                return false;
            }
        }

        return true;
    }

    // Returns true if the given TurnResult is present
    protected boolean hasTurnAction(Optional<TurnResult> turnResult) {
        return turnResult.isPresent();
    }

    // Returns true if all tiles were placed during the turn or if the game has no remaining players
    protected boolean isTerminalTurn(TurnResult turnResult) {
        return turnResult.placedAllTiles() || !this.hasRemainingPlayers();
    }

    protected boolean hasRemainingPlayers() {
        return this.gameState.getNumberOfPlayers() > 0;
    }

    protected int calculateMaxScore() {
        int maxScore = 0;

        for (PlayerState playerState : this.gameState.getPlayerStates()) {
            maxScore = Math.max(maxScore, playerState.getScore());
        }

        return maxScore;
    }

    // Executes the given callable, which represents a communication attempt with a remote player, with a 
    // timeout limit defined by this referee. The failure callback defaults to removal of the player with
    // the given name. Returns the result of the communication
    protected <T> CommunicationResult<T> attemptTimedCommunication(Callable<T> callable, String name) {
        TimedCommunication<T> timedCommunication = new TimedCommunication.Builder<T>(
            callable, 
            this.COMMUNICATION_TIMEOUT_SECONDS
        ).failureCallback(
            new PlayerRemovalPunishment(this, name)
        ).build();

        return timedCommunication.attempt();
    }

    // Same as above, but executes the given failure callback instead of defaulting to player removal
    protected <T> CommunicationResult<T> attemptTimedCommunicationWithPunishment(Callable<T> callable, Supplier<Void> failureCallback) {
        TimedCommunication<T> timedCommunication = new TimedCommunication.Builder<T>(
            callable, 
            this.COMMUNICATION_TIMEOUT_SECONDS
        ).failureCallback(failureCallback).build();

        return timedCommunication.attempt();
    }

    // Same as attemptTimedCommunication, but adds the given success callback, which is executed upon success
    protected <T> CommunicationResult<T> attemptTimedCommunicationWithReward(Callable<T> callable, String name, Supplier<Void> successCallback) {
        TimedCommunication<T> timedCommunication = new TimedCommunication.Builder<T>(
            callable, 
            this.COMMUNICATION_TIMEOUT_SECONDS
        ).failureCallback(
            new PlayerRemovalPunishment(this, name)
        ).successCallback(successCallback).build();

        return timedCommunication.attempt();
    }

    // Sends the current game state to each of observers of this referee
    // NOTE: Assumes that the observers are server side and thus cannot misbehave 
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

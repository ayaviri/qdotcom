package chen_ayaviri.server;

import chen_ayaviri.player.IPlayer;
import chen_ayaviri.referee.GameResult;
import chen_ayaviri.referee.Referee;
import chen_ayaviri.referee.RefereeConfig;
import chen_ayaviri.common.DebuggingLogger;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Represents a server that accepts player sign-ups via TCP and runs a game of Q
public class Server {
    private ServerSocket serverSocket;
    private final RefereeConfig refereeConfig;
    private final List<Closeable> openedSockets;

    private final DebuggingLogger logger;

    private final int NUMBER_WAITING_ROUNDS;
    private final int WAITING_PERIOD_SECONDS;
    private final int NAME_SUBMISSION_PERIOD_SECONDS;

    // TODO: How should the connection be abstracted over to make it testable (in theory) ?
    public Server(ServerConfig serverConfig) {
        this.createServerSocket(serverConfig.getPortNumber());
            
        this.refereeConfig = serverConfig.getRefereeConfig();
        this.openedSockets = new ArrayList<Closeable>(Arrays.asList(this.serverSocket));
        this.logger = serverConfig.getLogger();
        this.NUMBER_WAITING_ROUNDS = serverConfig.getNumberOfWaitingRounds();
        this.WAITING_PERIOD_SECONDS = serverConfig.getWaitingPeriodInSeconds();
        this.NAME_SUBMISSION_PERIOD_SECONDS = serverConfig.getConfirmationPeriodInSeconds();
    }

    // Waits for player sign up and runs a game if the minimum number of players have joined
    public GameResult runGameToCompletion() {
        GameResult gameResult = new GameResult(new ArrayList<>(), new ArrayList<>());
        List<IPlayer> players = this.waitForSignUps();

        // TODO: This constant ties the server and referee components... Can we avoid doing so ? If so, how ?
        if (this.hasEnoughPlayersForGame(players.size())) {
            this.logger.println(String.format("Starting game with %s", players));

            Referee referee = new Referee(players, this.refereeConfig);
            gameResult = referee.playToCompletion();
        }

        this.closeAllOpenPlayerSockets();    
        return gameResult;
    }

    // Returns the created list of player proxies after waiting enough periods to get the minimum
    // number of players or after waiting for the maximum number of periods
    // NOTE: Adds ALL opened player sockets to this server's collection to close upon shutdown
    protected List<IPlayer> waitForSignUps() {
        int waitingRoundsCompleted = 0;
        List<IPlayer> players = new ArrayList<>();

        do {
            this.logger.println("Starting waiting period");

            this.executeWaitingPeriod(players);
            waitingRoundsCompleted += 1;
        } while (this.canSignUpMorePlayers(players.size()) && this.canWaitAnotherRound(waitingRoundsCompleted));

        return players;
    }

    // Executes a waiting period that populates the given list of players with new ones that sign up
    // during it
    protected void executeWaitingPeriod(List<IPlayer> players) {
        new TimedCommunication.Builder<>(
            new WaitingPeriod(
                this.serverSocket, 
                players, 
                this.openedSockets, 
                this.NAME_SUBMISSION_PERIOD_SECONDS, 
                this.logger
            ),
            this.WAITING_PERIOD_SECONDS
        ).build().attempt();
    }

    // Returns true if the given number of players is at least as big as the minimum
    // needed to start game
    protected boolean hasEnoughPlayersForGame(int numberOfPlayers) {
        return numberOfPlayers >= Referee.MINIMUM_PLAYERS;
    }

    // Returns true if the given number of players is less than the maximum number as 
    // defined in the referee
    protected boolean canSignUpMorePlayers(int numberOfPlayers) {
        return numberOfPlayers < Referee.MINIMUM_PLAYERS;
    }

    // Returns true if the given number of waiting rounds completed is less than the 
    // maximum number of waiting rounds this server will do
    protected boolean canWaitAnotherRound(int waitingRoundsCompleted) {
        return waitingRoundsCompleted < this.NUMBER_WAITING_ROUNDS;
    }

    // Closes all of the open player sockets accumulated by this server and the server socket
    protected void closeAllOpenPlayerSockets() {
        for (Closeable socket : this.openedSockets) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO: This should probably just be logged, the OS will close them for us anyway
            }
        }
    }

    // Creates a new server socket with the given port number and initialises this server's socket to it
    protected void createServerSocket(int portNumber) {
        try {
            this.serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialise server");
        }
    }
}

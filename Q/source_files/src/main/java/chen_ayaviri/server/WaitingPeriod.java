package chen_ayaviri.server;

import chen_ayaviri.player.IPlayer;
import chen_ayaviri.player.PlayerProxy;
import chen_ayaviri.referee.Referee;
import chen_ayaviri.common.DebuggingLogger;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

// Represents a function object that gathers remote players over TCP during a 
// waiting period. ACCUMULATES these players onto the given list
public class WaitingPeriod implements Callable<Void> {
    private final ServerSocket serverSocket;
    // NOTE: this FOLLOWING TWO lists are references to the lists given by the server as they server
    // as accumulators
    private final List<IPlayer> players;
    private final List<Closeable> openedSockets;

    private final int NAME_SUBMISSION_PERIOD_SECONDS; 

    private final DebuggingLogger logger;

    // Constructs a new WaitingPeriod using the given socket to accept new connections and
    // the given list of players to accumulate them onto 
    public WaitingPeriod(
        ServerSocket serverSocket, 
        List<IPlayer> players, 
        List<Closeable> openedSockets, 
        int nameSubmissionPeriodSeconds, 
        DebuggingLogger logger
    ) {
        this.serverSocket = serverSocket;
        this.players = players;
        this.openedSockets = openedSockets;
        this.NAME_SUBMISSION_PERIOD_SECONDS = nameSubmissionPeriodSeconds;
        this.logger = logger;
    }

    public Void call() {
        while (this.players.size() < Referee.MAXIMUM_PLAYERS) {
            Optional<IPlayer> possiblePlayer = this.getNewPlayerProxy();

            if (possiblePlayer.isPresent()) {
                this.players.add(possiblePlayer.get());
            }
        }

        return null;
    }

    // Performs the following:
    // 1) Accepts a new player connection 
    // 2) Adds the socket representing this connection to the accumulator stored in this
    // function object
    // 3) Awaits for their name to be sent within NAME_SUBMISSION_PERIOD_SECONDS
    // 4) Constructs and returns a player proxy
    //
    // Returns an empty object any of these operations fail
    protected Optional<IPlayer> getNewPlayerProxy() {
        try {
            Socket playerSocket = this.serverSocket.accept();

            this.logger.println("New player connection received");

            this.openedSockets.add(playerSocket);
            CommunicationResult<String> nameCommunicationResult = this.attemptToGetPlayerName(playerSocket);

            if (nameCommunicationResult.hasSucceeded()) {
                return this.constructNamedPlayerProxy(nameCommunicationResult, playerSocket);
            }
        } catch (IOException ignored) {}

        return Optional.empty();
    }

    // Attempts to the get a player's name through the given socket using this WaitingPeriod's timeout
    protected CommunicationResult<String> attemptToGetPlayerName(Socket playerSocket) {
        TimedCommunication<String> timedCommunication = new TimedCommunication.Builder<>(
            new NameSubmission(playerSocket),
            this.NAME_SUBMISSION_PERIOD_SECONDS
        ).build();
        CommunicationResult<String> nameCommunicationResult = timedCommunication.attempt();

        return nameCommunicationResult;
    }

    // Constructs a new PlayerProxy from the given CommunicationResult containing the player's name
    // and the socket for communcation with the remote player. Returns empty if the instantiation fails
    // NOTE: Assumes that the CommunicationResult has succeeded
    protected Optional<IPlayer> constructNamedPlayerProxy(CommunicationResult<String> nameCommunicationResult, Socket playerSocket) {
        String name = nameCommunicationResult.returnValue().get();

        this.logger.println(String.format("Player sign up successful, received name %s", name));

        try {
            return Optional.of(new PlayerProxy(name, playerSocket));
        } catch (IOException e) {
            return Optional.empty();
        }

    }
}

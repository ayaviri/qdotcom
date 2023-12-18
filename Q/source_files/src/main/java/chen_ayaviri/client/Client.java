package chen_ayaviri.client;

import com.google.gson.JsonPrimitive;
import chen_ayaviri.player.IPlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Represents a client capable of connecting a collection of clients to the server via TCP
public class Client {
    private final String serverHostName;
    private final int serverPortNumber;
    private final int secondsBetweenSpawns;
    private final List<IPlayer> players;

    public Client(ClientConfig clientConfig) {
        this.serverHostName = clientConfig.getHostName();
        this.serverPortNumber = clientConfig.getPortNumber();
        this.secondsBetweenSpawns = clientConfig.getSecondsBetweenSpawns();
        this.players = clientConfig.getPlayers();
    }

    public void registerClients() {
        CountDownLatch latch = new CountDownLatch(this.players.size());
        ExecutorService executor = Executors.newFixedThreadPool(this.players.size());

        for (int index = 0; index < this.players.size(); index++) {
            executor.execute(
                new PlayerSpawn(this.players.get(index), latch)
            );

            this.possibleWaitBeforeNextSpawn(index);
        }

        this.awaitCompletionOfPlayers(latch);
    }

    // Sleeps the current thread _secondsBetweenSpawns_ seconds if the given index does 
    // NOT correspond to the index of the last player in this Client's list
    protected void possibleWaitBeforeNextSpawn(int playerIndex) {
        if (playerIndex != this.players.size() - 1) {
            try {
                Thread.sleep(
                        TimeUnit.MILLISECONDS.convert((long) this.secondsBetweenSpawns, TimeUnit.SECONDS)
                );
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }

        }
    }

    // Waits on the given latch's countdown (blocking call) to hit zero, throwing
    // an exception of if the current thread is interrupted while the countdown hits zero
    protected void awaitCompletionOfPlayers(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Current thread was interrupted while waiting");
        }
    }

    // Represents a runnable task that:
    // 1) Creates a referee proxy
    // 2) Sends the given player's name to the server
    // 3) Plays a game with the given player
    // 4) Decreases the given latch's countdown when the game is over for the given player
    public class PlayerSpawn implements Runnable {
        private final IPlayer player;
        private CountDownLatch latch;

        public PlayerSpawn(IPlayer player, CountDownLatch latch) {
            this.player = player;
            this.latch = latch;
        }

        // NOTE: Creation of the referee proxy is done here as opposed to in the constructor 
        // as the socket connection required for it can fail
        public void run() {

                RefereeProxy refereeProxy = this.createRefereeProxy(); 
                refereeProxy.writeOut(new JsonPrimitive(this.player.name()));
                refereeProxy.playToCompletion();


            this.latch.countDown();
        }

        // Creates a socket that connects to the hostname and port number in the surrounding
        // Client class, using it and this task's player to create and return a referee proxy
        protected RefereeProxy createRefereeProxy() {
            // TODO: add connection retry functionality upon failure, removed the declaration
            // of the checked exception when this is done
            try {
                Socket serverSocket = new Socket();
                do {
                    serverSocket = new Socket(serverHostName, serverPortNumber);
                    Thread.sleep(1000);
                } while (!serverSocket.isConnected());

                return new RefereeProxy(serverSocket, this.player);
            } catch (IOException | InterruptedException e) {
                throw new IllegalArgumentException();
            }
        }
    }
}

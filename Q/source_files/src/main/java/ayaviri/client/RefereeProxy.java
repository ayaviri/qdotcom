package ayaviri.client;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.JsonWriter;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.Tile;
import ayaviri.map_representation.Tiles;
import ayaviri.player.IPlayer;
import ayaviri.player.NewTiles;
import ayaviri.player.Setup;
import ayaviri.player.TakeTurn;
import ayaviri.player.Win;
import ayaviri.server.CommunicationResult;
import ayaviri.server.TimedCommunication;
import ayaviri.utils.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

// Represents the remote referee communicating over TCP
public class RefereeProxy {
    private final IPlayer player;
    private final JsonWriter writerOut;
    private final JsonStreamParser parserIn;
    private final InputStream
            inputStream; // TODO: Why is this here ? Is it so that this proxy can close it ?
    private final JsonElement VOID_JSON_RESPONSE = new JsonPrimitive("void");
    private final int PLAYER_TIMEOUT_SECONDS = 7;

    public RefereeProxy(Socket serverSocket, IPlayer player) throws IOException {
        this.player = player;
        this.writerOut = new JsonWriter(serverSocket.getOutputStream());
        this.parserIn = new JsonStreamParser(new InputStreamReader(serverSocket.getInputStream()));
        this.inputStream = serverSocket.getInputStream();
    }

    public void playToCompletion() {
        while (this.parserIn.hasNext()) {
            JsonElement requestJson = this.parserIn.next();
            JsonElement responseJson = this.processRequest(requestJson);
            this.writeOut(responseJson);
        }
    }

    // Deserializes the given JSON request, sends it to this proxy's player, and
    // returns the serialized response as JSON
    protected JsonElement processRequest(JsonElement request) {
        Pair<String, JsonArray> deserialisedRequest = this.deserialiseRequest(request);
        JsonElement response = this.handleAndSerialiseRequest(deserialisedRequest);

        return response;
    }

    // Deserialises the given request (represented as a two element array in which the first
    // is the name of the method called on the player and the second is an array of arguments).
    // Returns as a pair of string method name and argument array
    protected Pair<String, JsonArray> deserialiseRequest(JsonElement request) {
        Iterator<JsonElement> requestIterator = request.getAsJsonArray().iterator();
        // TODO: exception handling as a result of invalid JSON sent by server ?
        String methodName = requestIterator.next().getAsString();
        JsonArray arguments = requestIterator.next().getAsJsonArray();

        return new Pair(methodName, arguments);
    }

    // Converts the deserialised request into a method invocation on this referee proxy's player,
    // retrieving the results, serialising them back to JSON, and returning them
    protected JsonElement handleAndSerialiseRequest(Pair<String, JsonArray> deserialisedRequest) {
        String methodName = deserialisedRequest.getFirst();
        JsonArray arguments = deserialisedRequest.getSecond();

        // If execution gets as far as retrieving the result from the CommunicationResult, it has
        // succeeded, as the failure callback in invokePlayerWithTimeout interrupts the current
        // thread
        switch (methodName) {
            case "setup":
                ActivePlayerInfo setupState =
                        ActivePlayerInfo.fromJson(arguments.get(0).getAsJsonObject());
                List<Tile> setupTiles = Tiles.fromJson(arguments.get(1).getAsJsonArray());
                // this.player.setup(setupActivePlayerInfo, setupTiles);
                this.invokePlayerWithTimeout(new Setup(this.player, setupState, setupTiles));
                return this.VOID_JSON_RESPONSE;
            case "take-turn":
                ActivePlayerInfo takeTurnState =
                        ActivePlayerInfo.fromJson(arguments.get(0).getAsJsonObject());
                CommunicationResult<TurnAction> communicationResult =
                        this.invokePlayerWithTimeout(new TakeTurn(this.player, takeTurnState));
                // TurnAction turnAction = this.player.takeTurn(takeTurnActivePlayerInfo);
                TurnAction turnAction = communicationResult.returnValue().get();
                return turnAction.toJson();
            case "new-tiles":
                List<Tile> newTilesTiles = Tiles.fromJson(arguments);
                this.invokePlayerWithTimeout(new NewTiles(this.player, newTilesTiles));
                // this.player.newTiles(newTilesTiles);
                return this.VOID_JSON_RESPONSE;
            case "win":
                boolean won = arguments.get(0).getAsBoolean();
                this.invokePlayerWithTimeout(new Win(this.player, won));
                // this.player.win(won);
                return this.VOID_JSON_RESPONSE;
            default:
                throw new RuntimeException("Invalid method name given from server");
        }
    }

    // Invokes the given player callable with the timeout limit specified by this referee.
    // Upon failure, the current thread is interrupted (as though the game "finished" for
    // this referee proxy's player)
    protected <T> CommunicationResult<T> invokePlayerWithTimeout(Callable<T> callable) {
        Supplier<Void> currentThreadInterruption =
                () -> {
                    Thread.currentThread().interrupt();
                    return null;
                };
        TimedCommunication<T> timedTask =
                new TimedCommunication.Builder<T>(callable, this.PLAYER_TIMEOUT_SECONDS)
                        .failureCallback(currentThreadInterruption)
                        .build();

        return timedTask.attempt();
    }

    protected void writeOut(JsonElement jsonElement) {
        this.writerOut.write(jsonElement);
        this.writerOut.flush();
    }
}

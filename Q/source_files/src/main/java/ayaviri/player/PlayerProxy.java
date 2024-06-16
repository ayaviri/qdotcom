package ayaviri.player;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.JsonSerializable;
import ayaviri.common.JsonWriter;
import ayaviri.common.TurnAction;
import ayaviri.map_representation.Tile;
import com.google.gson.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.function.Function;

// Represents a remote player communicating via TCP
// Player API methods use blocking reads from the socket's input stream,
// this is what the Referee relies on to time out the player if necessary.
public class PlayerProxy implements IPlayer {
    private final String name;
    private final JsonWriter writerOut;
    private final JsonStreamParser parserIn;
    private final Function<JsonElement, Void> verifyVoidResponse =
            (jsonElement) -> {
                if (jsonElement.isJsonPrimitive()
                        && jsonElement.getAsJsonPrimitive().getAsString().equals("void")) {
                    return null;
                }

                throw new RuntimeException("Expected void response");
            };

    public PlayerProxy(String name, Socket playerSocket) throws IOException {
        this.name = name;
        this.writerOut = new JsonWriter(playerSocket.getOutputStream());
        this.parserIn = new JsonStreamParser(new InputStreamReader(playerSocket.getInputStream()));
    }

    public String name() {
        return this.name;
    }

    public void setup(ActivePlayerInfo state, List<Tile> tiles) {
        this.communicateWithRemote(new Setup(this, state, tiles), this.verifyVoidResponse);
    }

    public void newTiles(List<Tile> tiles) {
        this.communicateWithRemote(new NewTiles(this, tiles), this.verifyVoidResponse);
    }

    public TurnAction takeTurn(ActivePlayerInfo currentState) {
        return this.communicateWithRemote(
                new TakeTurn(this, currentState), response -> TurnAction.fromJson(response));
    }

    // NOTE: The closing of input and output streams is done in the referee, so this RemotePlayer
    // does not need to worry about closing them here
    public void win(boolean win) {
        this.communicateWithRemote(new Win(this, win), this.verifyVoidResponse);
    }

    // Serializes the given Player API function object into JSON, writes it to the output stream,
    // awaits a response on the input stream, and returns the validated + deserialized respsonse
    protected <T> T communicateWithRemote(
            JsonSerializable method, Function<JsonElement, T> deserializationFunction) {
        this.writeOut(method.toJson());

        if (this.parserIn.hasNext()) {
            JsonElement response = this.parserIn.next();
            return deserializationFunction.apply(response);
        } else {
            throw new RuntimeException("Could not read next JSON element");
        }
    }

    protected void writeOut(JsonElement jsonElement) {
        this.writerOut.write(jsonElement);
        this.writerOut.flush();
    }

    public String toString() {
        return String.format("{Player: %s}", this.name());
    }

    @Override
    public JsonElement toJson() {
        return null;
    }
}

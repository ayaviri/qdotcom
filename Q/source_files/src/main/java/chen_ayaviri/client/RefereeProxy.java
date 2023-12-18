package chen_ayaviri.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.TurnAction;
import chen_ayaviri.common.JsonWriter;
import chen_ayaviri.player.IPlayer;
import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Tiles;

import java.io.IOException;
import java.io.InputStreamReader;
// import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

// Represents the remote referee communicating over TCP
public class RefereeProxy {
    private final IPlayer player;
    private final JsonWriter writerOut;
    private final JsonStreamParser parserIn;
    private final JsonElement VOID_JSON_RESPONSE = new JsonPrimitive("void");

    public RefereeProxy(Socket serverSocket, IPlayer player) throws IOException {
        this.writerOut = new JsonWriter(serverSocket.getOutputStream());
        this.parserIn = new JsonStreamParser(
            new InputStreamReader(serverSocket.getInputStream())
        );
        this.player = player;
    }

    public void playToCompletion() {
        while (this.parserIn.hasNext()) {
            JsonElement requestJson = this.parserIn.next();
            JsonElement responseJson = this.processRequest(requestJson);
            this.writeOut(responseJson);
        }

        // TODO: do both streams need to be closed here ?
        this.writerOut.close();
    }

    // Deserializes the given JSON request, sends it to this proxy's player, and
    // returns the serialized response as JSON
    protected JsonElement processRequest(JsonElement request) {
        Iterator<JsonElement> requestIterator = request.getAsJsonArray().iterator();
        // TODO: exception handling as a result of invalid JSON sent by server ?
        String methodName = requestIterator.next().getAsString();
        JsonArray arguments = requestIterator.next().getAsJsonArray();

        switch (methodName) {
            case "setup":
                ActivePlayerInfo setupActivePlayerInfo = ActivePlayerInfo.fromJson(arguments.get(0).getAsJsonObject());
                List<Tile> setupTiles = Tiles.fromJson(arguments.get(1).getAsJsonArray());
                this.player.setup(setupActivePlayerInfo, setupTiles);
                return this.VOID_JSON_RESPONSE;
            case "take-turn":
                ActivePlayerInfo takeTurnActivePlayerInfo = ActivePlayerInfo.fromJson(arguments.get(0).getAsJsonObject());
                TurnAction turnAction = this.player.takeTurn(takeTurnActivePlayerInfo);
                return turnAction.toJson();
            case "new-tiles":
                List<Tile> newTilesTiles = Tiles.fromJson(arguments);
                this.player.newTiles(newTilesTiles);
                return this.VOID_JSON_RESPONSE;
            case "win":
                boolean won = arguments.get(0).getAsBoolean();
                this.player.win(won);
                return this.VOID_JSON_RESPONSE;
            default:
                throw new RuntimeException("Invalid method name given from server");
        }
    }

    protected void writeOut(JsonElement jsonElement) {
        this.writerOut.write(jsonElement);
        this.writerOut.flush();
    }
}

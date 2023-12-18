package chen_ayaviri.common;

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;

import chen_ayaviri.utils.Pair;


public class XLegal {
    public static void main(String[] args) {
        /*
        Pair<JsonObject, JsonArray> input = getInput(System.in);

        // create data from json input
        JsonObject jPub = input.getKey();
        QMap map = QMap.fromJson(jPub.get("map").getAsJsonArray());
        int remainingRefereeTiles = jPub.get("tile*").getAsInt();
        Players players = Players.fromJson(jPub.get("players").getAsJsonArray());
        List<Placement> placements = Placements.fromJson(input.getValue());
        GameState gameState = new GameState(map, players, new ArrayList<>(remainingRefereeTiles)); // empty referee tiles
        int originalNumPlayers = players.getOrdering().size();
        TurnAction action = new GameState.PlaceAction(placements);

        // execute test action
        if (gameState.checkLegalityOf(action)) {
            gameState.performCheckedTurnAction(action);
        } else {
            gameState.removeActivePlayer();
        }

        // analyze result
        JsonElement output = new JsonPrimitive(false);
        ActivePlayerInfo activePlayerInfo = gameState.getInfoForActivePlayer();
        int updatedNumberOfPlayers = activePlayerInfo.getPlayerScores().size();
        boolean legalPlacement = updatedNumberOfPlayers == originalNumPlayers;
        if (legalPlacement) {
            Map<Posn, Tile> updatedMap = activePlayerInfo.getMap().getTiles();
            output = toJson(updatedMap);
        }

        System.out.println(output.toString());
        */
    }

    // Returns the JPub, JPlacements (array) pair from the input test file
    public static Pair<JsonObject, JsonArray> getInput(InputStream in) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(in));
        JsonObject jPub = parser.next().getAsJsonObject();
        JsonArray jPlacements = parser.next().getAsJsonArray();
        return new Pair<JsonObject, JsonArray>(jPub, jPlacements);
    }

}

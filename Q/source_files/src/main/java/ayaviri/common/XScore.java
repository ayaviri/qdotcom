package ayaviri.common;

import static ayaviri.map_representation.Placements.fromJson;

import ayaviri.map_representation.Placement;
import ayaviri.utils.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XScore {
    public static void main(String[] args) {
        Pair<JsonArray, JsonArray> input = getInput(System.in);
        // create data from json input
        JsonArray jMap = input.getFirst();
        QMap map = QMap.fromJson(jMap);
        Players players = new Players(new ArrayList<>(), new HashMap<>());
        List<Placement> placements = fromJson(input.getSecond());
        GameState gameState = new GameState(map, players, new ArrayList<>()); // empty referee tiles

        // analyze result
        int score = gameState.scorePlacement(placements, placements.size() + 1);
        JsonElement output = new JsonPrimitive(score);

        System.out.println(output.toString());
    }

    // Returns the JMap, JPlacements (array) pair from the input test file
    public static Pair<JsonArray, JsonArray> getInput(InputStream in) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(in));
        JsonArray jMap = parser.next().getAsJsonArray();
        JsonArray jPlacements = parser.next().getAsJsonArray();
        return new Pair<JsonArray, JsonArray>(jMap, jPlacements);
    }
}

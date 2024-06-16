package ayaviri.common;

import ayaviri.strategy.Strategy;
import ayaviri.utils.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XStrategy {

    public static void main(String[] args) {
        Pair<JsonObject, JsonPrimitive> input = getInput(System.in);
        ActivePlayerInfo publicKnowledge = ActivePlayerInfo.fromJson(input.getFirst());
        Strategy strategy = Strategy.fromJson(input.getSecond().getAsJsonPrimitive().getAsString());
        TurnAction turnAction = strategy.computeTurnAction(publicKnowledge);
        System.out.println(turnAction.toJson().toString());
    }

    public static Pair<JsonObject, JsonPrimitive> getInput(InputStream in) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(in));
        JsonObject jPub = parser.next().getAsJsonObject();
        JsonPrimitive jStrategy = parser.next().getAsJsonPrimitive();
        return new Pair<JsonObject, JsonPrimitive>(jPub, jStrategy);
    }
}

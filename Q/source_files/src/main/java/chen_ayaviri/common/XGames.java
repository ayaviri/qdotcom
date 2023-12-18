package chen_ayaviri.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import chen_ayaviri.referee.GameResult;
import chen_ayaviri.referee.Referee;

import java.io.InputStreamReader;

public class XGames {
    public static void main(String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonObject jState = parser.next().getAsJsonObject();
        JsonArray jActors = parser.next().getAsJsonArray();

        GameState gameState = GameState.fromJson(jState, jActors);
        Referee referee = new Referee(gameState);
        GameResult gameResult = referee.playToCompletion();

        System.out.println(GameResult.toJson(gameResult));
    }
}

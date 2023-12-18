package chen_ayaviri.common;

import chen_ayaviri.referee.Referee;
import chen_ayaviri.referee.Observer;
import chen_ayaviri.referee.GameResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;

public class XGamesWithObserver {

    public static void main(String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
        JsonObject jState = parser.next().getAsJsonObject();
        JsonArray jActorSpecA = parser.next().getAsJsonArray();
        GameState gameState = GameState.fromJson(jState, jActorSpecA);

        Referee referee = new Referee(gameState);

        if (hasShowFlag(args)) {
            Observer observer = new Observer();
            referee.addObserver(observer);
        }

        GameResult gameResult = referee.playToCompletion();
        System.out.println(GameResult.toJson(gameResult));
    }

    public static boolean hasShowFlag(String[] args) {
        return args.length >= 1 && args[0].equals("--show");
    }
}

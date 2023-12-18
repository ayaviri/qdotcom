package chen_ayaviri.referee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.List;

// Represents the results of a game: a pair of the names of the winning players and the names of the eliminated players
public class GameResult {
    private final List<String> winningPlayers;
    private final List<String> eliminatedPlayers;

    // As of 6, game result is only constructed by the referee
    public GameResult(List<String> winningPlayers, List<String> eliminatedPlayers) {
        this.winningPlayers = winningPlayers;
        this.eliminatedPlayers = eliminatedPlayers;
    }

    public static JsonElement toJson(GameResult gameResult) {
        JsonArray output = new JsonArray();
        JsonArray winnerJNames = new JsonArray();
        JsonArray eliminatedJNames = new JsonArray();

        List<String> winningPlayers = gameResult.getWinningPlayers();
        List<String> eliminatedPlayers = gameResult.getEliminatedPlayers();
        Collections.sort(winningPlayers);
        winningPlayers.forEach(name -> winnerJNames.add(name));
        eliminatedPlayers.forEach(name -> eliminatedJNames.add(name));
        output.add(winnerJNames);
        output.add(eliminatedJNames);
        return output;
    }

    public List<String> getWinningPlayers() {
        return this.winningPlayers;
    }

    public List<String> getEliminatedPlayers() {
        return this.eliminatedPlayers;
    }
}

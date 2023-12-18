package chen_ayaviri.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

// Represents a scoring configuration for a game's Q and end-of-game bonus values
public class GameStateConfig {
    private int Q_BONUS = 4;
    private int END_OF_GAME_BONUS = 8;

    // Constructs a configuration with default values
    public GameStateConfig() {}

    public GameStateConfig(int qBonus, int endOfGameBonus) {
        this.Q_BONUS = qBonus;
        this.END_OF_GAME_BONUS = endOfGameBonus;
    }

    public int getQBonus() {
        return this.Q_BONUS;
    }

    public int getEndOfGameBonus() {
        return this.END_OF_GAME_BONUS;
    }

    public static GameStateConfig fromJson(JsonElement gameStateConfigJson) {
        JsonObject json = gameStateConfigJson.getAsJsonObject();
        int qBonus = json.get("qbo").getAsInt();
        int endOfGameBonus = json.get("fbo").getAsInt();

        return new GameStateConfig(qBonus, endOfGameBonus);
    }

    public JsonElement toJson() {
        JsonObject o = new JsonObject();
        o.add("qbo", new JsonPrimitive(this.getQBonus()));
        o.add("fbo", new JsonPrimitive(this.END_OF_GAME_BONUS));
        return o;
    }
}

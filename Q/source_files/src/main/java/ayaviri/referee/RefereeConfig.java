package ayaviri.referee;

import ayaviri.common.ADebuggableConfig;
import ayaviri.common.GameState;
import ayaviri.common.GameStateConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class RefereeConfig extends ADebuggableConfig {
    private GameState initialState;
    private GameStateConfig gameStateConfig = new GameStateConfig();
    private int PER_TURN_TIMEOUT_SECONDS = 6;
    private boolean hasObserverAttached = false;

    // Constructs an empty configuration with the necessary default value(s)
    public RefereeConfig() {
        super();
    }

    public RefereeConfig(
            GameState initialState,
            boolean quiet,
            GameStateConfig gameStateConfig,
            int perTurnTimeout,
            boolean hasObserverAttached) {
        super(quiet);
        this.initialState = initialState;
        this.gameStateConfig = gameStateConfig;
        this.PER_TURN_TIMEOUT_SECONDS = perTurnTimeout;
        this.hasObserverAttached = hasObserverAttached;
    }

    // Reference to the game state used during initialisation
    public GameState getInitialState() {
        return this.initialState;
    }

    public GameStateConfig getGameStateConfig() {
        return this.gameStateConfig;
    }

    public int getPerTurnTimeout() {
        return this.PER_TURN_TIMEOUT_SECONDS;
    }

    public boolean hasObserverAttached() {
        return this.hasObserverAttached;
    }

    public static RefereeConfig fromJson(JsonElement refereeConfigJson) {
        JsonObject json = refereeConfigJson.getAsJsonObject();
        GameState initialState = GameState.fromJson(json.get("state0"));
        boolean quiet = json.get("quiet").getAsBoolean();
        GameStateConfig gameStateConfig = GameStateConfig.fromJson(json.get("config-s"));
        int perTurnTimeout = json.get("per-turn").getAsInt();
        boolean hasObserverAttached = json.get("observe").getAsBoolean();

        return new RefereeConfig(
                initialState, quiet, gameStateConfig, perTurnTimeout, hasObserverAttached);
    }

    public JsonElement toJson() {
        JsonObject refconfig = new JsonObject();
        refconfig.add("state0", this.initialState.toJson());
        refconfig.add("quiet", new JsonPrimitive(super.isQuiet()));
        refconfig.add("config-s", this.gameStateConfig.toJson());
        refconfig.add("per-turn", new JsonPrimitive(this.PER_TURN_TIMEOUT_SECONDS));
        refconfig.add("oberve", new JsonPrimitive(this.hasObserverAttached));
        return refconfig;
    }
}

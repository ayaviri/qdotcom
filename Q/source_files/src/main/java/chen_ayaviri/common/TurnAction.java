package chen_ayaviri.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import chen_ayaviri.map_representation.Placement;
import chen_ayaviri.map_representation.Placements;

import java.util.List;

// Represents a possible turn action in the Q game. Abstract class chosen over
// interface in order to protect functionality from other components (namely referee)
public abstract class TurnAction implements JsonSerializable {

    // Determines the legality of this turn action on the given game state
    protected abstract boolean isLegalFor(GameState state);

    // Performs this action on the given game state for the given active player, populating
    // the given turn result builder object
    // NOTE: Must be called AFTER the legality of the move is checked on the given game state
    // NOTE: Does NOT set the legality of the turn in the builder to true since that is the 
    // assumption going in to this method
    protected abstract void performCheckedOn(GameState state, TurnResult.Builder turnResultBuilder);

    // Constructs one of PassAction, ExchangeAction, or an UncheckedPlaceAction from the given jChoice.
    public static TurnAction fromJson(JsonElement jChoice) {
        if (jChoice.isJsonPrimitive()) {
            String passOrReplace = jChoice.getAsString();
            switch (passOrReplace) {
                case "pass": return new GameState.PassAction();
                case "replace": return new GameState.ExchangeAction();
            }
            throw new IllegalArgumentException("bad jChoice string json");
        } else {
            JsonArray jPlacements = jChoice.getAsJsonArray();
            List<Placement> placements = Placements.fromJson(jPlacements);
            return new GameState.UncheckedPlaceAction(placements);
        }
    }
}

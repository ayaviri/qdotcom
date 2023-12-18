package chen_ayaviri.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.JsonSerializable;
import chen_ayaviri.common.TurnAction;

import java.util.concurrent.Callable;

// A function object that represents a call to the given player proxy's 
// takeTurn method with the given ActivePlayerInfo object
public class TakeTurn implements Callable<TurnAction>, JsonSerializable {
    private final IPlayer playerProxy;
    private final ActivePlayerInfo activePlayerInfo;

    public TakeTurn(IPlayer playerProxy, ActivePlayerInfo activePlayerInfo) {
        this.playerProxy = playerProxy;
        this.activePlayerInfo = activePlayerInfo;
    }

    public TurnAction call() {
        return this.playerProxy.takeTurn(this.activePlayerInfo);
    }


    // TODO: consider abstracting across all function objects that represent Player API methods
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(new JsonPrimitive("take-turn"));
        JsonArray arguments = new JsonArray();
        arguments.add(this.activePlayerInfo.toJson());
        json.add(arguments);
        return json;
    }
}

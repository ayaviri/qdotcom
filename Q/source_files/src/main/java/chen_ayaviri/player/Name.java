package chen_ayaviri.player;

import com.google.gson.JsonPrimitive;
import chen_ayaviri.common.JsonSerializable;

import com.google.gson.JsonElement;
import java.util.concurrent.Callable;

// A function object that represents a call to the given player proxy's 
// name method with the given map and list of tiles
public class Name implements Callable<String>, JsonSerializable {
    private final IPlayer playerProxy;

    public Name(IPlayer playerProxy) {
        this.playerProxy = playerProxy;
    }

    public String call() {
        return this.playerProxy.name();
    }

    // This function object does not need to convert itself to JSON, so
    // a dummy value is returned here
    public JsonElement toJson() {
        return new JsonPrimitive(0);
    }
}

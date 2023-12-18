package chen_ayaviri.player;

import com.google.gson.JsonArray;
import chen_ayaviri.common.JsonSerializable;

import java.util.concurrent.Callable;

// A function object that represents a call to the given player proxy's 
// win method with the given boolean
public class Win implements Callable<Void>, JsonSerializable {
    private final IPlayer playerProxy;
    private final boolean didWin;

    public Win(IPlayer playerProxy, boolean didWin) {
        this.playerProxy = playerProxy;
        this.didWin = didWin;
    }

    public Void call() {
        this.playerProxy.win(didWin);
        return null;
    }

    public JsonArray toJson() {
        JsonArray json = new JsonArray();
        json.add("win");
        JsonArray arguments = new JsonArray();
        arguments.add(this.didWin);
        json.add(arguments);
        return json;
    }


}

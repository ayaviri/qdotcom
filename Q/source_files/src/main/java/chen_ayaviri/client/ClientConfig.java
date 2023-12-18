package chen_ayaviri.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import chen_ayaviri.common.ADebuggableConfig;
import chen_ayaviri.player.IPlayer;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientConfig extends ADebuggableConfig {
    private final int portNumber;
    private final String hostName;
    private final int secondsBetweenSpawns;
    private final List<IPlayer> players;

    public ClientConfig(
        int portNumber, 
        String hostName, 
        int secondsBetweenSpawns, 
        boolean quiet, 
        List<IPlayer> players
    ) {
        super(quiet);
        this.portNumber = portNumber;
        this.hostName = hostName;
        this.secondsBetweenSpawns = secondsBetweenSpawns;
        this.players = new ArrayList<IPlayer>(players);
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getSecondsBetweenSpawns() {
        return this.secondsBetweenSpawns;
    }

    public List<IPlayer> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public static ClientConfig fromJson(JsonElement clientConfigJson) {
        JsonObject json = clientConfigJson.getAsJsonObject();
        int portNumber = json.get("port").getAsInt();
        String hostName = json.get("host").getAsString();
        int secondsBetweenSpawns = json.get("wait").getAsInt();
        boolean quiet = json.get("quiet").getAsBoolean();

        List<IPlayer> players = new ArrayList<>();
        Iterator<JsonElement> playersIterator = json.get("players").getAsJsonArray().iterator();

        while (playersIterator.hasNext()) {
            players.add(
                    IPlayer.fromJson(playersIterator.next().getAsJsonArray())
            );
        }

        return new ClientConfig(
            portNumber,
            hostName,
            secondsBetweenSpawns,
            quiet,
            players
        );
    }

    public JsonElement toJson() {
        JsonObject o = new JsonObject();
        o.add("port", new JsonPrimitive(this.portNumber));
        o.add("host", new JsonPrimitive(this.hostName));
        o.add("wait", new JsonPrimitive(this.secondsBetweenSpawns));
        o.add("port", new JsonPrimitive(super.isQuiet()));
        JsonArray a = new JsonArray();
        for(IPlayer p : this.players) {
            a.add(p.toJson());
        }
        o.add("players", a);
        return o;
    }
}

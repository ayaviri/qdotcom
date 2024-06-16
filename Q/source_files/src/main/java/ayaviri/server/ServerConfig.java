package ayaviri.server;

import ayaviri.common.ADebuggableConfig;
import ayaviri.referee.RefereeConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ServerConfig extends ADebuggableConfig {
    private int portNumber;
    // The number of rounds the server waits for during the sign-up stage
    private int numberOfWaitingRounds = 2;
    // The duration of each waiting period with the sign-up stage
    private int waitingPeriodInSeconds = 20;
    // The duration of the sign up confirmation period (between client connection and client
    // confirmation)
    private int confirmationPeriodInSeconds = 3;
    private RefereeConfig refereeConfig;

    // Constructs a new ServerConfig with the defaults defined above
    public ServerConfig() {
        super();
    }

    public ServerConfig(
            int portNumber,
            int numberOfWaitingRounds,
            int waitingPeriodInSeconds,
            int confirmationPeriodInSeconds,
            boolean quiet,
            RefereeConfig refereeConfig) {
        super(quiet);
        this.portNumber = portNumber;
        this.numberOfWaitingRounds = numberOfWaitingRounds;
        this.waitingPeriodInSeconds = waitingPeriodInSeconds;
        this.confirmationPeriodInSeconds = confirmationPeriodInSeconds;
        this.refereeConfig = refereeConfig;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public int getNumberOfWaitingRounds() {
        return this.numberOfWaitingRounds;
    }

    public int getWaitingPeriodInSeconds() {
        return this.waitingPeriodInSeconds;
    }

    public int getConfirmationPeriodInSeconds() {
        return this.confirmationPeriodInSeconds;
    }

    public RefereeConfig getRefereeConfig() {
        return this.refereeConfig;
    }

    public static ServerConfig fromJson(JsonElement serverConfigJson) {
        JsonObject json = serverConfigJson.getAsJsonObject();
        int portNumber = json.get("port").getAsInt();
        int numberOfWaitingRounds = json.get("server-tries").getAsInt();
        int waitingPeriodInSeconds = json.get("server-wait").getAsInt();
        int confirmationPeriodInSeconds = json.get("wait-for-signup").getAsInt();
        boolean quiet = json.get("quiet").getAsBoolean();
        RefereeConfig refereeConfig = RefereeConfig.fromJson(json.get("ref-spec"));

        return new ServerConfig(
                portNumber,
                numberOfWaitingRounds,
                waitingPeriodInSeconds,
                confirmationPeriodInSeconds,
                quiet,
                refereeConfig);
    }

    public JsonElement toJson() {
        JsonObject serverConfigJson = new JsonObject();
        serverConfigJson.add("port", new JsonPrimitive(this.portNumber));
        serverConfigJson.add("server-tries", new JsonPrimitive(this.numberOfWaitingRounds));
        serverConfigJson.add("server-wait", new JsonPrimitive(this.waitingPeriodInSeconds));
        serverConfigJson.add(
                "wait-for-signup", new JsonPrimitive(this.confirmationPeriodInSeconds));
        serverConfigJson.add("quiet", new JsonPrimitive(super.isQuiet()));
        serverConfigJson.add("ref-spec", this.refereeConfig.toJson());
        return serverConfigJson;
    }
}

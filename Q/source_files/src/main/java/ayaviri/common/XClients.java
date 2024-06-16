package ayaviri.common;

import ayaviri.client.Client;
import ayaviri.client.ClientConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XClients {
    public static void main(String[] args) {
        workhorse(System.in, args);
    }

    protected static void workhorse(InputStream inputStream, String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(inputStream));
        JsonObject clientConfigJson = getInputsFrom(parser, args);

        ClientConfig clientConfig = ClientConfig.fromJson(clientConfigJson);
        Client client = new Client(clientConfig);
        client.registerClients();
    }

    // Gets the client config JSON from the given parser, replacing the port number in it with the
    // one from
    // the given list of (command line) args
    protected static JsonObject getInputsFrom(JsonStreamParser parser, String[] args) {
        JsonObject clientConfigJson = parser.next().getAsJsonObject();
        int inputtedPortNumber = getArgsFrom(args);
        clientConfigJson.add("port", new JsonPrimitive(inputtedPortNumber));

        return clientConfigJson;
    }

    protected static int getArgsFrom(String[] args) {
        if (args.length == 1) {
            return Integer.valueOf(args[0]);
        } else {
            throw new RuntimeException(
                    "Not given the correct number of command line arguments, expects port number");
        }
    }
}

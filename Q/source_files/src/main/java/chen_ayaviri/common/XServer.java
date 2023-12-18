package chen_ayaviri.common;

import chen_ayaviri.server.ServerConfig;
import chen_ayaviri.server.Server;
import chen_ayaviri.referee.GameResult;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.google.gson.JsonStreamParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class XServer {
    public static void main(String[] args) {
        workhorse(System.in, System.out, args);
    }

    protected static void workhorse(InputStream inputStream, PrintStream printStream, String[] args) {
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(inputStream));
        JsonObject serverConfigJson = getInputsFrom(parser, args);

        ServerConfig serverConfig = ServerConfig.fromJson(serverConfigJson);
        Server server = new Server(serverConfig);
        GameResult gameResult = server.runGameToCompletion();

        printStream.println(GameResult.toJson(gameResult));
    }

    // Gets the server config JSON from the given parser, replacing the port number in it with the one from
    // the given list of (command line) args
    protected static JsonObject getInputsFrom(JsonStreamParser parser, String[] args) {
        JsonObject serverConfigJson = parser.next().getAsJsonObject();
        int inputtedPortNumber = getArgsFrom(args);
        serverConfigJson.add("port", new JsonPrimitive(inputtedPortNumber));
        
        return serverConfigJson;
    }

    protected static int getArgsFrom(String[] args) {
        if (args.length == 1) {
            return Integer.valueOf(args[0]);
        } else {
            throw new RuntimeException("Not given the correct number of command line arguments, expects port number");
        }
    }
}

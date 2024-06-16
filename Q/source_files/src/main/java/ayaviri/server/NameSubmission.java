package ayaviri.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Callable;

// NOTE: An alternative design could have been to make a new RemotePlayer subclass
// that requests the name of a player remotely, has a serialisation function that returns
// an empty string, and a deserialisation function that validates a JName. The advantage
// here is that we reuse the communicateWithRemote function, the RemotePlayer constructor,
// and the Win callable
//
// Represents the task of waiting for a remote player's name through a TCP connection
public class NameSubmission implements Callable<String> {
    private final Socket playerSocket;

    public NameSubmission(Socket playerSocket) {
        this.playerSocket = playerSocket;
    }

    public String call() throws IOException {
        JsonStreamParser jsonParser =
                new JsonStreamParser(new InputStreamReader(playerSocket.getInputStream()));

        if (jsonParser.hasNext()) {
            return this.validateAndReturnJName(jsonParser.next());
        } else {
            throw new RuntimeException("Connection interrupted during player sign-up");
        }
    }

    protected String validateAndReturnJName(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else {
            throw new RuntimeException("JName given was not a primitive");
        }
    }
}

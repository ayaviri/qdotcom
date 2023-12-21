package chen_ayaviri.common;

import com.google.gson.JsonElement;

import java.io.PrintWriter;
import java.io.OutputStream;

// Represents a writer to a given output stream that exclusively writes JsonElements
public class JsonWriter {
    private final int MINIMUM_CHARACTERS_NEEDED = 6;
    private final PrintWriter writer;

    public JsonWriter(OutputStream outputStream) {
        this.writer = new PrintWriter(outputStream, true);
    }
    
    public void write(JsonElement jsonElement) {
        String string = jsonElement.toString();
        string = this.possiblyPadMessage(string);

        this.writer.write(string);
    }

    public void flush() {
        this.writer.flush();
    }

    public void close() {
        this.writer.close();
    }

    protected String possiblyPadMessage(String s) {
        if (s.length() < this.MINIMUM_CHARACTERS_NEEDED) {
            return String.format("%1$-" + this.MINIMUM_CHARACTERS_NEEDED + "s", s);
        }
        return s;
    }
}

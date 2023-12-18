package chen_ayaviri.common;

import com.google.gson.JsonElement;

// Represents an object that can be converted to JSON
public interface JsonSerializable {
    // Converts this object to JSON
    JsonElement toJson();
}

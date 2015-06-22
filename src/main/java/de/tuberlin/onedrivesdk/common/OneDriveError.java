package de.tuberlin.onedrivesdk.common;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Data object for json transport
 */
public class OneDriveError {
    InnerError error;

    public static OneDriveError parseError(String json)  throws ParseException{
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(json);
        if (root.containsKey("error")) {
            Gson gson = new Gson();
            return gson.fromJson(json, OneDriveError.class);
        }

        return null;
    }

    @Override
    public String toString() {
        return "Error: " + error;
    }
}

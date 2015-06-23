package de.tuberlin.onedrivesdk.drive;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.common.OneDriveError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of the OneDrive Interface
 */
public class ConcreteOneDrive implements OneDrive {
    protected String id;
    protected ConcreteOneDriveSDK api;
    protected String driveType;
    protected DriveOwner owner;
    protected DriveQuota quota;
    protected String rawJson = "";
	private static final Logger logger = LogManager.getLogger(ConcreteOneDrive.class);

    private ConcreteOneDrive() {
    }

    public static List<OneDrive> parseDrivesFromJson(String json) throws OneDriveException, ParseException {
        OneDriveError error;
        if ((error = OneDriveError.parseError(json)) != null) {
            throw new OneDriveException(error.toString());
        }

        JSONParser parser = new JSONParser();
        JSONObject root = null;

        try {
            root = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
        	logger.warn("Something failed while parsing Json {}",e.getMessage());
            logger.debug("Exception while parsing",e);
        }

        JSONArray values = (JSONArray) root.get("value");
        json = values.toJSONString();

        Gson gson = new Gson();
        List<OneDrive> oneDrives = gson.fromJson(json, new TypeToken<List<ConcreteOneDrive>>() {
        }.getType());

        return oneDrives;
    }

    public static ConcreteOneDrive fromJSON(String json) throws ParseException, OneDriveException {
        OneDriveError error;
        if ((error = OneDriveError.parseError(json)) != null) {
            throw new OneDriveException(error.toString());
        }

        Gson gson = new Gson();
        ConcreteOneDrive drive = gson.fromJson(json, ConcreteOneDrive.class);
        return drive.setRawJson(json);
    }

    public OneDrive setApi(ConcreteOneDriveSDK api) {
        this.api = api;
        return this;
    }

    public ConcreteOneDrive setRawJson(String rawJson) {
        this.rawJson = rawJson;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDriveType() {
        return driveType;
    }

    @Override
    public DriveUser getUser() {
        return owner.user;
    }

    @Override
    public DriveQuota getQuota() {
        return quota;
    }

    @Override
    public String toString() {
        return "Drive: "+ id + " - " + driveType + " " + owner;
    }

    @Override
    public OneFolder getRootFolder() throws IOException, OneDriveException {
		return api.getRootFolder(this);
	}

    @Override
    public String getRawJson() {
        return rawJson;
    }
}

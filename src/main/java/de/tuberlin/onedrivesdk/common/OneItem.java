package de.tuberlin.onedrivesdk.common;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.drive.DriveUser;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.ConcreteOneFolder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * The root class of all files and folder types that can be accessed through this sdk.
 * @author timmeey
 */
public abstract class OneItem {

    /**
     * The SDK object.
     */
    protected ConcreteOneDriveSDK api;

    /**
     * The OneDrive id of the resource.
     */
    protected String id = "";

    /**
     * The Name.
     */
    protected String name = "";

    /**
     * The created by reference. Possible keys are 'user', 'application' and 'device'.
     */
    protected HashMap<String, DriveUser> createdBy = new HashMap<>();

    /**
     * The creation timestamp of this item.
     */
    protected String createdDateTime;

    /**
     * The modified by reference. Possible keys are 'user', 'application' and 'device'.
     */
    protected HashMap<String, DriveUser> lastModifiedBy = new HashMap<>();

    /**
     * The last modified timestamp of this item.
     */
    protected String lastModifiedDateTime = "";

    /**
     * The cTag.
     */
    protected String cTag = "";

    /**
     * The eTag.
     */
    protected String eTag = "";

    /**
     * The size of an item in bytes.
     */
    protected long size = 0;

    /**
     * URL that displays the resource in the browser.
     */
    protected String webUrl = "";

    /**
     * The parent folder reference.
     */
    protected ParentReference parentReference;

    /**
     * The raw JSON which is received from the OneDrive API.
     */
    protected String rawJson = "";

    /**
     * A Url that can be used to download this file's content.
     */
    @SerializedName("@content.downloadUrl")
    protected String downloadUrl;

    /**
     * A timestamp of the last refresh.
     */
    private long lastRefresh;

    /**
     * Parse a OneItem object from JSON.
     *
     * @param json JSON from the OneDrive API
     * @return OneItem
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the JSON contains an OneDrive Error object from the API
     */
    public static OneItem fromJSON(String json) throws ParseException, OneDriveException {
        JSONObject root = getJsonObject(json);

        OneDriveError error;
        if ((error = OneDriveError.parseError(json)) != null) {
            throw new OneDriveException(error.toString());
        }

        Gson gson = new Gson();
        if (root.containsKey("file")) {
            return gson.fromJson(json, ConcreteOneFile.class).setLastRefresh(System.currentTimeMillis());
        } else {
            return gson.fromJson(json, ConcreteOneFolder.class).setLastRefresh(System.currentTimeMillis());
        }
    }

    /**
     * Parse a List of OneItems from JSON.
     *
     * @param json JSON from the OneDrive API
     * @return a List of OneItems
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the JSON contains an OneDrive Error object from the API
     */
    public static List<OneItem> parseItemsFromJson(String json) throws ParseException, OneDriveException {
        return OneItem.parseItemsFromJson(json, OneItemType.ALL);
    }

    /**
     * Parse a List of OneItems from JSON.
     *
     * @param json JSON from the OneDrive API
     * @param type OneItemType, can be used to define which type of items should be parsed
     * @return items from json
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the json dose not contain a 'value' attribute
     */
    public static List<OneItem> parseItemsFromJson(String json, OneItemType type) throws ParseException, OneDriveException {
        ArrayList<OneItem> itemList = new ArrayList<>();

        JSONObject root = getJsonObject(json);

        if (root.containsKey("value")) {
            JSONArray values = (JSONArray) root.get("value");
            for (Object object : values) {
                JSONObject itemJson = (JSONObject) object;
                OneItem item = OneItem.fromJSON(itemJson.toJSONString());
                switch (type) {
                    case FILE:
                        if (item instanceof OneFile) itemList.add(item);
                        break;
                    case FOLDER:
                        if (item instanceof OneFolder) itemList.add(item);
                        break;
                    case ALL:
                        itemList.add(item);
                }

            }
        } else {
            throw new OneDriveException("Cannot parse items from JSON. Missing argument 'value'.");
        }

        return itemList;
    }

    /**
     * Parse JSON from string and return the JSONObject
     *
     * @param json from the OneDrive API
     * @return JSONObject
     * @throws ParseException if the JSON can not be parsed
     */
    private static JSONObject getJsonObject(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject root;
        root = (JSONObject) parser.parse(json);
        return root;
    }

    /**
     * Gets the name of the item.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the api object.
     *
     * @param api the api object
     * @return the identity
     * @throws OneDriveException if the api is null
     */
    public OneItem setApi(ConcreteOneDriveSDK api) throws OneDriveException {
        if (api == null) {
            throw new OneDriveException("The provided api object can not be null!");
        }
        this.api = api;
        return this;
    }

    /**
     * Gets the id of the item.
     *
     * @return id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Delete the item.
     *
     * @return true if the item was deleted from OneDrive.
     * @throws IOException
     * @throws OneDriveException
     */
    public boolean delete() throws IOException, OneDriveException {
        return this.api.deleteItem(this);
    }

    /**
     * Gets the cTag.
     *
     * @return cTag
     */
    public String getCTag() {
        return this.cTag;
    }

    /**
     * Gets the eTag.
     *
     * @return eTag
     */
    public String getETag() {
        return this.eTag;
    }

    /**
     * The created by reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return created by
     */
    public HashMap<String, DriveUser> getCreatedBy() {
        return this.createdBy;
    }

    /**
     * The creation timestamp of this item.
     *
     * @return unix formatted timestamp
     */
    public long getCreatedDateTime() {
        try {
            return this.parseTimestamp(this.createdDateTime).getTime() / 1000;
        } catch (java.text.ParseException e) {
            return 0;
        }
    }

    /**
     * The modified by reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return last modified by
     */
    public HashMap<String, DriveUser> getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    /**
     * The last modified timestamp of this item.
     *
     * @return unix formatted timestamp
     */
    public long getLastModifiedDateTime() {
        try {
            return this.parseTimestamp(this.lastModifiedDateTime).getTime() / 1000;
        } catch (java.text.ParseException e) {
            return 0;
        }
    }

    /**
     * Parse a timestamp.
     *
     * @param dateTime Format: 0000-00-00T00:00:00
     * @return timestamp
     * @throws java.text.ParseException if the date can not be parsed
     */
    private Date parseTimestamp(String dateTime) throws java.text.ParseException {
        if (dateTime != null && dateTime.indexOf('.') != -1) {
            dateTime = dateTime.substring(0, dateTime.indexOf('.'));
            DateFormat df = new SimpleDateFormat("y-M-d'T'H:m:s");
            return df.parse(dateTime);
        } else {
            throw new java.text.ParseException(dateTime, -1);
        }
    }

    /**
     * Gets the size of this item in bytes.
     *
     * @return size
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Gets the URL that displays the resource in the browser.
     *
     * @return web url
     */
    public String getWebUrl() {
        return this.webUrl;
    }

    /**
     * Gets the parent folder.
     *
     * @return parent folder
     * @throws IOException
     * @throws OneDriveException
     */
    public OneFolder getParentFolder() throws IOException, OneDriveException {
        return api.getFolderById(this.parentReference.id);
    }

    /**
     * Gets the raw JSON which is received from the OneDrive API.
     *
     * @return raw json
     */
    public String getRawJson() {
        return rawJson;
    }

    /**
     * Sets the raw json.
     *
     * @param rawJson json
     * @return raw json
     */
    public OneItem setRawJson(String rawJson) {
        this.rawJson = rawJson;
        return this;
    }

    /**
     * Gets the timestamp of the last refresh.
     *
     * @return timestamp in milliseconds
     */
    public long getLastRefresh() {
        return lastRefresh;
    }

    /**
     * Sets the timestamp of the last refresh.
     *
     * @param lastRefresh timestamp in milliseconds
     * @return last refresh
     */
    public OneItem setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
        return this;
    }

    /**
     * Refresh the item state.
     *
     * @return item the reference of this item will be another one.
     * @throws IOException
     * @throws OneDriveException
     */
    public OneItem refreshItem() throws IOException, OneDriveException {
        if(this instanceof OneFile){
            return (OneItem) api.getFileById(id);
        } else {
            return (OneItem) api.getFolderById(id);
        }
    }

    /**
     * Is file.
     *
     * @return boolean
     */
    public abstract boolean isFile();

    /**
     * Is folder.
     *
     * @return boolean
     */
    public abstract boolean isFolder();
}

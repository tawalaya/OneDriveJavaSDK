package de.tuberlin.onedrivesdk.folder;


import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.common.ConflictBehavior;
import de.tuberlin.onedrivesdk.common.OneItemType;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.uploadFile.ConcreteOneUploadFile;
import de.tuberlin.onedrivesdk.uploadFile.OneUploadFile;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Here goes everything that is needed for every resource
 * Like all the communication with the server and handling the requests and parsing them to simple JSONObjects
 * The childClasses then will form themselves according to the JSONObject by picking what they need and ignoring what isn't relevant for them
 * Like If this item represents a folder, a folder child will use the JSONObject with all the folder data to fill it's own fields
 *
 * @author timmeey
 */
public class ConcreteOneFolder extends OneItem implements OneFolder {

    private FolderProperty folder;

    private ConcreteOneFolder() {
    }

    public static ConcreteOneFolder fromJSON(String json) throws ParseException, OneDriveException {
        return (ConcreteOneFolder) OneItem.fromJSON(json).setRawJson(json);
    }

    @Override
    public OneFolder getParentFolder() throws IOException, OneDriveException {
        return super.getParentFolder();
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public OneFolder refresh() throws OneDriveException, IOException {
        return (OneFolder) super.refreshItem();
    }

    @Override
    public List<OneFolder> getChildFolder() throws IOException, OneDriveException {
        return api.getChildFolder(this);
    }

    @Override
    public List<OneFile> getChildFiles() throws IOException, OneDriveException {
        return api.getChildFiles(this);
    }

    @Override
    public List<OneItem> getChildren() throws IOException, OneDriveException {
        return api.getChildren(this, OneItemType.ALL);
    }

    @Override
    public OneFolder createFolder(String name) throws IOException, OneDriveException {
        return api.createFolder(this, name);
    }

    @Override
    public OneFolder createFolder(String name, ConflictBehavior behavior) throws IOException, OneDriveException {
        return api.createFolder(this, name, behavior);
    }

    @Override
    public int getChildCount() {
        return this.folder.childCount;
    }

    @Override
    public OneUploadFile uploadFile(File file) throws IOException, OneDriveException {
        return new ConcreteOneUploadFile(this, file, api);
    }

    @Override
    public String toString() {
        return "(D) " + name;
    }

}

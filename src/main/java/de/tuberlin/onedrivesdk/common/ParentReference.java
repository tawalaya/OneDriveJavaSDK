package de.tuberlin.onedrivesdk.common;

/**
 * Created by Andi on 10.05.2015.
 * The parent folder reference of an item. Used for JSON transport.
 */
public class ParentReference {
    protected String driveId;
    protected String id;
    protected String path;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

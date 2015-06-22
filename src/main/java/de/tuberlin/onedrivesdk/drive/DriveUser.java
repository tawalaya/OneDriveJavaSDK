package de.tuberlin.onedrivesdk.drive;

/**
 * Data object for drive user
 */
public class DriveUser {
    private String id;
    private String displayName;

    public DriveUser(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Owner: " + displayName + " - " + id;
    }
}

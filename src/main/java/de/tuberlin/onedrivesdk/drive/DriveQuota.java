package de.tuberlin.onedrivesdk.drive;

/**
 * Data Object for drive qouta
 */
public class DriveQuota {
    private long deleted = 0;
    private long remaining = 0;
    private String state;
    private long total = 0;
    private long used = 0;

    public long getDeleted() {
        return deleted;
    }

    public long getRemaining() {
        return remaining;
    }

    public String getState() {
        return state;
    }

    public long getTotal() {
        return total;
    }

    public long getUsed() {
        return used;
    }
}

package de.tuberlin.onedrivesdk.drive;

/**
 * Created by Andi on 11.05.2015.
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

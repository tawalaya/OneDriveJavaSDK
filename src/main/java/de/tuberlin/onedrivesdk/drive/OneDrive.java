package de.tuberlin.onedrivesdk.drive;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.folder.OneFolder;

import java.io.IOException;
/**
 * This Interface provides all Method to handle a specific Drive
 *
 */
public interface OneDrive {

    String getId();

    /**
     * Gets the root folder of the default drive.
     *
     * @return root folder
     * @throws IOException
     * @throws IOException
     */
    OneFolder getRootFolder() throws IOException, OneDriveException;


    /**
     * Gets drive type. OneDrive drives will show as personal.
     *
     * @return drive type
     */
    String getDriveType();

    /**
     * Gets the owner user of the drive.
     *
     * @return user
     */
    DriveUser getUser();


    /**
     * Gets the information about the drive's storage space quota.
     *
     * @return quota
     */
    DriveQuota getQuota();

    /**
     * Gets the raw JSON which is received from the OneDrive API.
     *
     * @return raw json
     */
    String getRawJson();
}

package de.tuberlin.onedrivesdk.file;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import de.tuberlin.onedrivesdk.drive.DriveUser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public interface OneFile {

    /**
     * Returns a URL that can be used to download this file's content.
     *
     * @return web url
     */
    String getDownloadUrl();

    /**
     * Gets the cTag.
     *
     * @return cTag
     */
    String getCTag();

    /**
     * Gets the eTag.
     *
     * @return eTag
     */
    String getETag();

    /**
     * Gets the OneDrive id of the file.
     *
     * @return id
     */
    String getId();

    /**
     * Gets the name of the folder.
     *
     * @return name
     */
    String getName();

    /**
     * Gets the size of this file in bytes.
     *
     * @return size
     */
    long getSize();

    /**
     * The created by reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return created by
     */
    HashMap<String, DriveUser> getCreatedBy();

    /**
     * The creation timestamp of this file in unix format.
     *
     * @return unix formatted timestamp
     */
    long getCreatedDateTime();


    /**
     * The last modified reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return last modified by
     */
    HashMap<String, DriveUser> getLastModifiedBy();

    /**
     * The last modified timestamp of this file.
     *
     * @return unix formatted timestamp
     */
    long getLastModifiedDateTime();

    /**
     * Returns the last refresh time since the folder metadata was fetched from the OneDrive server.
     *
     * @return unix formatted timestamp
     */
    long getLastRefresh();

    /**
     * Refreshes the metadata of this file.
     *
     * @return OneFile with new reference
     * @throws OneDriveException
     * @throws IOException
     */
    OneFile refresh() throws OneDriveException, IOException;


    /**
     * Gets the CRC32 value of the file (if available).
     *
     * @return cRC 32 hash
     */
    String getCRC32Hash();

    /**
     * Gets the SHA1 hash for the contents of the file (if available).
     *
     * @return SHA1 hash
     */
    String getSHA1Hash();


    /**
     * Gets the MIME type for the file.
     *
     * @return mime type
     */
    String getMimeType();

    /**
     * Gets the currents parent folder.
     *
     * @return the currents parent folder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder getParentFolder() throws IOException, OneDriveException;

    /**
     * Gets the URL that displays the resource in the browser.
     *
     * @return web url
     */
    String getWebUrl();

    /**
     * Downloads this file into the specified local file handle.
     * Will block until finished or interrupted.
     *
     * @param targetFile the local file handle. MUST be writable.
     * @return the DownloadSession
     */
    OneDownloadFile download(File targetFile) throws FileNotFoundException;

    /**
     * Deletes the current folder.
     *
     * @return true if the deletion was successful, false otherwise
     * @throws OneDriveException
     * @throws IOException
     */
    boolean delete() throws IOException, OneDriveException;


    /**
     * Copy this file into the target folder.
     *
     * @param targetFolder destination folder
     * @return the new created reference of the file in the new folder
     * @throws IOException
     * @throws OneDriveException
     * @throws ParseException
     * @throws InterruptedException
     */
    OneFile copy(OneFolder targetFolder) throws IOException, OneDriveException, ParseException, InterruptedException;

    /**
     * Copy this file into the target folder.
     *
     * @param targetFolder destination folder
     * @param name a new name for the file
     * @return the new created reference of the file in the new folder
     * @throws IOException
     * @throws OneDriveException
     * @throws ParseException
     * @throws InterruptedException
     */
    OneFile copy(OneFolder targetFolder, String name) throws IOException, OneDriveException, ParseException, InterruptedException;

    /**
     * Move this file into the target folder.
     *
     * @param targetFolder destination folder
     * @return the reference of the file in the new folder
     * @throws InterruptedException
     * @throws OneDriveException
     * @throws ParseException
     * @throws IOException
     */
    OneFile move(OneFolder targetFolder) throws InterruptedException, OneDriveException, ParseException, IOException;

    /**
     * Gets the raw JSON which is received from the OneDrive API.
     *
     * @return raw json
     */
    String getRawJson();
}

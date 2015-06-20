package de.tuberlin.onedrivesdk.folder;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConflictBehavior;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.drive.DriveUser;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.uploadFile.OneUploadFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface OneFolder {

    /**
     * Gets the OneDrive id of the folder.
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
     * Gets the cTag.
     *
     * @return cTag
     */
    String getCTag();

    /**
     * Gets eTag.
     *
     * @return eTag
     */
    String getETag();

    /**
     * The created by reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return created by
     */
    HashMap<String, DriveUser> getCreatedBy();

    /**
     * The creation timestamp of this folder.
     *
     * @return unix formatted timestamp
     */
    long getCreatedDateTime();

    /**
     * The last modified reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return last modified
     */
    HashMap<String, DriveUser> getLastModifiedBy();

    /**
     * The last modified timestamp of this folder.
     *
     * @return unix formatted timestamp
     */
    long getLastModifiedDateTime();

    /**
     * Gets the URL that displays the resource in the browser.
     *
     * @return web url
     */
    String getWebUrl();

    /**
     * Gets the size of this item in bytes.
     *
     * @return size
     */
    long getSize();

    /**
     * Returns the last refresh time since the folder metadata was fetched from the OneDrive server.
     *
     * @return unix formatted timestamp
     */
    long getLastRefresh();

    /**
     * Refreshes the metadata of this folder.
     *
     * @return OneFolder with new reference
     * @throws OneDriveException
     * @throws IOException
     */
    OneFolder refresh() throws  OneDriveException, IOException;

    /**
     * Gets the currents folders parent.
     * If already in the root folder, returns the root folder again.
     *
     * @return the currents folders parent
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder getParentFolder() throws IOException, OneDriveException;

    /**
     * Gets all folder inside the current folder.
     *
     * @return all folder inside the current folder
     * @throws IOException
     * @throws OneDriveException
     */
    List<OneFolder> getChildFolder() throws IOException, OneDriveException;

    /**
     * Gets all normal files in this folder.
     *
     * @return all files in this folder
     * @throws IOException
     * @throws OneDriveException
     */
    List<OneFile> getChildFiles() throws IOException, OneDriveException;

    /**
     * Gets the children of this folder (e.g. files and folder).
     *
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    List<OneItem> getChildren() throws IOException, OneDriveException;

    /**
     * Create a folder with the specified name in the current folder.
     *
     * @param name of the new folder
     * @return the newly created folder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder createFolder(String name) throws IOException, OneDriveException;

    /**
     * Create a folder with the specified name in the current folder.
     *
     * @param name of the new folder
     * @param behavior the behavior in case of name conflict
     * @return folder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder createFolder(String name, ConflictBehavior behavior) throws IOException, OneDriveException;

    /**
     * Gets child count.
     *
     * @return child count
     */
    int getChildCount();

    /**
     * Creates a resumable upload Session for a file.
     * The file will be uploaded into this folder.
     * The upload is not started upon creation but need to be started manually.
     *
     * @param file the file to be uploaded
     * @return the OneUploadFile session
     * @throws IOException
     * @throws OneDriveException
     */
    OneUploadFile uploadFile(File file) throws IOException, OneDriveException;

    String toString();


    /**
     * Deletes the current folder.
     *
     * @return true if the deletion was successful, false otherwise
     * @throws OneDriveException
     * @throws IOException
     */
    boolean delete() throws OneDriveException,IOException;

    /**
     * Gets the raw JSON which is received from the OneDrive API.
     *
     * @return raw json
     */
    String getRawJson();
}

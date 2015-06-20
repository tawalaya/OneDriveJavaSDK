package de.tuberlin.onedrivesdk.downloadFile;

import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;

import java.io.File;
import java.io.IOException;

public interface OneDownloadFile {

    /**
     * Gets the meta data of the downloaded file.
     *
     * @return meta data
     */
    OneFile getMetaData();

    /**
     * Starts Download, blocks until finished.
     *
     * @throws IOException
     */
    void startDownload() throws IOException, OneDriveAuthenticationException;

    /**
     * Gets the file handel of the downloaded file.
     *
     * @return downloaded file
     */
    File getDownloadedFile();
}

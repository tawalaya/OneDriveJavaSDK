package de.tuberlin.onedrivesdk.file;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.downloadFile.ConcreteOneDownloadFile;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Implementation of OneFile using methods from ConcreteOneDriveSDK
 */
public class ConcreteOneFile extends OneItem implements OneFile {

    private FileProperty file;

    private ConcreteOneFile() {
    }

    public static ConcreteOneFile fromJSON(String json) throws ParseException, OneDriveException {
        return (ConcreteOneFile) OneItem.fromJSON(json).setRawJson(json);
    }

    @Override
    public String toString() {
        return "(F) " + name;
    }

    public OneDownloadFile download(File targetFile) throws FileNotFoundException {
        return new ConcreteOneDownloadFile(this,api,targetFile);
    }

    @Override
    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    @Override
    public OneFile refresh() throws OneDriveException, IOException {
        return (OneFile) super.refreshItem();
    }

    @Override
    public String getCRC32Hash() {
        return this.file.hashes.get("crc32Hash");
    }

    @Override
    public String getSHA1Hash() {
        return this.file.hashes.get("sha1Hash");
    }

    @Override
    public String getMimeType() {
        return this.file.mimeType;
    }

    @Override
    public OneFolder getParentFolder() throws IOException, OneDriveException {
        return super.getParentFolder();
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public OneFile copy(OneFolder targetFolder) throws IOException, OneDriveException, ParseException, InterruptedException {
        return this.copy(targetFolder, null);
    }

    @Override
    public OneFile copy(OneFolder targetFolder, String name) throws IOException, OneDriveException, ParseException, InterruptedException {
        return api.copy(id, targetFolder.getId(), name);
    }

    @Override
    public OneFile move(OneFolder targetFolder) throws InterruptedException, OneDriveException, ParseException, IOException {
        return api.move(id, targetFolder.getId());
    }
}

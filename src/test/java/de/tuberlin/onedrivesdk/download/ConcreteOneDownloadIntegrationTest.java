package de.tuberlin.onedrivesdk.download;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.common.TestSDKFactory;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConcreteOneDownloadIntegrationTest {

	@Test
    public void simpleDownloadTest() throws InstantiationException,
            IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, SecurityException, OneDriveException, IOException, InterruptedException {
		OneDriveSDK api = TestSDKFactory.getInstance();

		OneFolder folder = api.getFolderByPath("/IntegrationTesting/FolderForDownload");
		List<OneFile> files = folder.getChildFiles();


		for (OneFile file : files){
			File localCopy = File.createTempFile(file.getName(), ".bin");

			OneDownloadFile f = file.download(localCopy);
			f.startDownload();

			HashCode code = Files.hash(localCopy, Hashing.sha1());
            assertEquals(file.getName() + " mismatch", code.toString().toUpperCase(), file.getSHA1Hash());
        }
	}

}

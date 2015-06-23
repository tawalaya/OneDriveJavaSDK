package de.tuberlin.onedrivesdk.uploadFile;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.common.TestSDKFactory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

public class ConcreteOneUploadFileIntegrationTest {

	private OneFile uploadedFile;

	@Test
	public void simpleUploadTest() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, SecurityException, OneDriveException, IOException, InterruptedException {
		OneDriveSDK api = TestSDKFactory.getInstance();
		File file = new File("src/test/resources/uploadTest.jpg");
		OneFolder folder = api.getRootFolder();
		OneUploadFile upload = folder.uploadFile(file);
		//Future<OneFile> futureUpload = executor.submit(upload);
		uploadedFile = upload.startUpload();
		System.out.println(uploadedFile.toString());
		assertTrue(uploadedFile!=null);
		assertTrue(uploadedFile.getName().equals("uploadTest.jpg"));
		System.out.println(uploadedFile.getId());
	}

	@After
	public void removeTestFile() throws IOException, OneDriveException {
		if(uploadedFile!=null) {
			uploadedFile.delete();
			uploadedFile=null;
			}
	}

	public static Field getUnaccessibleField(String fieldName, Class<?> clazz)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		Field privateField = clazz.getDeclaredField(fieldName);

		privateField.setAccessible(true);

		return privateField;

	}
}

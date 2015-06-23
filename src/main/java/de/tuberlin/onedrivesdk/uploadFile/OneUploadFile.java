package de.tuberlin.onedrivesdk.uploadFile;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * This Interface provides all Methods to (resumable) upload a File 
 * @author timmeey
 *
 */
public interface OneUploadFile extends Callable<OneFile> {

	//http://onedrive.github.io/items/upload_large_files.htm
	
	
	/**
	 * Gets the size of the file that is uploaded (convenience method for simple completion Percent calculation)
	 * @return size of the file
	 */
	long fileSize();

	/**
	 * Gets something like an enum indication that the upload is either finished,running,paused,canceled...
	 * @return upload status
	 */
	long uploadStatus() throws IOException, OneDriveException;
	
	/**
	 * Starts the upload, needs to be called to start the upload. Will throw exception when called while the download has already been started
	 * Blocks until either upload is finished or interrupted.
	 * @return the OneFile handle for the finished file
	 * @throws IOException 
	 */
	OneFile startUpload() throws IOException, OneDriveException;
	
	/**
	 * Will pause the upload. Does nothing when called if the download is already paused
	 * @return this.OneUploadFile
	 */
	OneUploadFile pauseUpload();
	
	/**
	 * Will resume the upload if it got paused/interrupted. Calling this method on an already running upload will do nothing
	 * @return this.OneUploadFile
	 */
	OneUploadFile resumeUpload();
	
	/**
	 * Will cancel the upload completely. Calling this more than once will do nothing
	 * Canceled uploads can't be resumed
	 * Will block until file Upload is finished
	 * @return the OneFile handle of the finished File or null on interruption
	 */
	OneUploadFile cancelUpload() throws IOException, OneDriveAuthenticationException;
	
	/**
	 * Gets the file handle of the file that is being uploaded 
	 * @return the file handle of the File that is uploaded
	 */
	File getUploadFile();
}

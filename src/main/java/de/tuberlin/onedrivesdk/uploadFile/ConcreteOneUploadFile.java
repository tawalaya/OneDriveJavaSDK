package de.tuberlin.onedrivesdk.uploadFile;

import com.google.gson.Gson;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.ConcreteOneFolder;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;
import de.tuberlin.onedrivesdk.networking.OneResponse;
import de.tuberlin.onedrivesdk.networking.PreparedRequest;
import de.tuberlin.onedrivesdk.networking.PreparedRequestMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of OneUploadFile, blocking operation
 */
public class ConcreteOneUploadFile implements OneUploadFile {

    private static final int chunkSize = 320 * 1024 * 30; // (use a multiple value of 320KB, best practice of dev.onedrive)
    private static final Logger logger = LogManager.getLogger(ConcreteOneUploadFile.class);
    private static final Gson gson = new Gson();
    private final ReentrantLock shouldRun = new ReentrantLock(true);
    private File fileToUpload;
    private ConcreteOneDriveSDK api;
    private boolean canceled = false;
	private boolean finished = false;
	private UploadSession uploadSession;
	private RandomAccessFile randFile;
	private String uploadUrl ="";

	public ConcreteOneUploadFile(ConcreteOneFolder parentFolder,
			File fileToUpload, ConcreteOneDriveSDK api) throws IOException, OneDriveAuthenticationException {
		checkNotNull(parentFolder);
		this.api = checkNotNull(api);

		if (fileToUpload != null) {
			if (fileToUpload.isFile()) {
				if (fileToUpload.canRead()) {
					this.fileToUpload = fileToUpload;
						randFile = new RandomAccessFile(fileToUpload, "r");
				} else {
					throw new IOException(String.format("File %s is not readable!", fileToUpload.getName()));
				}
			} else {
				throw new IOException(String.format("%s is not a File",
						fileToUpload.getAbsolutePath()));
			}
		} else {
			throw new NullPointerException("FileToUpload was null");
		}
		this.uploadSession = api.createUploadSession(parentFolder, fileToUpload.getName());
		this.uploadUrl = this.uploadSession.getUploadURL();
    }

    @Override
	public long fileSize() {
		return fileToUpload.length();
	}

    @Override
	public long uploadStatus() throws IOException, OneDriveException {
		if (uploadSession != null) {
            PreparedRequest request = new PreparedRequest(this.uploadUrl, PreparedRequestMethod.GET);
			OneResponse response = api.makeRequest(request);
            if (response.wasSuccess()) {
                return gson.fromJson(response.getBodyAsString(), UploadSession.class).getNextRange();
            } else {
				throw new OneDriveException(response.getBodyAsString());
			}
		}
		return 0;
	}

    @Override
	public OneFile startUpload() throws IOException, OneDriveException {
		byte[] bytes;
		ConcreteOneFile finishedFile = null;

		OneResponse response;

		while (!canceled && !finished) {
			shouldRun.lock();

			long currFirstByte = randFile.getFilePointer();
			PreparedRequest uploadChunk = new PreparedRequest(this.uploadUrl, PreparedRequestMethod.PUT);

			if (currFirstByte + chunkSize < randFile.length()) {
				bytes = new byte[chunkSize];
			} else {
				// optimistic cast, assuming the last bit of the file is
				// never bigger than MAXINT
                bytes = new byte[(int) (randFile.length() - randFile.getFilePointer())];
            }
			long start = randFile.getFilePointer();
			randFile.readFully(bytes);

			uploadChunk.setBody(bytes);
            uploadChunk.addHeader("Content-Length", (randFile.getFilePointer() - start) + "");
            uploadChunk.addHeader(
                    "Content-Range",
                    String.format("bytes %s-%s/%s", start, randFile.getFilePointer() - 1, randFile.length()));

            logger.trace("Uploading chunk {} - {}", start, randFile.getFilePointer() - 1);
            response = api.makeRequest(uploadChunk);
			if (response.wasSuccess()) {
				if (response.getStatusCode()==200 || response.getStatusCode()==201) { // if last chunk upload was successful end the
					finished = true;
                    finishedFile = gson.fromJson(response.getBodyAsString(), ConcreteOneFile.class);

                }else {
					//just continue
                    uploadSession = gson.fromJson(response.getBodyAsString(),
							UploadSession.class);
                    randFile.seek(uploadSession.getNextRange());
				}
			} else {
				logger.info("Something went wrong while uploading last chunk. Trying to fetch upload status from server to retry");
				logger.trace(response.getBodyAsString());
                response = api.makeRequest(this.uploadUrl, PreparedRequestMethod.GET, null);

                if (response.wasSuccess()) {
                    uploadSession = gson.fromJson(
							response.getBodyAsString(), UploadSession.class);
                    randFile.seek(uploadSession.getNextRange());
                    logger.debug("Fetched updated uploadSession. Server requests {} as next chunk",uploadSession.getNextRange());

				} else {
					canceled=true;
					logger.info("Something went wrong while uploading. Was unable to fetch the currentUpload session from the Server");
                    randFile.close();
                    throw new OneDriveException(
                            String.format("Could not get current upload status from Server, aborting. Message was: %s", response.getBodyAsString()));
                }
			}
			shouldRun.unlock();

		}
        randFile.close();
        logger.info("finished upload");

		finishedFile.setApi(api);
		return finishedFile;

	}

    @Override
	public OneUploadFile pauseUpload() {
		logger.info("Pausing upload");
		shouldRun.lock();
		logger.info("Upload paused");
		return this;
	}

    @Override
	public OneUploadFile resumeUpload() {
		logger.info("Resuming upload");
		try{
			shouldRun.unlock();
			logger.info("Upload resumed");
		}catch (IllegalMonitorStateException e) {
			logger.info("Trying to resume an already running download");
		}
		return this;
	}

    @Override
	public OneUploadFile cancelUpload() throws IOException, OneDriveAuthenticationException {
		logger.info("Canceling upload");
		this.canceled = true;
		if (uploadSession != null) {
			api.makeRequest(this.uploadUrl,
                    PreparedRequestMethod.DELETE, "");
            logger.info("Upload was canceled");
		}
		return this;
	}

    @Override
	public File getUploadFile() {
		return this.fileToUpload;
	}

	@Override
	public OneFile call() throws IOException, OneDriveException {
		logger.info("Starting upload");
		return startUpload();
	}

}

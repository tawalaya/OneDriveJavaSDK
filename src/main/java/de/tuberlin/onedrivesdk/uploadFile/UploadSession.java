package de.tuberlin.onedrivesdk.uploadFile;

import de.tuberlin.onedrivesdk.OneDriveException;
/**
 * THis internal class keeps a representation of the UploadSession provided by the OneDriveAPI
 * @author timmeey
 *
 */
public class UploadSession {
	
	private String uploadUrl;
	private long expirationDate;
	private String[] nextExpectedRanges;
	private UploadSession() {}

	/**
	 * Gives the uploadURL to where the next range should be uploaded to
	 * @return the uploadURL to upload to
	 */
	public String getUploadURL() {
		return uploadUrl;
	}

	/**
	 * Gets the expiration Date of this UploadSession
	 * @return the expiration Date of this upload session
	 */
	public long getExpirationDate() {
		return expirationDate;
	}

	/**
	 * THis method will parse the ranges that are still missing acording to OneDriveAPI and will give the start next range that needs to be uploaded
	 * @return the next byte positin that should be uploaded
	 * @throws OneDriveException
	 */
	public long getNextRange() throws OneDriveException {
		if(nextExpectedRanges!= null && nextExpectedRanges.length>=1) {
			return Long.parseLong(nextExpectedRanges[0].split("-")[0]);
		}else {
            return 0;
		}
	}
}

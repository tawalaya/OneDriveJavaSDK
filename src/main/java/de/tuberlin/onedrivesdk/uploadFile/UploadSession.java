package de.tuberlin.onedrivesdk.uploadFile;

import de.tuberlin.onedrivesdk.OneDriveException;

public class UploadSession {
	
	private String uploadUrl;
	private long expirationDate;
	private String[] nextExpectedRanges;
	private UploadSession() {}

	public String getUploadURL() {
		return uploadUrl;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public long getNextRange() throws OneDriveException {
		if(nextExpectedRanges!= null && nextExpectedRanges.length>=1) {
			return Long.parseLong(nextExpectedRanges[0].split("-")[0]);
		}else {
            return 0;
		}
	}
}

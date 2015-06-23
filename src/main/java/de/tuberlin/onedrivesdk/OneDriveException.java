package de.tuberlin.onedrivesdk;

/**
 * Exception that can be thrown on all API calls,
 * mostly JSON error responses from the oneDrive server
 */
public class OneDriveException extends Exception {

    public OneDriveException(String msg, Throwable reason) {
        super(msg, reason);
    }

    public OneDriveException(String msg) {
        super(msg);
    }

}

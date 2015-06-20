package de.tuberlin.onedrivesdk;

public class OneDriveException extends Exception {

    public OneDriveException(String msg, Throwable reason) {
        super(msg, reason);
    }

    public OneDriveException(String msg) {
        super(msg);
    }

}

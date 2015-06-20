package de.tuberlin.onedrivesdk.networking;


import de.tuberlin.onedrivesdk.OneDriveException;

public class OneDriveAuthenticationException extends OneDriveException {
    public OneDriveAuthenticationException(String msg, Throwable reason) {
        super(msg, reason);
    }

    public OneDriveAuthenticationException(String msg) {
        super(msg);
    }
}

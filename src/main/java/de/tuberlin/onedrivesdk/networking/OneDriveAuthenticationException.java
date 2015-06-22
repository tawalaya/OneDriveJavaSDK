package de.tuberlin.onedrivesdk.networking;


import de.tuberlin.onedrivesdk.OneDriveException;

/**
 * This is Exception is used to indicated that an unauthorized session has been used for an operatio
 */
public class OneDriveAuthenticationException extends OneDriveException {
    public OneDriveAuthenticationException(String msg, Throwable reason) {
        super(msg, reason);
    }

    public OneDriveAuthenticationException(String msg) {
        super(msg);
    }
}

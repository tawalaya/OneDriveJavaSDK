package de.tuberlin.onedrivesdk;

import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.ExceptionEventHandler;
import de.tuberlin.onedrivesdk.common.OneDriveScope;
import de.tuberlin.onedrivesdk.common.OneDriveCredentials;

public final class OneDriveFactory {

    /**
     * Create default OneDriveSDK without a redirect URL, pure code flow.
     * @param clientId
     * @param clientSecret
     * @param scopes
     * @return
     */
    public static OneDriveSDK createOneDriveSDK(String clientId, String clientSecret,
                                                OneDriveScope... scopes) {
        return ConcreteOneDriveSDK.createOneDriveConnection(clientId, clientSecret, null, null, scopes);
    }

    /**
     * Creates default OneDriveSDK with redirect.
     * @param clientId
     * @param clientSecret
     * @param redirect_uri
     * @param scopes
     * @return
     */
    public static OneDriveSDK createOneDriveSDK(String clientId, String clientSecret,
                                                String redirect_uri, OneDriveScope... scopes) {
        return ConcreteOneDriveSDK.createOneDriveConnection(clientId,clientSecret,redirect_uri,null,scopes);
    }

    /**
     * Creates OneDriverSDK with redirect URL and a ExceptionHandler as a callback in case the automatic refresh
     * of the authentication session fails.
     * @param clientId
     * @param clientSecret
     * @param redirect_uri
     * @param handler
     * @param scopes
     * @return
     */
    public static OneDriveSDK createOneDriveSDK(String clientId, String clientSecret,
                                                String redirect_uri,ExceptionEventHandler handler, OneDriveScope... scopes) {
        return ConcreteOneDriveSDK.createOneDriveConnection(clientId,clientSecret,redirect_uri,handler,scopes);
    }

    /**
     * Create default OneDriveSDK with a given redirect URL using the @see OneDriveCredentials.
     *
     * @param redirect_uri
     * @return
     * @throws RuntimeException if no credential file can be found.
     */
    public static OneDriveSDK createOneDriveSDK(String redirect_uri, OneDriveScope... scopes) {
        return ConcreteOneDriveSDK.createOneDriveConnection(OneDriveCredentials.getClientId()
                , OneDriveCredentials.getClientSecret(), redirect_uri, null, scopes);
    }
}

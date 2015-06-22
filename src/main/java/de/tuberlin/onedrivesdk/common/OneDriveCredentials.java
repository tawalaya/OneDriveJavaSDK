package de.tuberlin.onedrivesdk.common;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 * Loads the clientId and the clientSecret from a properties file.
 */
public class OneDriveCredentials {
    private final static String credentialFileLocation = "credentials.properties";
    private final static Properties properties = new Properties();
    private static String clientId = null;
    private static String clientSecret = null;

    public static String getClientId() {
        if (clientId == null)
            loadConfig();

        return clientId;
    }

    public static String getClientSecret() {
        if (clientSecret == null)
            loadConfig();

        return clientSecret;
    }

    private static void loadConfig() {
        File credentialsFile = null;
        try {
            credentialsFile = new File(credentialFileLocation);
            properties.load(new FileReader(credentialsFile));
            clientId = properties.getProperty("clientId");
            clientSecret = properties.getProperty("clientSecret");
        } catch (Exception e) {
            throw new RuntimeException("Credentials properties file can not be loaded at: "+credentialsFile.getAbsolutePath());
        }
    }

}

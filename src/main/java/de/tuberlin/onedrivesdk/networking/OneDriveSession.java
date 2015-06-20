package de.tuberlin.onedrivesdk.networking;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.squareup.okhttp.*;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ExceptionEventHandler;
import de.tuberlin.onedrivesdk.common.OneDriveScope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handel's authentication and continues refresh of the accessToken.
 */
public class OneDriveSession implements Runnable {


    private static final Logger logger = LogManager.getLogger(OneDriveSession.class);

    private final static String ENDPOINT = "https://login.live.com";
    private final long refreshDelay = 3000 * 1000;//3000 sec to ms

    @Expose
    private final String clientID;
    @Expose
    private final String clientSecret;

    private final OneDriveScope[] scopes;
    private OkHttpClient client;
    private ExecutorService refreshThread;
    private ExceptionEventHandler refreshExceptionHandler;

    private String tokenType;

    @Expose
    private long expiresIn;
    @Expose
    private String accessToken;
    @Expose
    private String refreshToken;
    @Expose
    private long lastRefresh = Long.MIN_VALUE;

    private String redirect_uri;
    private boolean keepRefreshing = true;


    private OneDriveSession(OkHttpClient client, String clientID, String clientSecret, String redirectUri, ExceptionEventHandler refreshExceptionHandler, OneDriveScope[] scopes) {
        this.client = client;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.scopes = scopes;

        if (redirectUri != null) {
            try {
                this.redirect_uri = URLEncoder.encode(redirectUri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("redirectURL is not a valid url... or something else went horrobly wrong at this point..." + e.getMessage());
            }
        }

        this.refreshExceptionHandler = refreshExceptionHandler;

        logger.info("initialize session for " + clientID);
    }

    private OneDriveSession(OkHttpClient client, String clientID, String clientSecret, String redirectUri, OneDriveScope[] scopes) {
        this(client, clientID, clientSecret, redirectUri, null, scopes);
    }

    /**
     * creates a OneDriveSession used to handle the authentication process
     *
     * @param client            Used for HTTP Communication
     * @param clientID          @see https://dev.onedrive.com/auth/msa_oauth.htm CodeFlow
     * @param clientSecret      @see https://dev.onedrive.com/auth/msa_oauth.htm CodeFlow
     * @param redirect_uri      @see https://dev.onedrive.com/auth/msa_oauth.htm can be null
     * @param exceptionCallback Method that is called if the RefreshLoop encounters an exception
     * @param scopes            @see https://dev.onedrive.com/auth/msa_oauth.htm
     * @return
     */
    public static OneDriveSession initializeSession(OkHttpClient client, String clientID, String clientSecret,
                                                    String redirect_uri, ExceptionEventHandler exceptionCallback, OneDriveScope[] scopes) {

        return new OneDriveSession(client, clientID, clientSecret, redirect_uri, exceptionCallback, scopes);
    }

    public static OneDriveSession initializeSession(OkHttpClient client, String clientID, String clientSecret,
                                                    String redirect_uri, OneDriveScope... scopes) {

        return new OneDriveSession(client, clientID, clientSecret, redirect_uri, scopes);
    }

    public static void authorizeSession(OneDriveSession session, String code) throws OneDriveException {
        //The body of the second step in the code-flow guide
        String oAuthCodeRedeemBodyString = String.format("client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code",
                session.getClientID(), session.getClientSecret(), code);
        if (session.redirect_uri != null) {
            oAuthCodeRedeemBodyString += String.format("&redirect_uri=%s", session.redirect_uri);
        }

        handleAuthRequest(session, oAuthCodeRedeemBodyString);
    }

    public static void refreshSession(OneDriveSession session, String refreshToken) throws OneDriveException {
        session.setRefreshToken(refreshToken);
        session.refresh();
    }

    private static void handleAuthRequest(OneDriveSession session, String messageBody) throws OneDriveAuthenticationException {
        JSONParser jsonParser = new JSONParser();
        //Url of the second step of the Code-FLow guide
        String oAuthCodeRedeemURL = String.format("%s/oauth20_token.srf", ENDPOINT);

        RequestBody oAuthCodeRedeemBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), messageBody);
        // Create request for remote resource.
        Request request = new Request.Builder()
                .url(oAuthCodeRedeemURL)
                .post(oAuthCodeRedeemBody)
                .build();

        // Execute the request and retrieve the response.
        String responseBody = null;
        try {
            Response response = session.getClient().newCall(request).execute();
            responseBody = response.body().string();
            JSONObject resp = (JSONObject) jsonParser.parse(responseBody);

            if (resp.containsKey("error")) {
                String error = (String) resp.get("error");
                String error_description = (String) resp.get("error_description");
                throw new OneDriveException(String.format("[%s] %s", error, error_description));
            } else {
                session.setTokenType((String) resp.get("token_type"));
                session.setExpiresIn((Long) resp.get("expires_in"));
                session.setAccessToken((String) resp.get("access_token"));
                session.setRefreshToken((String) resp.get("refresh_token"));
                session.setLastRefresh(System.currentTimeMillis());
            }
        } catch (OneDriveException e) {
            throw new OneDriveAuthenticationException("Internal Error:", e);
        } catch (IOException e) {
            throw new OneDriveAuthenticationException("Could not establish a connection.", e);
        } catch (ParseException pa) {
            throw new OneDriveAuthenticationException("Could not process the server response.", pa);
        } catch (Exception e) {
            throw new OneDriveAuthenticationException("A undefined error accrued during the authentication request.\n" + responseBody, e);
        }
        logger.info("successfully authorized session for " + session.clientID);
    }

    private static Gson builderGson() {
        final GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers();
        builder.registerTypeAdapter(OneDriveSession.class, new JsonDeserializer<OneDriveSession>() {
            @Override
            public OneDriveSession deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject retValue = jsonElement.getAsJsonObject();

                String accessToken = retValue.get("accessToken").getAsString();
                String refreshToken = retValue.get("refreshToken").getAsString();
                String clientID = retValue.get("clientID").getAsString();
                String clientSecret = retValue.get("clientSecret").getAsString();
                long expiresIn = retValue.get("expiresIn").getAsLong();
                long lastRefresh = retValue.get("lastRefresh").getAsLong();

                OneDriveSession session = new OneDriveSession(new OkHttpClient(), clientID, clientSecret, null, null, new OneDriveScope[]{OneDriveScope.OFFLINE_ACCESS});
                session.setRefreshToken(refreshToken);
                session.setAccessToken(accessToken);
                session.setLastRefresh(lastRefresh);
                session.setExpiresIn(expiresIn);
                return session;
            }
        });

        builder.registerTypeAdapter(OneDriveSession.class, new JsonSerializer<OneDriveSession>() {
            @Override
            public JsonElement serialize(OneDriveSession session, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject retValue = new JsonObject();

                retValue.add("accessToken", new JsonPrimitive(session.getAccessToken()));
                retValue.add("refreshToken", new JsonPrimitive(session.getRefreshToken()));

                retValue.add("clientID", new JsonPrimitive(session.getClientID()));
                retValue.add("clientSecret", new JsonPrimitive(session.getClientSecret()));
                retValue.add("expiresIn", new JsonPrimitive(session.getExpiresIn()));
                retValue.add("lastRefresh", new JsonPrimitive(session.getLastRefresh()));
                return retValue;
            }
        });
        final Gson gson = builder.create();
        return gson;
    }

    public static OneDriveSession readFromFile(File f) throws IOException {
        StringBuilder json = new StringBuilder();

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String l;
            while ((l = r.readLine()) != null) {
                json.append(l);
            }
        }


        final OneDriveSession session = builderGson().fromJson(json.toString(), OneDriveSession.class);
        session.setClient(new OkHttpClient());
        return session;
    }

    public static void write(OneDriveSession that, File outputFile) throws IOException, OneDriveException {
        that.refresh();

        String json = builderGson().toJson(that);

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(outputFile))) {
            bf.write(json);
        }
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public boolean isAuthenticated() {
        return lastRefresh + expiresIn * 1000 >= System.currentTimeMillis();
    }

    public OkHttpClient getClient() {
        return client;
    }

    private void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScopeString() {
        StringBuilder bul = new StringBuilder();
        for (OneDriveScope os : scopes) {
            bul.append(os.getCode()).append(" ");
        }

        return bul.toString().trim();
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void refresh() throws OneDriveException {
        String oAuthRefreshBodyString = String.format("client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token", clientID, clientSecret, refreshToken);
        handleAuthRequest(this, oAuthRefreshBodyString);
    }

    public String getAccessURL() {
        String scope = "";
        try {
            scope = URLEncoder.encode(getScopeString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Error while encoding scopeString to url, using UTF-8",e);
        }
        String uri = String.format("%s/oauth20_authorize.srf?client_id=%s&scope=%s&response_type=code", ENDPOINT, clientID, scope);

        if (this.redirect_uri != null) {
            uri += String.format("&redirect_uri=%s", this.redirect_uri);
        }

        return uri;
    }

    @Override
    public String toString() {
        return "OneDriveSession{" +
                "accessToken='" + accessToken + '\'' +
                "\n, expiresIn=" + expiresIn +
                "\n, refreshToken='" + refreshToken + '\'' +
                "\n, lastRefresh=" + lastRefresh + '\'' +
                "\n, scope=" + getScopeString() +
                '}';
    }

    public void run() {
        //initial delay
        try {
            Thread.sleep(refreshDelay);
        } catch (InterruptedException e) {
        }
        //continuously refresh thread
        while (keepRefreshing) {
            logger.info("refreshing session");
            try {
                refresh();
                Thread.sleep(refreshDelay);
            } catch (OneDriveException e) {
                logger.info("failed to refresh session - attempting recovery");
                long retryTime = System.currentTimeMillis() + 1000 * 30;
                while (System.currentTimeMillis() <= retryTime && keepRefreshing) {
                    try {
                        refresh();
                    } catch (OneDriveException e1) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                        }
                    }
                }
                logger.error("could not refresh session", e);
                if (refreshExceptionHandler != null) {
                    refreshExceptionHandler.handle(this, e);
                }
                //backoff after error
                try {
                    Thread.sleep(10000);
                } catch (Exception e1) {
                }
            } catch (Exception e) {
            }
        }

    }

    public void startRefreshThread() {
        if (this.refreshThread == null) {
            this.refreshThread = Executors.newSingleThreadExecutor();
            this.refreshThread.submit(this);
            logger.info("starting refresh thread");
        }
    }

    public void terminate() {
        if (this.refreshThread != null) {
            keepRefreshing = false;
            refreshThread.shutdownNow();
            logger.info("stopping refresh thread");
        }
    }
}
package de.tuberlin.onedrivesdk.networking;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a HTTP Request
 */
public class PreparedRequest {

    private String method;
    private String path;
    private Map<String, String> header = new HashMap<>();
    private byte[] body;

    public PreparedRequest(String path, PreparedRequestMethod method) {
        this.path = path;
        this.method = method.toString();
    }

    public PreparedRequest addHeader(String key, String value) {
        header.put(key, value);
        return this;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public PreparedRequest setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public PreparedRequest setBody(byte[] body) {
        this.body = body;
        return this;
    }

}

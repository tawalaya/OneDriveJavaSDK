package de.tuberlin.onedrivesdk.networking;

import java.util.List;

/**
 * Wrapper Interface for HTTP Responses
 */
public interface OneResponse {

	/**
	 * Gets the response body as string.
     *
	 * @return the body as string
	 */
	String getBodyAsString();

    /**
     * Gets the response body as bytes.
     *
     * @return the body as bytes
     */
    byte[] getBodyAsBytes();

	/**
	 * Gets the status code of the response.
     *
	 * @return the status code of the response
	 */
	int getStatusCode();

	/**
	 * Gets a list of all header values for the given key.
     *
	 * @param key the header name
	 * @return list of values for the requested header
	 */
	List<String> getHeaders(String key);

	/**
	 * Gets a single Header.
     *
	 * @param key key of the header
	 * @return the requested header if present
	 */
	String getHeader(String key);

    /**
     * Determines whether the request was a success (status code 200-300).
     *
     * @return whether the htp request was a successful
     */
    boolean wasSuccess();
}
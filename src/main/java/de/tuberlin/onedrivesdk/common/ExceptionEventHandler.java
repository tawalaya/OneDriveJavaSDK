package de.tuberlin.onedrivesdk.common;

/**
 * Class can be implemented to receive Exceptions from other Threads
 */
public interface ExceptionEventHandler {

    public void handle(Exception e);
    public void handle(Object src,Exception e);
}

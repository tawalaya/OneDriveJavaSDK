package de.tuberlin.onedrivesdk.common;

/**
 * Created by Andi on 20.05.2015.
 * Data object for json transport
 */
public class InnerError {
    String code;
    String message;
    String target;
    InnerError details;
    InnerError innererror;

    @Override
    public String toString() {
        return "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ((target != null) ? ", target='" + target + '\'' : "") +
                ((details != null) ? ", details: {" + details + "}" : "") +
                ((innererror != null) ? ", innererror: {" + innererror + "}" : "");
    }
}

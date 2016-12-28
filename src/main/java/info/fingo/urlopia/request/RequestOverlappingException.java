package info.fingo.urlopia.request;

/**
 * Created by Tomasz Urbas on 16.12.2016.
 */
public class RequestOverlappingException extends Exception {
    String code = "REQUEST_OVERLAPPING";

    RequestOverlappingException() {
        super();
    }

    public String getCode() {
        return code;
    }
}

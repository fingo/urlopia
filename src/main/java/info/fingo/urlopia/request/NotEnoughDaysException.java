package info.fingo.urlopia.request;

/**
 * Created by Tomasz Urbas on 16.12.2016.
 */
public class NotEnoughDaysException extends Exception {
    private String code = "NOT_ENOUGH_DAYS";

    NotEnoughDaysException() {
        super();
    }

    public String getCode() {
        return code;
    }
}

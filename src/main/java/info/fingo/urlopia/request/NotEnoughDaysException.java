package info.fingo.urlopia.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "NOT_ENOUGH_DAYS")
public class NotEnoughDaysException extends RuntimeException {
    private String code = "NOT_ENOUGH_DAYS";

    public NotEnoughDaysException() {
    }

    public String getCode() {
        return code;
    }
}

package info.fingo.urlopia.request;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "NOT_ENOUGH_DAYS")
public class NotEnoughDaysException extends BaseCustomException {
    private static final String ERROR_MSG = "Not enough days";

    public NotEnoughDaysException() {
        super(ERROR_MSG);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }
}

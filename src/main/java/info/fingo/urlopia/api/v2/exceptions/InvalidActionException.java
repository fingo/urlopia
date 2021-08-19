package info.fingo.urlopia.api.v2.exceptions;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidActionException extends BaseCustomException {
    private static final String ERROR_MESSAGE = "User tried to perform invalid action";

    private InvalidActionException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public static InvalidActionException invalidAction() {
        return new InvalidActionException(ERROR_MESSAGE);
    }
}

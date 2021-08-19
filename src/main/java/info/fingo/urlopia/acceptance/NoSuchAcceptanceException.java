package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NoSuchAcceptanceException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "There is no acceptance with given %s";

    private NoSuchAcceptanceException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public static NoSuchAcceptanceException invalidId() {
        return new NoSuchAcceptanceException(ERROR_MESSAGE.formatted("id"));
    }
}

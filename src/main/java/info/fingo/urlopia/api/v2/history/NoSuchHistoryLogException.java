package info.fingo.urlopia.api.v2.history;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NoSuchHistoryLogException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "There is no history log with given %s";

    private NoSuchHistoryLogException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public static NoSuchHistoryLogException invalidId() {
        return new NoSuchHistoryLogException(ERROR_MESSAGE.formatted("id"));
    }
}

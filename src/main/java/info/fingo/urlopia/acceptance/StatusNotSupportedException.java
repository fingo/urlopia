package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class StatusNotSupportedException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "Status not supported: %s";

    private StatusNotSupportedException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public static StatusNotSupportedException invalidStatus(String status) {
        return new StatusNotSupportedException(ERROR_MESSAGE.formatted(status));
    }
}

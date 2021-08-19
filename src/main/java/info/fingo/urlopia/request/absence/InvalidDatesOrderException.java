package info.fingo.urlopia.request.absence;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidDatesOrderException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "End date is before start date";

    private InvalidDatesOrderException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    public static InvalidDatesOrderException invalidDatesOrder() {
        return new InvalidDatesOrderException(ERROR_MESSAGE);
    }
}

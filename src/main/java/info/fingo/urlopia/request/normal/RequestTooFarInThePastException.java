package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class RequestTooFarInThePastException extends BaseCustomException {
    private static final String ERROR_MESSAGE = "User tried to add request that starts earlier than month ago";

    private RequestTooFarInThePastException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public static RequestTooFarInThePastException requestTooFarInThePast() {
        return new RequestTooFarInThePastException(ERROR_MESSAGE);
    }
}

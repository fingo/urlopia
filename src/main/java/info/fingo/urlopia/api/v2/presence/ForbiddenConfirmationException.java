package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ForbiddenConfirmationException extends BaseCustomException {
    private static final String NOT_CONFIRMING_OWN_PRESENCE_MSG = "You are not allowed to confirm presence of someone else";

    private ForbiddenConfirmationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    public static ForbiddenConfirmationException notConfirmingOwnPresence() {
        return new ForbiddenConfirmationException(NOT_CONFIRMING_OWN_PRESENCE_MSG);
    }
}

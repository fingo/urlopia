package info.fingo.urlopia.api.v2.exceptions;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseCustomException {
    private static final String ERROR_MESSAGE = "User unauthorized for this action";

    private UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    public static UnauthorizedException unauthorized() {
        return new UnauthorizedException(ERROR_MESSAGE);
    }
}

package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class WrongCredentialsException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "Incorrect password or email";

    private WrongCredentialsException(String errorMessage) {
        super(errorMessage);
    }

    public static WrongCredentialsException invalidCredentials() {
        return new WrongCredentialsException(ERROR_MESSAGE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}

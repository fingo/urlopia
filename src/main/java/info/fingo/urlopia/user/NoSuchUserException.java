package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NoSuchUserException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "There is no user with given %s";

    private static final String INACTIVE_ACCOUNT_MESSAGE = "Account for user %s is inactive";

    private NoSuchUserException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public static NoSuchUserException invalidId() {
        return new NoSuchUserException(ERROR_MESSAGE.formatted("id"));
    }

    public static NoSuchUserException invalidEmail() {
        return new NoSuchUserException(ERROR_MESSAGE.formatted("email"));
    }

    public static NoSuchUserException inactiveAccount(String mail) {
        return new NoSuchUserException(INACTIVE_ACCOUNT_MESSAGE.formatted(mail));
    }
}

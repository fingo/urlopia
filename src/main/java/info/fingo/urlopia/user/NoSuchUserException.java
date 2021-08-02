package info.fingo.urlopia.user;

public class NoSuchUserException extends RuntimeException{

    private static final String ERROR_MESSAGE = "There is no user with %s: %s";

    private NoSuchUserException(String errorMessage) {
        super(errorMessage);
    }

    public static NoSuchUserException invalidId(Long id) {
        return new NoSuchUserException(ERROR_MESSAGE.formatted("id", id.toString()));
    }

    public static NoSuchUserException invalidEmail(String email) {
        return new NoSuchUserException(ERROR_MESSAGE.formatted("email", email));
    }

    public static NoSuchUserException invalidCredentials() {
        return new NoSuchUserException("Incorrect password or email");
    }

}

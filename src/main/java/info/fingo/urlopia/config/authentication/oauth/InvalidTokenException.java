package info.fingo.urlopia.config.authentication.oauth;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, RuntimeException exception) {
        super(message, exception);
    }
}

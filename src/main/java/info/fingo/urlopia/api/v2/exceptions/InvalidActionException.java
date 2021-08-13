package info.fingo.urlopia.api.v2.exceptions;

public class InvalidActionException extends RuntimeException {
    private static final String ERROR_MESSAGE = "User tried to perform invalid action";

    private InvalidActionException(String errorMessage) {
        super(errorMessage);
    }

    public static InvalidActionException invalidAction() {
        return new InvalidActionException(ERROR_MESSAGE);
    }
}

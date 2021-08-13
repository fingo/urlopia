package info.fingo.urlopia.api.v2.exceptions;

public class UnauthorizedException extends RuntimeException {
    private static final String ERROR_MESSAGE = "User unauthorized for this action";

    private UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }

    public static UnauthorizedException unauthorized() {
        return new UnauthorizedException(ERROR_MESSAGE);
    }
}

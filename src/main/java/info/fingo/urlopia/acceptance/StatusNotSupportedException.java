package info.fingo.urlopia.acceptance;

public class StatusNotSupportedException extends RuntimeException{

    private static final String ERROR_MESSAGE = "Status not supported: %s";

    private StatusNotSupportedException(String errorMessage) {
        super(errorMessage);
    }

    public static StatusNotSupportedException invalidStatus(String status) {
        return new StatusNotSupportedException(ERROR_MESSAGE.formatted(status));
    }
}

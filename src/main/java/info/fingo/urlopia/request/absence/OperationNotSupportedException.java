package info.fingo.urlopia.request.absence;

public class OperationNotSupportedException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Operation not supported";

    private OperationNotSupportedException(String errorMessage) {
        super(errorMessage);
    }

    public static OperationNotSupportedException operationNotSupported() {
        return new OperationNotSupportedException(ERROR_MESSAGE);
    }
}

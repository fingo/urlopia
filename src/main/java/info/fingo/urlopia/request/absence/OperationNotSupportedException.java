package info.fingo.urlopia.request.absence;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class OperationNotSupportedException extends BaseCustomException {

    private static final String ERROR_MESSAGE = "Operation not supported";

    private OperationNotSupportedException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_IMPLEMENTED;
    }

    public static OperationNotSupportedException operationNotSupported() {
        return new OperationNotSupportedException(ERROR_MESSAGE);
    }
}

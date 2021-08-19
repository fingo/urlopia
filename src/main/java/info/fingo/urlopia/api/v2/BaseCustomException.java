package info.fingo.urlopia.api.v2;

import org.springframework.http.HttpStatus;

public abstract class BaseCustomException extends RuntimeException{

    protected BaseCustomException(String errorMessage) {
        super(errorMessage);
    }

    public abstract HttpStatus getHttpStatus();
}

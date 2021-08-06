package info.fingo.urlopia.api.v2;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionRestResponse handleCustomException(Exception exception) {
        var code = mapMessageToCode(exception.getMessage());
        return new ExceptionRestResponse(code);
    }

    public static record ExceptionRestResponse(String message) {
    }

    private String mapMessageToCode(String message) {
        var code = message.toUpperCase();
        return code.replaceAll("\\s", "_");
    }
}
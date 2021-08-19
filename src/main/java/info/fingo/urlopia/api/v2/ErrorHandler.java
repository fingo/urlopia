package info.fingo.urlopia.api.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ExceptionRestResponse> handleCustomException(BaseCustomException exception) {
        var code = mapMessageToCode(exception.getMessage());
        var response = new ExceptionRestResponse(code);
        var httpStatus = exception.getHttpStatus();
        return new ResponseEntity<>(response, httpStatus);
    }

    private static record ExceptionRestResponse(String message) {
    }

    private String mapMessageToCode(String message) {
        var code = message.toUpperCase();
        code = code.replaceAll("\\s", "_");
        return code.replace(":", "");
    }
}
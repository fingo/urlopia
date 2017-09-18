package info.fingo.urlopia.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "REQUEST_OVERLAPPING")
public class RequestOverlappingException extends RuntimeException {
    String code = "REQUEST_OVERLAPPING";

    RequestOverlappingException() {
    }

    public String getCode() {
        return code;
    }
}

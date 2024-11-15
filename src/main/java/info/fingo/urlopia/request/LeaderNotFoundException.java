package info.fingo.urlopia.request;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "LEADER_NOT_FOUND")
public class LeaderNotFoundException extends BaseCustomException {
    private static final String ERROR_MSG = "Leader has not been found";

    public LeaderNotFoundException() {
        super(ERROR_MSG);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }
}

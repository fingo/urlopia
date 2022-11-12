package info.fingo.urlopia.history;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "UNABLE_TO_DELETE_GIVEN_HISTORY_LOG")
public class HistoryLogNotFoundException extends BaseCustomException {
    private static final String ERROR_MSG = "Unable to delete given history log";

    public HistoryLogNotFoundException() {
        super(ERROR_MSG);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }
}

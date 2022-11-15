package info.fingo.urlopia.history;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "UNABLE_TO_DELETE_GIVEN_HISTORY_LOG")
public class HistoryLogDeleteException extends BaseCustomException {
    private static final String LOG_NOT_FOUND_MSG = "Unable to delete given history log";
    private static final String MISSING_EVENT_MSG = "Only logs with existing event can be deleted";


    private HistoryLogDeleteException(String errorMessage) {
        super(errorMessage);
    }

    public static HistoryLogDeleteException logNotFound() {
        return new HistoryLogDeleteException(LOG_NOT_FOUND_MSG);
    }

    public static HistoryLogDeleteException missingEvent() {
        return new HistoryLogDeleteException(MISSING_EVENT_MSG);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }
}

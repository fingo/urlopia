package info.fingo.urlopia.api.v2.reports;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class GenerateWorkTimeEvidenceReportException extends BaseCustomException {
    private static final String ERROR_MESSAGE = "Unable to generate report with name: %s";
    private static final String ERROR_MESSAGE_FOR_ALL = "Unable to generate evidence report for all users";

    private GenerateWorkTimeEvidenceReportException(String errorMessage){
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static GenerateWorkTimeEvidenceReportException fromIOException(String fileName){
        return new GenerateWorkTimeEvidenceReportException(ERROR_MESSAGE.formatted(fileName));
    }

    public static GenerateWorkTimeEvidenceReportException fromIOExceptionInZip() {
        return new GenerateWorkTimeEvidenceReportException(ERROR_MESSAGE_FOR_ALL);
    }
}

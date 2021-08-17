package info.fingo.urlopia.api.v2.reports;

public class GenerateWorkTimeEvidenceReportException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Unable to generate report with name: %s";

    private GenerateWorkTimeEvidenceReportException(String errorMessage){
        super(errorMessage);
    }

    public static GenerateWorkTimeEvidenceReportException fromIOException(String fileName){
        return new GenerateWorkTimeEvidenceReportException(ERROR_MESSAGE.formatted(fileName));
    }
}

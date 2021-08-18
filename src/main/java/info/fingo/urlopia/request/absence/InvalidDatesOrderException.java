package info.fingo.urlopia.request.absence;

public class InvalidDatesOrderException extends RuntimeException{

    private static final String ERROR_MESSAGE = "End date is before start date";

    private InvalidDatesOrderException(String errorMessage) {
        super(errorMessage);
    }

    public static InvalidDatesOrderException invalidDatesOrder() {
        return new InvalidDatesOrderException(ERROR_MESSAGE);
    }
}

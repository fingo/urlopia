package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class PresenceConfirmationException extends BaseCustomException {
    private static final String USER_ON_VACATION_MSG = "You cannot confirm a presence in a day you are on vacation";
    private static final String NOT_WORKING_DAY_MSG = "You cannot confirm a presence in a non working day";
    private static final String CONFIRMATION_IN_FUTURE_MSG = "You cannot confirm a presence in a future date";
    private static final String CONFIRMATION_IN_PAST_MSG = "You can only confirm a presence up to 2 weeks past";

    private PresenceConfirmationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    public static PresenceConfirmationException userOnVacation() {
        return new PresenceConfirmationException(USER_ON_VACATION_MSG);
    }

    public static PresenceConfirmationException nonWorkingDay() {
        return new PresenceConfirmationException(NOT_WORKING_DAY_MSG);
    }

    public static PresenceConfirmationException confirmationInFuture() {
        return new PresenceConfirmationException(CONFIRMATION_IN_FUTURE_MSG);
    }

    public static PresenceConfirmationException confirmationInPast() {
        return new PresenceConfirmationException(CONFIRMATION_IN_PAST_MSG);
    }
}

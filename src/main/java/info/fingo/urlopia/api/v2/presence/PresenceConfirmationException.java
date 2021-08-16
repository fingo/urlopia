package info.fingo.urlopia.api.v2.presence;

public class PresenceConfirmationException extends RuntimeException {
    private static final String FORBIDDEN_CONFIRMATION_MSG = "You are not allowed to confirm presence of someone else";
    private static final String USER_ON_VACATION_MSG = "You cannot confirm a presence in a day you are on vacation";
    private static final String NOT_WORKING_DAY_MSG = "You cannot confirm a presence in a non working day";

    private PresenceConfirmationException(String message) {
        super(message);
    }

    public static PresenceConfirmationException forbiddenConfirmation() {
        return new PresenceConfirmationException(FORBIDDEN_CONFIRMATION_MSG);
    }

    public static PresenceConfirmationException userOnVacation() {
        return new PresenceConfirmationException(USER_ON_VACATION_MSG);
    }

    public static PresenceConfirmationException nonWorkingDay() {
        return new PresenceConfirmationException(NOT_WORKING_DAY_MSG);
    }
}

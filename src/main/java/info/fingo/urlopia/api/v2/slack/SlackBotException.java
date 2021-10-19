package info.fingo.urlopia.api.v2.slack;

public class SlackBotException extends RuntimeException {
    private static final String INVALID_PRESENCE_TIME_FORMAT_MSG = "Given time format is invalid";

    private SlackBotException(String message) {
        super(message);
    }

    public static SlackBotException invalidPresenceTimeFormat() {
        return new SlackBotException(INVALID_PRESENCE_TIME_FORMAT_MSG);
    }
}

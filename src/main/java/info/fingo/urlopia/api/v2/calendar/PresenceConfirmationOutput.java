package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PresenceConfirmationOutput {
    private final boolean isConfirmed;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public static PresenceConfirmationOutput fromPresenceConfirmation(PresenceConfirmation presenceConfirmation) {
        var startTime = presenceConfirmation.getStartTime();
        var endTime = presenceConfirmation.getEndTime();
        return new PresenceConfirmationOutput(true, startTime, endTime);
    }

    public static PresenceConfirmationOutput empty() {
        return new PresenceConfirmationOutput(false, null, null);
    }

    public static PresenceConfirmationOutput unspecified() {
        return new PresenceConfirmationOutput(true, null, null);
    }
}

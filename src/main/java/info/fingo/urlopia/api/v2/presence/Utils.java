package info.fingo.urlopia.api.v2.presence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // To prevent object construction
public final class Utils {
    private static final String TIME_REGEX = "^ *(\\d{1,2}:\\d{2}|\\d{1,2}) *- *(\\d{1,2}:\\d{2}|\\d{1,2}) *$";
    private static final Pattern TIME_PATTERN = Pattern.compile(TIME_REGEX);

    public static List<LocalTime> getTimeRangeFrom(String timeRangeString) {
        var matcher = TIME_PATTERN.matcher(timeRangeString);

        if (matcher.find()) {
            var startTimeString = matcher.group(1);
            var endTimeString = matcher.group(2);
            return List.of(getLocalTimeFrom(startTimeString), getLocalTimeFrom(endTimeString));
        }

        return Collections.emptyList();
    }

    private static LocalTime getLocalTimeFrom(String timeString) {
        if (timeString.contains(":")) {
            var splitTime = timeString.split(":");
            int hour = Integer.parseInt(splitTime[0]);
            int minutes = Integer.parseInt(splitTime[1]);
            return LocalTime.of(hour, minutes);
        } else {
            int hour = Integer.parseInt(timeString);
            return LocalTime.of(hour, 0);
        }
    }
}

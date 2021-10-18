package info.fingo.urlopia.api.v2.preferences.working.hours;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@NoArgsConstructor // Required for jackson
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserWorkingHoursPreferenceDTO {
    private LocalDate changeDate;
    private Long userId;
    private Map<Integer, SingleDayHourPreferenceDTO> dayPreferences;

    public static UserWorkingHoursPreferenceDTO from(UserWorkingHoursPreference workingHoursPreference) {
        var userId = workingHoursPreference.getUserId();
        Map<Integer, SingleDayHourPreferenceDTO> dayPreferences = new HashMap<>();
        dayPreferences.put(1, dayHoursDtoFrom(workingHoursPreference.getMondayPreference()));
        dayPreferences.put(2, dayHoursDtoFrom(workingHoursPreference.getTuesdayPreference()));
        dayPreferences.put(3, dayHoursDtoFrom(workingHoursPreference.getWednesdayPreference()));
        dayPreferences.put(4, dayHoursDtoFrom(workingHoursPreference.getThursdayPreference()));
        dayPreferences.put(5, dayHoursDtoFrom(workingHoursPreference.getFridayPreference()));
        var changeDate = workingHoursPreference.getChanged().toLocalDate();
        return new UserWorkingHoursPreferenceDTO(changeDate, userId, dayPreferences);
    }

    private static SingleDayHourPreferenceDTO dayHoursDtoFrom(SingleDayHourPreference workingDayPreference) {
        return SingleDayHourPreferenceDTO.from(workingDayPreference);
    }

    public Optional<SingleDayHourPreferenceDTO> getDayPreferenceBy(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> Optional.of(dayPreferences.get(1));
            case TUESDAY -> Optional.of(dayPreferences.get(2));
            case WEDNESDAY -> Optional.of(dayPreferences.get(3));
            case THURSDAY -> Optional.of(dayPreferences.get(4));
            case FRIDAY -> Optional.of(dayPreferences.get(5));
            default -> Optional.empty();
        };
    }
}

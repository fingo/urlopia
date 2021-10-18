package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.api.v2.calendar.unspecifiedabsence.UnspecifiedAbsenceService;
import info.fingo.urlopia.api.v2.calendar.unspecifiedabsence.UsersIdentifiedDays;
import info.fingo.urlopia.api.v2.preferences.UserPreferencesService;
import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreference;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalendarOutputProvider {
    private final HolidayService holidayService;
    private final UserService userService;
    private final PresenceConfirmationService presenceConfirmationService;
    private final UnspecifiedAbsenceService unspecifiedAbsenceService;
    private final UserPreferencesService userPreferencesService;

    public CalendarOutput getCalendarOutputFor(Long userId, LocalDate startDate, LocalDate endDate, Filter userFilter) {
        var user = userService.get(userId);

        return CalendarOutputBuilder.of(user, startDate, endDate)
                .withHolidays(getAllHolidaysBetween(startDate, endDate))
                .withUsersVacationDays(getUsersVacationDaysBetween(startDate, endDate))
                .withUserPresenceConfirmations(getUserPresenceConfirmations(user, startDate, endDate))
                .withUsers(userService.get(userFilter))
                .withUserWorkingHoursPreference(userPreferencesService.getWorkingHoursPreferenceOf(userId))
                .build();
    }

    private List<Holiday> getAllHolidaysBetween(LocalDate startDate, LocalDate endDate) {
        var filter = Filter.newBuilder()
                .and("date", Operator.GREATER_OR_EQUAL, startDate.toString())
                .and("date", Operator.LESS_OR_EQUAL, endDate.toString())
                .build();

        return holidayService.getAll(filter);
    }

    private UsersIdentifiedDays getUsersVacationDaysBetween(LocalDate startDate, LocalDate endDate) {
        var activeRequesterKey = "requester.active";
        var startDateKey = "startDate";
        var endDateKey = "endDate";

        var fullyIncludedFilter = Filter.newBuilder()
                .and(activeRequesterKey, Operator.EQUAL, "true")
                .and(startDateKey, Operator.GREATER_OR_EQUAL, startDate.toString())
                .and(endDateKey, Operator.LESS_OR_EQUAL, endDate.toString())
                .build();

        var overlappingOnLeftFilter = Filter.newBuilder()
                .and(activeRequesterKey, Operator.EQUAL, "true")
                .and(startDateKey, Operator.LESS_OR_EQUAL, startDate.toString())
                .and(endDateKey, Operator.GREATER_OR_EQUAL, startDate.toString())
                .build();

        var overlappingOnRightFilter = Filter.newBuilder()
                .and(activeRequesterKey, Operator.EQUAL, "true")
                .and(startDateKey, Operator.LESS_OR_EQUAL, endDate.toString())
                .and(endDateKey, Operator.GREATER_OR_EQUAL, endDate.toString())
                .build();

        return unspecifiedAbsenceService.getUsersVacationDays(fullyIncludedFilter)
                .mergeWith(unspecifiedAbsenceService.getUsersVacationDays(overlappingOnLeftFilter))
                .mergeWith(unspecifiedAbsenceService.getUsersVacationDays(overlappingOnRightFilter));
    }

    private List<PresenceConfirmation> getUserPresenceConfirmations(User user, LocalDate startDate, LocalDate endDate) {
        var confirmations = getUserPresenceConfirmationsBetween(user, startDate, endDate);
        var userId = user.getId();
        presenceConfirmationService.getFirstUserConfirmation(userId).ifPresent(confirmations::add);
        return confirmations;
    }

    private List<PresenceConfirmation> getUserPresenceConfirmationsBetween(User user, LocalDate startDate, LocalDate endDate) {
        var filter = Filter.newBuilder()
                .and(PresenceConfirmationService.USER_ID_FROM_PRESENCE, Operator.EQUAL, user.getId().toString())
                .and(PresenceConfirmationService.DATE_FROM_PRESENCE, Operator.GREATER_OR_EQUAL, startDate.toString())
                .and(PresenceConfirmationService.DATE_FROM_PRESENCE, Operator.LESS_OR_EQUAL, endDate.toString())
                .build();

        return presenceConfirmationService.getAll(filter);
    }
}

package info.fingo.urlopia.api.v2.calendar.unspecifiedabsence;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.api.v2.preferences.UserPreferencesService;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnspecifiedAbsenceService {

    private final RequestService requestService;
    private final PresenceConfirmationService presenceConfirmationService;
    private final UserService userService;
    private final HolidayService holidayService;
    private final UserPreferencesService userPreferencesService;

    @Value("${urlopia.presence.confirmation.considered.months:2}")
    private int consideredMonthNumber;

    public UnspecifiedAbsenceOutput getEmployeesWithUnspecifiedAbsences(boolean onlyActives) {
        var users = getEmployees(onlyActives);
        var firstConsiderDate = countFirstConsiderDate();
        UsersIdentifiedDays usersConfirmedPresenceDays = getEmployeesConfirmedPresenceDays(firstConsiderDate);
        Map<Long, LocalDate> usersFirstConfirmationDates = getUsersFirstPresenceConfirmationDates(users, firstConsiderDate);

        UsersIdentifiedDays usersVacationDays = getEmployeesVacationDays(firstConsiderDate, onlyActives);
        var usersIdentifiedDays = usersVacationDays.mergeWith(usersConfirmedPresenceDays);

        usersIdentifiedDays.add(getAllHolidayDatesSince(firstConsiderDate.getYear()));
        return absenceOutputFrom(usersFirstConfirmationDates, usersIdentifiedDays);
    }

    private UnspecifiedAbsenceOutput absenceOutputFrom(Map<Long, LocalDate> usersFirstConfirmationDates,
                                                       UsersIdentifiedDays usersIdentifiedDays){
        Map<Long, List<LocalDate>> usersWithUnspecifiedAbsences = new HashMap<>();
        usersFirstConfirmationDates.forEach((userId, userFirstConfirmationDate) -> {
            var unspecifiedAbsenceDays = getDaysWhenUserHasUnspecifiedAbsence(usersIdentifiedDays,
                    userId,
                    userFirstConfirmationDate);
            if (!unspecifiedAbsenceDays.isEmpty()) {
                usersWithUnspecifiedAbsences.put(userId, unspecifiedAbsenceDays);
            }
        });
        return new UnspecifiedAbsenceOutput(usersWithUnspecifiedAbsences);
    }

    private LocalDate countFirstConsiderDate(){
        var today = LocalDate.now();
        var considerMonthDate = today.minusMonths(consideredMonthNumber);
        return LocalDate.of(considerMonthDate.getYear(), considerMonthDate.getMonth(), 1);
    }


    public UsersIdentifiedDays getUsersVacationDays(Filter requestFilter) {
        var usersIdentifiedDays = UsersIdentifiedDays.empty();

        requestFilter = requestFilter.toBuilder()
                .and("status", Operator.EQUAL, Request.Status.ACCEPTED.toString())
                .build();

        var usersAcceptedRequests = requestService.getAll(requestFilter);
        Map<Long, List<Request>> groupedRequests = groupByUserId(usersAcceptedRequests, req -> req.getRequester().getId());

        groupedRequests.forEach((userId, requests) -> {
            var dates = requests.stream()
                    .flatMap(req -> {
                        var startDate = req.getStartDate();
                        var endDate = req.getEndDate();
                        return startDate.datesUntil(endDate.plusDays(1));
                    })
                    .toList();
            usersIdentifiedDays.add(userId, dates);
        });

        return usersIdentifiedDays;
    }

    private UsersIdentifiedDays getEmployeesVacationDays(LocalDate firstPresenceConfirmationDates,
                                                         boolean onlyActive) {
        var filter = Filter.newBuilder()
                .and("requester.b2b", Operator.EQUAL, String.valueOf(false))
                .and("requester.active", Operator.EQUAL, String.valueOf(onlyActive))
                .and("startDate", Operator.GREATER_OR_EQUAL, firstPresenceConfirmationDates.toString())
                .build();

        return getUsersVacationDays(filter);
    }

    private UsersIdentifiedDays getEmployeesConfirmedPresenceDays(LocalDate firstConsiderDate) {
        var usersIdentifiedDays = UsersIdentifiedDays.empty();
        var filter = Filter.newBuilder()
                .and("presenceConfirmationId.date", Operator.GREATER_OR_EQUAL, firstConsiderDate.toString())
                .build();
        var usersPresenceConfirmations = presenceConfirmationService.getAll(filter);

        Map<Long, List<PresenceConfirmation>> groupedPresenceConfirmations =
                groupByUserId(usersPresenceConfirmations, PresenceConfirmation::getUserId);

        groupedPresenceConfirmations.forEach((userId, confirmations) -> {
            var dates = confirmations.stream()
                    .map(PresenceConfirmation::getDate)
                    .toList();
            usersIdentifiedDays.add(userId, dates);
        });

        for (var userId : groupedPresenceConfirmations.keySet()) {
            var userWorkingHoursPreference = userPreferencesService.getWorkingHoursPreferenceOf(userId);
            var changeDate = userWorkingHoursPreference.getChanged().toLocalDate();
            var today = LocalDate.now();
            // To be consistent with calendar output, i.e. new preference is valid from next day on
            var startDate = changeDate.plusDays(1).isBefore(firstConsiderDate) ? firstConsiderDate: changeDate.plusDays(1);
            if (startDate.isBefore(today)) {
                startDate.datesUntil(today)
                        .filter(day -> !usersIdentifiedDays.isIdentified(userId, day))
                        .filter(day -> userWorkingHoursPreference.isNonWorkingOn(day.getDayOfWeek()))
                        .filter(holidayService::isWeekend)
                        .forEach(day -> usersIdentifiedDays.add(userId, day));
            }
        }

        return usersIdentifiedDays;
    }

    private <T> Map<Long, List<T>> groupByUserId(List<T> objects, ToLongFunction<T> userIdProvider) {
        Map<Long, List<T>> result = new HashMap<>();

        objects.forEach(obj -> {
            var userId = userIdProvider.applyAsLong(obj);
            result.putIfAbsent(userId, new ArrayList<>());
            result.get(userId).add(obj);
        });

        return result;
    }

    private List<User> getEmployees(boolean onlyActive) {
        var filter = Filter.newBuilder()
                .and("b2b", Operator.EQUAL, String.valueOf(false))
                .and("active", Operator.EQUAL, String.valueOf(onlyActive))
                .build();
        return userService.get(filter);
    }

    private Map<Long, LocalDate> getUsersFirstPresenceConfirmationDates(List<User> users,
                                                                        LocalDate firstConsiderDate) {
        return users.stream()
                .map(User::getId)
                .map(userId -> presenceConfirmationService.getFirstUserConfirmationFromStartDate(userId, firstConsiderDate))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(PresenceConfirmation::getUserId, PresenceConfirmation::getDate));
    }

    private List<LocalDate> getDaysWhenUserHasUnspecifiedAbsence(UsersIdentifiedDays usersIdentifiedDays,
                                                                 Long userId,
                                                                 LocalDate firstConfirmationDate) {
        var today = LocalDate.now();
        return firstConfirmationDate.datesUntil(today)
                .filter(date -> !usersIdentifiedDays.isIdentified(userId, date))
                .filter(date -> !holidayService.isWeekend(date))
                .toList();
    }

    private List<LocalDate> getAllHolidayDatesSince(int startYear) {
        var dateFormatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
        var startDate = dateFormatter.format(LocalDate.of(startYear, 1, 1));
        var filter = Filter.newBuilder()
                .and("date", Operator.GREATER_OR_EQUAL, startDate)
                .build();

        return holidayService.getAll(filter).stream()
                .map(Holiday::getDate)
                .toList();
    }
}

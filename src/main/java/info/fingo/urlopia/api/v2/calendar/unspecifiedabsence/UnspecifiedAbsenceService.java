package info.fingo.urlopia.api.v2.calendar.unspecifiedabsence;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.api.v2.request.RequestStatus;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
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

    public UnspecifiedAbsenceOutput getEmployeesWithUnspecifiedAbsences() {
        Map<Long, List<LocalDate>> usersWithUnspecifiedAbsences = new HashMap<>();

        UsersIdentifiedDays usersVacationDays = getEmployeesVacationDays();
        UsersIdentifiedDays usersConfirmedPresenceDays = getEmployeesConfirmedPresenceDays();
        var usersIdentifiedDays = usersVacationDays.mergeWith(usersConfirmedPresenceDays);

        var users = getEmployeesOnly();
        Map<Long, LocalDate> usersFirstConfirmationDates = getUsersFirstPresenceConfirmationDates(users);
        var yearOfFirstEverConfirmation = getYearOfEarliestDate(usersFirstConfirmationDates.values());

        usersIdentifiedDays.add(getAllHolidayDatesSince(yearOfFirstEverConfirmation));

        usersFirstConfirmationDates.forEach((userId, firstConfirmationDate) -> {
            var unspecifiedAbsenceDays = getDaysWhenUserHasUnspecifiedAbsence(usersIdentifiedDays,
                                                                              userId,
                                                                              firstConfirmationDate);
            if (!unspecifiedAbsenceDays.isEmpty()) {
                usersWithUnspecifiedAbsences.put(userId, unspecifiedAbsenceDays);
            }
        });

        return new UnspecifiedAbsenceOutput(usersWithUnspecifiedAbsences);
    }

    private UsersIdentifiedDays getEmployeesVacationDays() {
        var usersIdentifiedDays = UsersIdentifiedDays.empty();

        var filter = Filter.newBuilder()
                .and("requester.b2b", Operator.EQUAL, String.valueOf(false))
                .and("status", Operator.EQUAL, Request.Status.ACCEPTED.toString())
                .build();

        var usersAcceptedRequests = requestService.getAll(filter);
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

    private UsersIdentifiedDays getEmployeesConfirmedPresenceDays() {
        var usersIdentifiedDays = UsersIdentifiedDays.empty();

        var usersPresenceConfirmations = presenceConfirmationService.getAll(Filter.empty());
        Map<Long, List<PresenceConfirmation>> groupedPresenceConfirmations =
                groupByUserId(usersPresenceConfirmations, PresenceConfirmation::getUserId);

        groupedPresenceConfirmations.forEach((userId, confirmations) -> {
            var dates = confirmations.stream()
                    .map(PresenceConfirmation::getDate)
                    .toList();
            usersIdentifiedDays.add(userId, dates);
        });

        return usersIdentifiedDays;
    }

    private <T> Map<Long, List<T>> groupByUserId(List<T> objects, ToLongFunction<T> userIdProvider) {
        Map<Long, List<T>> result = new HashMap<>();

        objects.forEach(obj -> {
            var userId = userIdProvider.applyAsLong(obj);
            result.putIfAbsent(userId, new LinkedList<>());
            result.get(userId).add(obj);
        });

        return result;
    }

    private List<User> getEmployeesOnly() {
        var filter = Filter.newBuilder()
                .and("b2b", Operator.EQUAL, String.valueOf(false))
                .build();
        return userService.get(filter);
    }

    private Map<Long, LocalDate> getUsersFirstPresenceConfirmationDates(List<User> users) {
        return users.stream()
                .map(User::getId)
                .map(presenceConfirmationService::getFirstUserConfirmation)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(PresenceConfirmation::getUserId, PresenceConfirmation::getDate));
    }

    private int getYearOfEarliestDate(Collection<LocalDate> dates) {
        return dates.stream()
                .map(LocalDate::getYear)
                .sorted()
                .findFirst()
                .orElse(LocalDate.now().getYear());
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

package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.api.v2.calendar.unspecifiedabsence.UnspecifiedAbsenceService;
import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private final CalendarOutputProvider calendarOutputProvider;
    private final UnspecifiedAbsenceService unspecifiedAbsenceService;

    public CalendarOutput getCalendarInfo(Long authenticatedId,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          Filter filter) {
        return calendarOutputProvider.getCalendarOutputFor(authenticatedId, startDate, endDate, filter);
    }

    public UsersVacationDaysOutput getUserVacationsOf(Long userId) {
        var filter = Filter.newBuilder()
                .and("requester.active", Operator.EQUAL, "true")
                .and("requester.id", Operator.EQUAL, userId.toString())
                .build();

        Map<LocalDate, List<Long>> usersVacations = new HashMap<>();
        var vacationDays = unspecifiedAbsenceService.getUsersVacationDays(filter);

        for (var date : vacationDays.ofUser(userId)) {
            usersVacations.computeIfAbsent(date, d -> new LinkedList<>());
            usersVacations.get(date).add(userId);
        }

        return new UsersVacationDaysOutput(usersVacations);
    }
}

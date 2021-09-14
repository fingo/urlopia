package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.absence.InvalidDatesOrderException;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final HistoryLogService historyLogService;
    private final HolidayService holidayService;
    private final RequestService requestService;
    private final UserService userService;
    private final PresenceConfirmationService presenceConfirmationService;

    private CurrentUserInformationOutput getCurrentUserInformation(Long userId,
                                                                   LocalDate date) {
        var currentUserInformation = new CurrentUserInformationOutput();
        var user = userService.get(userId);
        currentUserInformation.setAbsent(requestService.isVacationing(user, date));

        var presenceConfirmationOutput = presenceConfirmationService.getPresenceConfirmation(userId, date)
                .map(PresenceConfirmationOutput::fromPresenceConfirmation)
                .orElse(getFallbackValue(userId, date));
        currentUserInformation.setPresenceConfirmation(presenceConfirmationOutput);

        var historyLogExcerptProjections = historyLogService.get(date, userId);
        var vacationHoursModifications = historyLogExcerptProjections.stream()
                .map(VacationHoursModificationOutput::fromHistoryLogExcerptProjection)
                .toList();

        currentUserInformation.setVacationHoursModifications(vacationHoursModifications);
        return currentUserInformation;
    }

    private PresenceConfirmationOutput getFallbackValue(Long userId, LocalDate date) {
        return presenceConfirmationService.getFirstUserConfirmation(userId)
                .map(firstConfirmation -> {
                    if (date.isBefore(firstConfirmation.getDate())) {
                        return PresenceConfirmationOutput.unspecified();
                    }
                    return PresenceConfirmationOutput.empty();
                })
                .orElse(PresenceConfirmationOutput.unspecified());
    }

    private SingleDayOutput getSingleDayInfo(Long userId,
                                            LocalDate date,
                                            Filter filter) {
        var singleDayOutput = new SingleDayOutput();
        singleDayOutput.setWorkingDay(holidayService.isWorkingDay(date));
        var holiday = holidayService.getByDate(date);
        singleDayOutput.setHolidays(holiday.stream()
                .map(Holiday::getName)
                .toList());

        var vacationingUsers = requestService.getVacations(date, filter);
        singleDayOutput.setAbsentUsers(vacationingUsers);

        singleDayOutput.setCurrentUserInformation(getCurrentUserInformation(userId, date));
        return singleDayOutput;
    }

    public CalendarOutput getCalendarInfo(Long authenticatedId,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          Filter filter) {
        var calendar = new HashMap<LocalDate, SingleDayOutput>();
        if (endDate.isBefore(startDate)) {
            throw InvalidDatesOrderException.invalidDatesOrder();
        }
        startDate.datesUntil(endDate.plusDays(1))
                .forEach(date -> calendar.put(date, getSingleDayInfo(authenticatedId, date, filter)));
        return new CalendarOutput(calendar);
    }
}

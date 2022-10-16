package info.fingo.urlopia.api.v2.reports.attendance.resolver.handlers.user.params.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.history.HistoryLogExcerptProjection;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ReportStatusFromRequestType;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;

@RequiredArgsConstructor
public class MonthlyAttendanceListReportDayHandler {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
    private static final String DEFAULT_VALUE = "-";
    private static final String EMPTY_VALUE = "";
    private final HolidayService holidayService;
    private final RequestService requestService;
    private final PresenceConfirmationService presenceConfirmationService;
    private final HistoryLogService historyLogService;


    public String handle(int year,
                         int month,
                         int dayOfMonth,
                         User user) {
        if (user == null) {
            return EMPTY_VALUE;
        }
        try {
            var handleDate = LocalDate.of(year, month, dayOfMonth);

            if (holidayService.isWorkingDay(handleDate)) {
                if (shouldReturnDefaultValue(handleDate, user)) {
                    return DEFAULT_VALUE;
                }
                var resolvedValue = requestService
                                    .getByUserAndDate(user.getId(), handleDate).stream()
                                    .filter(req -> req.getStatus() == Request.Status.ACCEPTED)
                                    .map(this::handleRequest)
                                    .findFirst()
                                    .orElse(handlePresence(handleDate, user));
                if (shouldReturnEmptyValue(resolvedValue, handleDate, user)) {
                    return EMPTY_VALUE;
                }
                return resolvedValue;
            }
        } catch (DateTimeException e) {
            // if day does not exist then default value
        }
        return DEFAULT_VALUE;
    }

    private String handleRequest(Request request) {
        var requestType = request.getType();
        var specialTypeInfo = request.getSpecialTypeInfo();
        return switch (requestType) {
            case NORMAL -> ReportStatusFromRequestType.NORMAL.getMonthlyPresenceReportStatus();
            case OCCASIONAL -> ReportStatusFromRequestType.OCCASIONAL.getMonthlyPresenceReportStatus();
            case SPECIAL -> ReportStatusFromRequestType.valueOf(specialTypeInfo).getMonthlyPresenceReportStatus();
        };
    }

    private String handlePresence(LocalDate date,
                                  User user) {
        var presenceConfirmations = presenceConfirmationService.getByUserAndDate(user.getId(), date);
        if (presenceConfirmations.isEmpty()) {
            return DEFAULT_VALUE;
        }
        return DECIMAL_FORMAT.format(presenceConfirmationService.countWorkingHoursInDay(presenceConfirmations.get(0)));
    }

    private boolean isDefault(String resolvedValue) {
        return DEFAULT_VALUE.equals(resolvedValue);
    }

    private boolean shouldReturnEmptyValue(String resolvedValue,
                                           LocalDate handleDate,
                                           User user) {
        var currentDate = LocalDate.now();
        var lastValidDate = dateOfFirstInvalidResult(handleDate, user).minusDays(1);
        var isDayInPast = handleDate.isBefore(currentDate);

        var dateIsInvalid = handleDate.isAfter(lastValidDate);
        var valueIsInvalid = isDefault(resolvedValue) && !isDayInPast;
        return dateIsInvalid || valueIsInvalid;
    }

    private boolean shouldReturnDefaultValue(LocalDate handleDate,
                                             User user) {
        var lastValidDate = dateOfFirstInvalidResult(handleDate, user).minusDays(1);

        return handleDate.isAfter(lastValidDate);
    }

    private LocalDate dateOfFirstInvalidResult(LocalDate handleDate,
                                               User user) {
        var logs =  historyLogService.get(user.getId(), YearMonth.from(handleDate), UserDetailsChangeEvent.USER_CHANGE_TO_B2B);
        return logs.stream()
                .map(HistoryLogExcerptProjection::getCreated)
                .map(LocalDate::from)
                .sorted()
                .findFirst()
                .orElse(handleDate.plusMonths(1));

    }
}
package info.fingo.urlopia.api.v2.reports.attendance.resolver.handlers.user.params.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ReportStatusFromRequestType;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;

import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;

public class MonthlyAttendanceListReportDayHandler {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
    private static final String DEFAULT_VALUE = "-";
    private static final String EMPTY_VALUE = "";
    private final HolidayService holidayService;
    private final RequestService requestService;
    private final PresenceConfirmationService presenceConfirmationService;


    public MonthlyAttendanceListReportDayHandler(HolidayService holidayService,
                                                 RequestService requestService,
                                                 PresenceConfirmationService presenceConfirmationService) {
        this.holidayService = holidayService;
        this.requestService = requestService;
        this.presenceConfirmationService = presenceConfirmationService;
    }

    public String handle(int year,
                         int month,
                         int dayOfMonth,
                         User user) {
        if (user == null) {
            return EMPTY_VALUE;
        }
        var currentDate = LocalDate.now();
        try {
            var handleDate = LocalDate.of(year, month, dayOfMonth);
            var isDateInPast = handleDate.isBefore(currentDate);


            if (holidayService.isWorkingDay(handleDate)) {
                var resolvedValue = requestService
                                    .getByUserAndDate(user.getId(), handleDate).stream()
                                    .filter(req -> req.getStatus() == Request.Status.ACCEPTED)
                                    .map(this::handleRequest)
                                    .findFirst()
                                    .orElse(handlePresence(handleDate, user));
                if (shouldReturnEmptyValue(resolvedValue, isDateInPast)) {
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
                                           boolean isDayInPast) {
        return isDefault(resolvedValue) && !isDayInPast;
    }
}

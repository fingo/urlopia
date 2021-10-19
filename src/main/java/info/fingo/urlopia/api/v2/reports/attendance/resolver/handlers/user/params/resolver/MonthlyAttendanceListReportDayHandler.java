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
        var currentDate = LocalDate.now();
        var currentDateYear = currentDate.getYear();
        var currentDateMonth = currentDate.getMonthValue();
        var isDateInFuture = year > currentDateYear || year == currentDateYear && month >= currentDateMonth;

        if (user == null || isDateInFuture) {
            return "";
        }

        try {
            var date = LocalDate.of(year, month, dayOfMonth);
            if (holidayService.isWorkingDay(date)) {
                return requestService
                        .getByUserAndDate(user.getId(), date).stream()
                        .filter(req -> req.getStatus() == Request.Status.ACCEPTED)
                        .map(this::handleRequest)
                        .findFirst()
                        .orElse(handlePresence(date,user));
            }
        } catch (DateTimeException e) {
            // if day does not exist then default value
        }

        return "-";
    }

    private String handleRequest(Request request){
        var requestType = request.getType();
        var specialTypeInfo = request.getSpecialTypeInfo();
        return switch (requestType) {
            case NORMAL -> ReportStatusFromRequestType.NORMAL.getMonthlyPresenceReportStatus();
            case OCCASIONAL -> ReportStatusFromRequestType.OCCASIONAL.getMonthlyPresenceReportStatus();
            case SPECIAL ->  ReportStatusFromRequestType.valueOf(specialTypeInfo).getMonthlyPresenceReportStatus();
        };
    }

    private String handlePresence(LocalDate date,
                                  User user){
        var presenceConfirmations = presenceConfirmationService.getByUserAndDate(user.getId(),date);
        if(presenceConfirmations.isEmpty()){
            return "-";
        }
        return DECIMAL_FORMAT.format(presenceConfirmationService.countWorkingHoursInDay(presenceConfirmations.get(0)));
    }
}

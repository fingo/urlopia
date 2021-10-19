package info.fingo.urlopia.api.v2.reports.attendance.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.api.v2.reports.attendance.resolver.handlers.user.params.resolver.MonthlyAttendanceListReportDayHandler;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;

import java.util.HashMap;
import java.util.Map;

public class MonthlyAttendanceListReportUserParamsResolver implements ParamResolver {
    private static final int NUMBERS_OF_DAYS = 31;
    private final User user;
    private final int year;
    private final int month;
    private final MonthlyAttendanceListReportDayHandler monthlyAttendanceListReportDayHandler;


    public MonthlyAttendanceListReportUserParamsResolver(User user,
                                                         int year,
                                                         int month,
                                                         HolidayService holidayService,
                                                         RequestService requestService,
                                                         PresenceConfirmationService presenceConfirmationService) {
        this.user = user;
        this.year = year;
        this.month = month;
        this.monthlyAttendanceListReportDayHandler = new MonthlyAttendanceListReportDayHandler(holidayService,
                                                                                         requestService,
                                                                                         presenceConfirmationService);
    }

    public Map<String, String> resolve() {
        var params = new HashMap<String, String>();
        resolveFullName(params);
        resolveDays(params);
        return params;
    }

    private void resolveFullName(Map<String, String> params) {
        params.put("fullName", user == null ? "" : user.getFullName());
    }

    private void resolveDays(Map<String, String> params) {
        for (int day = 1; day <= NUMBERS_OF_DAYS; day++) {
            var dayStatus = monthlyAttendanceListReportDayHandler.handle(year, month, day, user);
            var paramName = String.format("day%02d", day);
            params.put(paramName, dayStatus);
        }
    }

}

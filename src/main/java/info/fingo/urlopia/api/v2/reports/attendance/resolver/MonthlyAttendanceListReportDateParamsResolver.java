package info.fingo.urlopia.api.v2.reports.attendance.resolver;

import info.fingo.urlopia.reports.ParamResolver;

import java.util.Map;

public class MonthlyAttendanceListReportDateParamsResolver implements ParamResolver {
    private final int year;
    private final int month;

    public MonthlyAttendanceListReportDateParamsResolver(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public Map<String, String> resolve() {
        return Map.of("month", String.valueOf(month),
                      "year", String.valueOf(year));
    }
}

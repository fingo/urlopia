package info.fingo.urlopia.api.v2.reports.attendance;

import info.fingo.urlopia.reports.Report;

public class MonthlyAttendanceListReport implements Report<MonthlyAttendanceListReportModel> {
    @Override
    public String templateName() {
        return "attendance_list.xlsx";
    }

    @Override
    public String mimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String fileName(MonthlyAttendanceListReportModel model) {
        return String.format("lista_obecności_%s_%s.xlsx",
                model.getValue("reportDate.month"),
                model.getValue("reportDate.year"));
    }
}

package info.fingo.urlopia.reports.evidence;

import info.fingo.urlopia.reports.Report;

public class EvidenceReport implements Report<EvidenceReportModel> {

    @Override
    public String templateName() {
        return "working_time_evidence.xlsx";
    }

    @Override
    public String mimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String fileName(EvidenceReportModel model) {
        return String.format("ewidencja_czasu_pracy_%s_%s%s.xlsx",
                model.getValue("reportDate.year"),
                model.getValue("user.lastName"),
                model.getValue("user.firstName"));
    }

}

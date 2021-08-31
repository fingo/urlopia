package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver;

import info.fingo.urlopia.reports.ReportStatusFromRequestType;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.absence.SpecialAbsenceReason;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EvidenceReportStatusFromRequestHandler {

    public String handle(Request request){
        var requestType = request.getType();
        return switch (requestType) {
            case NORMAL -> ReportStatusFromRequestType.NORMAL.getEvidenceReportStatus();
            case OCCASIONAL -> ReportStatusFromRequestType.OCCASIONAL.getEvidenceReportStatus();
            case SPECIAL -> mapSpecialAbsenceReasonToEvidenceReportStatus(request);
        };
    }

    private String mapSpecialAbsenceReasonToEvidenceReportStatus(Request request){
        var specialTypeInfo = request.getSpecialTypeInfo();
        if (SpecialAbsenceReason.valueOf(specialTypeInfo) == SpecialAbsenceReason.DELEGATION) {
            return "" + request.getRequester().getWorkTime();
        }
        return ReportStatusFromRequestType.valueOf(specialTypeInfo).getEvidenceReportStatus();

    }
}

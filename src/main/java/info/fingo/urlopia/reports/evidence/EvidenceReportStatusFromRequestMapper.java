package info.fingo.urlopia.reports.evidence;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.absence.SpecialAbsenceReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EvidenceReportStatusFromRequestMapper {

    public String getEvidenceReportStatusFromRequest(Request request){
        var requestType = request.getType();
        return switch (requestType) {
            case NORMAL -> ReportStatusFromReportType.NORMAL.getEvidenceReportStatus();
            case OCCASIONAL -> ReportStatusFromReportType.OCCASIONAL.getEvidenceReportStatus();
            case SPECIAL -> mapSpecialAbsenceReasonToEvidenceReportStatus(request);
        };
    }

    private String mapSpecialAbsenceReasonToEvidenceReportStatus(Request request){
        var specialTypeInfo = request.getSpecialTypeInfo();
        if (SpecialAbsenceReason.valueOf(specialTypeInfo) == SpecialAbsenceReason.DELEGATION) {
            return "" + request.getRequester().getWorkTime();
        }
        return ReportStatusFromReportType.valueOf(specialTypeInfo).getEvidenceReportStatus();

    }

}

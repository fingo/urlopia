package info.fingo.urlopia.reports.evidence;

public record EvidenceReportPresenceConfirmationTimeDTO(String startTimeResolve,
                                                        String endTimeResolve) {
    public static EvidenceReportPresenceConfirmationTimeDTO absent(){
        return new EvidenceReportPresenceConfirmationTimeDTO("-","-");
    }

    public static EvidenceReportPresenceConfirmationTimeDTO unspecified(){
        return new EvidenceReportPresenceConfirmationTimeDTO("","");
    }
}

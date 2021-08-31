package info.fingo.urlopia.reports;

public enum ReportStatusFromRequestType {
    NORMAL("uw"),
    OCCASIONAL("uo"),
    ADDITIONAL_CARE_ALLOWANCE_PANDEMIC("dzo"),
    BLOOD_DONATION("nup"),
    BLOOD_DONATION_PANDEMIC("nup"),
    DELEGATION(null),
    UNPAID_LEAVE("ub"),
    PARENTAL_LEAVE("r"),
    PATERNITY_LEAVE("oj"),
    MATERNITY_LEAVE("m"),
    SICK_LEAVE_EMPLOYEE("c"),
    SICK_LEAVE_CHILD("cd"),
    SICK_LEAVE_FAMILY("co"),
    UNEXCUSED("nn"),
    CHILDCARE("w"),
    CHILDCARE_FOR_14_YEARS_OLD("uop"),
    OTHER("??"),
    WRONG("error");


    private String evidenceReportStatus;

    ReportStatusFromRequestType(String evidenceReportStatus) {
        this.evidenceReportStatus = evidenceReportStatus;
    }

    public String getEvidenceReportStatus() {
        return evidenceReportStatus;
    }
}

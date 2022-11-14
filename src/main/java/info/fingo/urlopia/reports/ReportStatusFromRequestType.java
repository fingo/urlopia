package info.fingo.urlopia.reports;

public enum ReportStatusFromRequestType {
    NORMAL("uw","uw"),
    OCCASIONAL("uo","nu"),
    ADDITIONAL_CARE_ALLOWANCE_PANDEMIC("dzo","nu"),
    BLOOD_DONATION("nup","nu"),
    BLOOD_DONATION_PANDEMIC("nup","nu"),
    DELEGATION(null,"d"),
    UNPAID_LEAVE("ub","ub"),
    PARENTAL_LEAVE("r","nu"),
    PATERNITY_LEAVE("oj","nu"),
    MATERNITY_LEAVE("m","nu"),
    SICK_LEAVE_EMPLOYEE("c","nu"),
    SICK_LEAVE_CHILD("cd","nu"),
    SICK_LEAVE_FAMILY("co","nu"),
    UNEXCUSED("nn","nu"),
    EXCUSED_UNPAID("nun", "nu"),
    CHILDCARE("w","nu"),
    CHILDCARE_FOR_14_YEARS_OLD("uop","nu"),
    OTHER("nupi","nu"),
    QUARANTINE_OR_ISOLATION("k/i","nu"),
    WRONG("error","error");


    private final String evidenceReportStatus;
    private final String monthlyPresenceReportStatus;

    ReportStatusFromRequestType(String evidenceReportStatus,
                                String monthlyPresenceReportStatus){
            this.evidenceReportStatus = evidenceReportStatus;
            this.monthlyPresenceReportStatus = monthlyPresenceReportStatus;
        }

        public String getEvidenceReportStatus() {
            return evidenceReportStatus;
        }

        public String getMonthlyPresenceReportStatus() {
            return monthlyPresenceReportStatus;
        }

    }

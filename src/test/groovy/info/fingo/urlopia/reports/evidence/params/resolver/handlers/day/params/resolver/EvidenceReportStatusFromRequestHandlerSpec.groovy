package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver

import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.request.absence.SpecialAbsenceReason
import info.fingo.urlopia.user.User
import spock.lang.Specification

class EvidenceReportStatusFromRequestHandlerSpec extends Specification {
    private EvidenceReportStatusFromRequestHandler evidenceReportStatusFromRequestHandler;
    private Request request;
    private static final EXPECTED_NORMAL_STATUS = "uw"
    private static final EXPECTED_OCCASIONAL_STATUS = "uo";
    private static final EXPECTED_ADDITIONAL_CARE_ALLOWANCE_PANDEMIC_STATUS = "dzo";
    private static final EXPECTED_BLOOD_DONATION_STATUS = "nup";
    private static final EXPECTED_BLOOD_DONATION_PANDEMIC_STATUS = "nup";
    private static final EXPECTED_UNPAID_LEAVE_STATUS = "ub";
    private static final EXPECTED_PARENTAL_LEAVE_STATUS = "r";
    private static final EXPECTED_MATERNITY_LEAVE_STATUS = "m"
    private static final EXPECTED_PATERNITY_LEAVE_STATUS = "oj";
    private static final EXPECTED_SICK_LEAVE_EMPLOYEE_STATUS = "c";
    private static final EXPECTED_SICK_LEAVE_CHILD_STATUS = "cd"
    private static final EXPECTED_SICK_LEAVE_FAMILY_STATUS = "co"
    private static final EXPECTED_UNEXCUSED_STATUS = "nn";
    private static final EXPECTED_CHILDCARE_STATUS = "w";
    private static final EXPECTED_CHILDCARE_FOR_14_YEARS_OLD_STATUS = "uop";
    private static final EXPECTED_OTHER_STATUS = "??";
    private static final EXPECTED_WRONG_STATUS = "error";


    void setup() {
        request = Mock(Request)
        evidenceReportStatusFromRequestHandler = new EvidenceReportStatusFromRequestHandler()
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with NORMAL request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.NORMAL
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_NORMAL_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with OCCASIONAL request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.OCCASIONAL
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_OCCASIONAL_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with ADDITIONAL_CARE_ALLOWANCE_PANDEMIC request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.ADDITIONAL_CARE_ALLOWANCE_PANDEMIC.name();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_ADDITIONAL_CARE_ALLOWANCE_PANDEMIC_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with BLOOD_DONATION request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.BLOOD_DONATION.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_BLOOD_DONATION_STATUS


    }

    def "getEvidenceReportStatusFromRequest() WHEN called with BLOOD_DONATION_PANDEMIC request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.BLOOD_DONATION_PANDEMIC.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_BLOOD_DONATION_PANDEMIC_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with UNPAID_LEAVE request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.UNPAID_LEAVE.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_UNPAID_LEAVE_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with PARENTAL_LEAVE request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.PARENTAL_LEAVE.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_PARENTAL_LEAVE_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with MATERNITY_LEAVE request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.MATERNITY_LEAVE.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_MATERNITY_LEAVE_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with PATERNITY_LEAVE request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.PATERNITY_LEAVE.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_PATERNITY_LEAVE_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with SICK_LEAVE_EMPLOYEE request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.SICK_LEAVE_EMPLOYEE.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_SICK_LEAVE_EMPLOYEE_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with SICK_LEAVE_CHILD request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.SICK_LEAVE_CHILD.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_SICK_LEAVE_CHILD_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with SICK_LEAVE_FAMILY request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.SICK_LEAVE_FAMILY.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_SICK_LEAVE_FAMILY_STATUS
    }


    def "getEvidenceReportStatusFromRequest() WHEN called with UNEXCUSED request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.UNEXCUSED.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_UNEXCUSED_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with CHILDCARE request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.CHILDCARE.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_CHILDCARE_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with CHILDCARE_FOR_14_YEARS_OLD request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.CHILDCARE_FOR_14_YEARS_OLD.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_CHILDCARE_FOR_14_YEARS_OLD_STATUS

    }

    def "getEvidenceReportStatusFromRequest() WHEN called with  OTHER request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.OTHER.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_OTHER_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with  WRONG request SHOULD return expected status"() {
        given:
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.WRONG.toString();
        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == EXPECTED_WRONG_STATUS
    }

    def "getEvidenceReportStatusFromRequest() WHEN called with DELEGATION request SHOULD return requester working hours"() {
        given:
        def workTime = 7.0f
        def user = Mock(User) {
            getWorkTime() >> workTime
        }
        request.getType() >> RequestType.SPECIAL
        request.getSpecialTypeInfo() >> SpecialAbsenceReason.DELEGATION.toString();
        request.getRequester() >> user

        when:
        def result = evidenceReportStatusFromRequestHandler.handle(request)
        then:
        result == "" + workTime
    }
}

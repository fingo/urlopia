package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver

import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.request.absence.SpecialAbsenceReason
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class EvidenceReportWeekendHandlerSpec extends Specification {
    def SPECIAL_REQUESTS_TYPES_NEEDS_TO_BE_HANDLE = [SpecialAbsenceReason.CHILDCARE,
                                                     SpecialAbsenceReason.PARENTAL_LEAVE,
                                                     SpecialAbsenceReason.MATERNITY_LEAVE,
                                                     SpecialAbsenceReason.PATERNITY_LEAVE]
    def requestService = Mock(RequestService)
    def evidenceReportStatusFromRequestHandler = Mock(EvidenceReportStatusFromRequestHandler)
    def evidenceReportWeekendHandler = new EvidenceReportWeekendHandler(requestService,
            evidenceReportStatusFromRequestHandler)
    def SUNDAY_STATUS = "wn";
    def SATURDAY_STATUS = "ws"
    def userId = 5
    def user = Mock(User) {
        getId() >> userId
    }


    def "handle() WHEN called without requests that needs to be handle and  on Saturday date SHOULD return Saturday status"() {
        given:
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> []

        and: "saturday date"
        def date = LocalDate.of(2021, 1, 2)

        when:
        def result = evidenceReportWeekendHandler.handle(user, date)

        then:
        result == SATURDAY_STATUS
    }

    def "handle() WHEN called without requests that needs to be handle and  on Sunday date SHOULD return Sunday status"() {
        given:
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> []

        and: "sunday date"
        def date = LocalDate.of(2021, 1, 3)

        when:
        def result = evidenceReportWeekendHandler.handle(user, date)

        then:
        result == SUNDAY_STATUS
    }

    def "handle() WHEN called with requests that needs to be handle SHOULD RETURN HIS STATUS"() {
        given:
        def request = Mock(Request) {
            getType() >> RequestType.SPECIAL
            getSpecialTypeInfo() >> SpecialAbsenceReason.CHILDCARE.toString()
        }
        def status = "default"
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> [request]
        evidenceReportStatusFromRequestHandler.handle(_ as Request) >> status
        def date = LocalDate.of(2021, 1, 3)

        when:
        def result = evidenceReportWeekendHandler.handle(user, date)

        then:
        result == status
    }


}

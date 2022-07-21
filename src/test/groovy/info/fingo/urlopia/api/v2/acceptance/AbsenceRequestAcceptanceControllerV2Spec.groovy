package info.fingo.urlopia.api.v2.acceptance

import info.fingo.urlopia.acceptance.Acceptance
import info.fingo.urlopia.acceptance.AcceptanceExcerptProjection
import info.fingo.urlopia.acceptance.AcceptanceService
import info.fingo.urlopia.api.v2.exceptions.InvalidActionException
import info.fingo.urlopia.config.authentication.UserIdInterceptor
import info.fingo.urlopia.config.persistance.filter.Filter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.util.function.Function

class AbsenceRequestAcceptanceControllerV2Spec extends Specification {
    def leaderId = 1L
    def filtersString = [] as String[]
    def pageable = Mock(Pageable)
    def httpRequest = Mock(HttpServletRequest) {
        getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE) >> leaderId
    }

    def expectedAcceptances = Mock(Page<AcceptancesOutput>)

    def returnedPage = Mock(Page<AcceptanceExcerptProjection>) {
        map(_ as Function) >> expectedAcceptances
    }

    def acceptanceId = 1L

    def acceptanceService = Mock(AcceptanceService)
    def absenceRequestAcceptanceControllerV2 = new AbsenceRequestAcceptanceControllerV2(acceptanceService)

    def "getAcceptances() should return acceptances"() {
        given:
        acceptanceService.get(leaderId, _ as Filter, pageable) >> returnedPage

        when:
        def acceptances = absenceRequestAcceptanceControllerV2.getAcceptances(filtersString,
                pageable,
                httpRequest)

        then:
        expectedAcceptances == acceptances
    }

    def "updateAcceptanceStatus() when status is accepted should return status accepted"() {
        given:
        def acceptanceStatus = new AcceptanceStatus(Acceptance.Status.ACCEPTED)
        def returnedAcceptance = Mock(Acceptance) {
            getStatus() >> Acceptance.Status.ACCEPTED
        }

       acceptanceService.getAcceptance(acceptanceId) >> returnedAcceptance

        when:
        def status = absenceRequestAcceptanceControllerV2.updateAcceptanceStatus(acceptanceId,
                acceptanceStatus,
                httpRequest)

        then:
        acceptanceStatus == status
    }

    def "updateAcceptanceStatus() when status is rejected should return status rejected"() {
        given:
        def acceptanceStatus = new AcceptanceStatus(Acceptance.Status.REJECTED)
        def returnedAcceptance = Mock(Acceptance) {
            getStatus() >> Acceptance.Status.REJECTED
        }

        acceptanceService.getAcceptance(acceptanceId) >> returnedAcceptance


        when:
        def status = absenceRequestAcceptanceControllerV2.updateAcceptanceStatus(acceptanceId,
                acceptanceStatus,
                httpRequest)

        then:
        acceptanceStatus == status
    }

    def "updateAcceptanceStatus() when status is invalid should InvalidActionException"() {
        given:
        def acceptanceStatus = new AcceptanceStatus(Acceptance.Status.EXPIRED)
        def returnedAcceptance = Mock(Acceptance) {
            getStatus() >> Acceptance.Status.EXPIRED
        }

        acceptanceService.getAcceptance(acceptanceId) >> returnedAcceptance

        when:
        absenceRequestAcceptanceControllerV2.updateAcceptanceStatus(acceptanceId, acceptanceStatus, httpRequest)

        then:
        thrown(InvalidActionException)
    }
}

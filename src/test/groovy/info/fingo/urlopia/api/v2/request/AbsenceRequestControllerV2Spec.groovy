package info.fingo.urlopia.api.v2.request

import info.fingo.urlopia.acceptance.AcceptanceService
import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import info.fingo.urlopia.api.v2.request.AbsenceRequestControllerV2
import info.fingo.urlopia.config.authentication.WebTokenService
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.request.absence.BaseRequestInput
import info.fingo.urlopia.request.absence.SpecialAbsence
import info.fingo.urlopia.request.absence.SpecialAbsenceReason
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class AbsenceRequestControllerV2Spec extends Specification{
    def requesterId = 1L
    def requester = Mock(User) {
        getId() >> requesterId
    }
    def startDate = LocalDate.now()
    def endDate = LocalDate.now()
    def reason = SpecialAbsenceReason.OTHER
    def workingDays = 1

    def specialAbsence = new SpecialAbsence(requesterId, startDate, endDate, reason)
    def request = new Request(requester, startDate, endDate, workingDays, reason.toString())

    def requestService = Mock(RequestService)
    def acceptanceService = Mock(AcceptanceService)
    def webTokenService = Mock(WebTokenService)

    def absenceRequestControllerV2 = new AbsenceRequestControllerV2(acceptanceService, requestService)

    def "createSpecialAbsence() WHEN called by admin SHOULD return instance of RequestOutput"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true
        requestService.create(requesterId, _ as BaseRequestInput) >> request

        when:
        def output = absenceRequestControllerV2.createSpecialAbsence(specialAbsence)

        then:
        output.getType() == RequestType.SPECIAL
        output.getStartDate() == specialAbsence.startDate()
        output.getEndDate() == specialAbsence.endDate()
        output.getTypeInfo() == specialAbsence.reason().toString()
    }

    def "createSpecialAbsence() WHEN called by admin SHOULD NOT throw UnauthorizedException"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true
        requestService.create(requesterId, _ as BaseRequestInput) >> request

        when:
        absenceRequestControllerV2.createSpecialAbsence(specialAbsence)

        then:
        notThrown(UnauthorizedException)
    }
}

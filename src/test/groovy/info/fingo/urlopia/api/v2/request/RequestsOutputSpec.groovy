package info.fingo.urlopia.api.v2.request

import info.fingo.urlopia.acceptance.Acceptance
import info.fingo.urlopia.acceptance.AcceptanceExcerptProjection
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestExcerptProjection
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class RequestsOutputSpec extends Specification {
    def id = 1L
    def startDate = LocalDate.now()
    def endDate = LocalDate.now()
    def status = Request.Status.ACCEPTED
    def requesterName = "requester"
    def leaderName = "leader"
    def type = RequestType.NORMAL
    def acceptanceStatus = Acceptance.Status.ACCEPTED

    def expectedAcceptances = new AcceptanceInfoOutput()

    void setup() {
        expectedAcceptances.setStatus(acceptanceStatus)
        expectedAcceptances.setLeaderName(leaderName)
        expectedAcceptances.setRequesterName(requesterName)
    }

    def "fromRequestExcerptProjection() when projection is passed should return requestOutput with same values"() {
        given:
        def acceptance = Mock(AcceptanceExcerptProjection) {
            getRequesterName() >> requesterName
            getLeaderName() >> leaderName
            getStatus() >> acceptanceStatus
        }

        def acceptances = [acceptance]

        def requestExcerptProjection = Mock(RequestExcerptProjection) {
            getId() >> id
            getStartDate() >> startDate
            getEndDate() >> endDate
            getStatus() >> status
            getRequesterName() >> requesterName
            getType() >> type
            getAcceptances() >> acceptances
        }

        when:
        def output = RequestsOutput.fromRequestExcerptProjection(requestExcerptProjection)

        then:
        output.getId() == id
        output.getStartDate() == startDate
        output.getEndDate() == endDate
        output.getStatus() == status
        output.getRequesterName() == requesterName
        output.getType() == type
        output.getAcceptances() == [expectedAcceptances]
    }

    def "fromRequest() when projection is passed should return requestOutput with same values"() {
        given:
        def requester = Mock(User) {
            getFullName() >> requesterName
        }

        def request = Mock(Request) {
            getId() >> id
            getStartDate() >> startDate
            getEndDate() >> endDate
            getStatus() >> status
            getRequester() >> requester
            getType() >> type
        }

        def leader = Mock(User) {
            getFullName() >> leaderName
        }

        def acceptance = Mock(Acceptance) {
            getRequest() >> request
            getLeader() >> leader
            getStatus() >> acceptanceStatus
        }
        def acceptances = [acceptance]

        when:
        def output = RequestsOutput.fromRequest(request, acceptances)

        then:
        output.getId() == id
        output.getStartDate() == startDate
        output.getEndDate() == endDate
        output.getStatus() == status
        output.getRequesterName() == requesterName
        output.getType() == type
        output.getAcceptances() == [expectedAcceptances]
    }

}

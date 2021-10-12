package info.fingo.urlopia.acceptance

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification

import java.time.LocalDate

class AcceptanceServiceSpec extends Specification {
    def leaderId = 1L
    def notLeaderId = 2L
    def requestId = 1L

    def leader = Mock(User) {
        getId() >> leaderId
    }

    def notLeader = Mock(User) {
        getId() >> notLeaderId
    }

    def request = Mock(Request) {
        getId() >> requestId
    }

    def acceptance = new Acceptance(request, leader)

    def acceptanceRepository = Mock(AcceptanceRepository) {
        findById(acceptance.getId()) >> Optional.of(acceptance)
        save(_ as Acceptance) >> {Acceptance a -> a}
        findByRequestId(requestId) >> [acceptance]
    }
    def requestService = Mock(RequestService)
    def publisher = Mock(ApplicationEventPublisher)

    def acceptanceService = new AcceptanceService(acceptanceRepository, requestService, publisher)

    def "accept() when leader accepts his acceptance should change acceptance status to ACCEPTED"() {
        when: "leader accepts acceptance"
        acceptanceService.accept(acceptance.getId(), leader.getId())

        then: "acceptance is accepted"
        acceptance.getStatus() == Acceptance.Status.ACCEPTED
    }

    def "accept() when leader accepts another leader's acceptance should throw UnauthorizedException"() {
        when: "not proper leader tries to accept acceptance"
        acceptanceService.accept(acceptance.getId(), notLeader.getId())

        then: "exception is thrown"
        thrown(UnauthorizedException)
    }

    def "reject() when leader rejects his acceptance should change acceptance status to REJECTED"() {
        when: "leader rejects acceptance"
        acceptanceService.reject(acceptance.getId(), leader.getId())

        then: "acceptance is rejected"
        acceptance.getStatus() == Acceptance.Status.REJECTED
    }

    def "reject() when leader rejects another leader's acceptance should throw UnauthorizedException"() {
        when: "not proper leader tries to reject acceptance"
        acceptanceService.reject(acceptance.getId(), notLeader.getId())

        then: "exception is thrown"
        thrown(UnauthorizedException)
    }

    def "getAcceptancesByRequestId() when requestId is valid should return acceptances associated with request"() {
        given:
        def acceptance = Mock(Acceptance)
        def requestId = 1L
        def request = Mock(Request) {
            getAcceptances() >> [acceptance]
            getId() >> requestId
        }

        def acceptanceRepository = Mock(AcceptanceRepository) {
            findByRequestId(request.getId()) >> [acceptance]
        }

        def acceptanceService = new AcceptanceService(acceptanceRepository, requestService, publisher)

        when:
        def acceptances = acceptanceService.getAcceptancesByRequestId(request.getId())

        then:
        acceptances == [acceptance]
    }

    def "checkIsExistActiveAcceptanceByLeaderId WHEN called with user that has active acceptance SHOULD return true"(){
        given:
        User user = Mock(User)
        acceptanceRepository.checkIsExistActiveAcceptanceByLeaderId(user) >> true

        when:
        def result = acceptanceService.hasActiveAcceptances(user)

        then:
        result
    }

    def "checkIsExistActiveAcceptanceByLeaderId WHEN called with user that hasn't active acceptance SHOULD return true"(){
        given:
        User user = Mock(User)
        acceptanceRepository.checkIsExistActiveAcceptanceByLeaderId(user) >> false

        when:
        def result = acceptanceService.hasActiveAcceptances(user)

        then:
        !result
    }

    def "getHistory() SHOULD return acceptances mapped to acceptance history output"() {
        given:
        def leader1Id = 11L
        def leader1FullName = "Jan Kowalski"

        def leader2Id = 22L
        def leader2FullName = "Adam Nowak"

        def requesterId = 33L
        def requesterFullName = "John Doe"

        def requestStartDate = LocalDate.of(2021, 10, 7)
        def requestEndDate = LocalDate.of(2021, 10, 8)
        def workingDays = 2

        def filter = Filter.empty()
        def pageable = Pageable.unpaged()

        and:
        def requester = Mock(User) {
            getId() >> requesterId
            getFullName() >> requesterFullName
        }

        and:
        def leader1 = Mock(User) {
            getId() >> leader1Id
            getFullName() >> leader1FullName
        }
        def acceptance1 = new Acceptance(null, leader1)
        acceptance1.setStatus(Acceptance.Status.ACCEPTED)

        def leader2 = Mock(User) {
            getId() >> leader2Id
            getFullName() >> leader2FullName
        }
        def acceptance2 = new Acceptance(null, leader2)
        acceptance2.setStatus(Acceptance.Status.REJECTED)

        def request = Mock(Request) {
            getRequester() >> requester
            getAcceptances() >> [acceptance1, acceptance2]
            getStartDate() >> requestStartDate
            getEndDate() >> requestEndDate
            getWorkingDays() >> workingDays
            getStatus() >> Request.Status.REJECTED
        }
        acceptance1.setRequest(request)
        acceptance2.setRequest(request)

        and:
        acceptanceRepository.findAll(_ as Filter, _ as Pageable) >> new PageImpl<Acceptance>([acceptance1])

        when:
        def acceptanceHistoryPage = acceptanceService.getHistory(leader1Id, filter, pageable)

        then:
        acceptanceHistoryPage.size() == 1
        def acceptanceHistory = acceptanceHistoryPage.getContent().get(0)
        acceptanceHistory.getId() == null
        acceptanceHistory.getRequestId() == null
        acceptanceHistory.getRequesterName() == requesterFullName
        acceptanceHistory.getStartDate() == requestStartDate
        acceptanceHistory.getEndDate() == requestEndDate
        acceptanceHistory.getWorkingDays() == workingDays
        acceptanceHistory.getStatus() == Acceptance.Status.ACCEPTED
        acceptanceHistory.getRequestStatus() == Request.Status.REJECTED
        acceptanceHistory.getLeadersAcceptances() == [
                "Jan Kowalski": Acceptance.Status.ACCEPTED,
                "Adam Nowak": Acceptance.Status.REJECTED
        ]
    }
}

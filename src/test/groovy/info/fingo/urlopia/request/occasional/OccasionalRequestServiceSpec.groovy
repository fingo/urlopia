package info.fingo.urlopia.request.occasional

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestInput
import info.fingo.urlopia.request.RequestRepository
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import java.time.LocalDate

class OccasionalRequestServiceSpec extends Specification {
    def requestRepository = Mock(RequestRepository) {
        save(_ as Request) >> { Request req -> req }
    }
    def userRepository = Mock(UserRepository)
    def historyLogService = Mock(HistoryLogService)
    def workingDaysCalculator = Mock(WorkingDaysCalculator)
    def publisher = Mock(ApplicationEventPublisher)
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def occasionalRequestService = new OccasionalRequestService(requestRepository, userRepository,
            historyLogService, workingDaysCalculator, publisher, presenceConfirmationService)

    def requesterId = 1L
    def sampleRequestStartDate = LocalDate.of(2021, 8, 17)
    def sampleRequestEndDate = LocalDate.of(2021, 8, 18)

    def "create() WHEN occasional request is created SHOULD delete presence confirmations in its date range"() {
        given: "sample requester"
        def requester = Mock(User) {
            getId() >> requesterId
        }

        and: "a valid request input"
        def requestInput = new RequestInput()
        requestInput.setStartDate(sampleRequestStartDate)
        requestInput.setEndDate(sampleRequestEndDate)
        requestInput.setType(RequestType.OCCASIONAL)
        requestInput.setOccasionalType(OccasionalType.D2_WEDDING)

        and: "user repository that returns requester"
        userRepository.findById(requesterId) >> Optional.of(requester)

        and: "a working days calculator that returns number of working days"
        workingDaysCalculator.calculate(sampleRequestStartDate, sampleRequestEndDate) >> 2

        when: "request is created"
        occasionalRequestService.create(requesterId, requestInput)

        then: "presence confirmation service is called"
        1 * presenceConfirmationService.deletePresenceConfirmations(requesterId, sampleRequestStartDate, sampleRequestEndDate)
    }
}

package info.fingo.urlopia.request.absence

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import info.fingo.urlopia.config.authentication.WebTokenService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestInput
import info.fingo.urlopia.request.RequestOverlappingException
import info.fingo.urlopia.request.RequestRepository
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification
import java.time.LocalDate

class SpecialAbsenceRequestServiceSpec extends Specification{
    def requesterId = 1L
    def adminId = 2L

    def requester = Mock(User) {
        getId() >> requesterId
    }
    def startDate = LocalDate.now()
    def endDate = startDate.plusDays(1L)
    def reason = SpecialAbsenceReason.OTHER
    def typeInfo = SpecialAbsenceReason.OTHER.toString()
    def workingDays = 1

    def request = new Request(requester, startDate, endDate, workingDays, typeInfo)

    def specialAbsenceInput = new SpecialAbsenceRequestInput(startDate, endDate, reason)

    def invalidSpecialAbsenceInput = new SpecialAbsenceRequestInput(endDate, startDate, reason)

    def requestRepository = Mock(RequestRepository) {
        findById(request.getId()) >> Optional.of(request)
    }

    def workingDaysCalculator = Mock(WorkingDaysCalculator)
    def publisher = Mock(ApplicationEventPublisher)
    def historyLogService = Mock(HistoryLogService)
    def userService = Mock(UserService)
    def webTokenService = Mock(WebTokenService)

    def specialAbsenceRequestService = new SpecialAbsenceRequestService(
            userService,
            workingDaysCalculator,
            historyLogService,
            requestRepository,
            publisher,
            webTokenService)

    def "create() WHEN called by non admin SHOULD throw UnauthorizedException"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> false

        when:
        specialAbsenceRequestService.create(requesterId, specialAbsenceInput)

        then:
        thrown(UnauthorizedException)
    }

    def "create() WHEN called by admin SHOULD not throw UnauthorizedException"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true
        webTokenService.getUserId() >> adminId
        userService.get(requesterId) >> requester
        requestRepository.findByRequesterId(requesterId) >> List.of()

        when:
        specialAbsenceRequestService.create(requesterId, specialAbsenceInput)

        then:
        notThrown(UnauthorizedException)
    }

    def  "create() WHEN request has reversed dates SHOULD throw InvalidDatesOrderException"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true
        userService.get(requesterId) >> requester
        requestRepository.findByRequesterId(requesterId) >> List.of()

        when:
        specialAbsenceRequestService.create(requesterId, invalidSpecialAbsenceInput)

        then:
        thrown(InvalidDatesOrderException)

    }

    def "create() WHEN requests are overlapping SHOULD throw RequestOverlappingException"() {
        given:
        userService.get(requesterId) >> requester
        webTokenService.isCurrentUserAnAdmin() >> true
        requestRepository.findByRequesterId(requesterId) >> List.of(request)

        when:
        specialAbsenceRequestService.create(requesterId, specialAbsenceInput)

        then:
        thrown(RequestOverlappingException)
    }

    def "create() WHEN request is valid and user is admin SHOULD save request"() {
        given:
        userService.get(requesterId) >> requester
        webTokenService.isCurrentUserAnAdmin() >> true
        requestRepository.findByRequesterId(requesterId) >> List.of()

        when:
        def request = specialAbsenceRequestService.create(requesterId, specialAbsenceInput)

        then:
        requestRepository.findById(request.getId()) >> request
    }

    def "cancel() WHEN called by admin SHOULD change request status to REJECTED"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true

        when:
        specialAbsenceRequestService.cancel(request)

        then:
        request.getStatus() == Request.Status.CANCELED
    }

    def "cancel() WHEN called by non admin SHOULD throw UnauthorizedException"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> false

        when:
        specialAbsenceRequestService.cancel(request)

        then:
        thrown(UnauthorizedException)
    }

    def "cancel() WHEN called by admin SHOULD save given request"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true

        when:
        specialAbsenceRequestService.cancel(request)

        then:
        1 * requestRepository.save(request)
    }

    def "accept() SHOULD throw OperationNotSupportedException"() {
        when:
        specialAbsenceRequestService.accept(request)

        then:
        thrown(OperationNotSupportedException)
    }

    def "reject() SHOULD throw OperationNotSupportedException"() {
        when:
        specialAbsenceRequestService.reject(request)

        then:
        thrown(OperationNotSupportedException)
    }

    def "mapToRequest() WHEN input is SpecialAbsenceInput SHOULD return Request with typeInfo equals to #reason"() {
       when:
       def newRequest = specialAbsenceRequestService
               .mapToRequest(requester, specialAbsenceInput, workingDays)

       then:
       newRequest.getSpecialTypeInfo() == reason.toString()
       newRequest.getStartDate() == specialAbsenceInput.getStartDate()
       newRequest.getEndDate() == specialAbsenceInput.getEndDate()
       newRequest.getRequester() == requester
       newRequest.getWorkingDays() == workingDays
    }

    def "mapToRequest() WHEN input is not a SpecialAbsenceInput SHOULD return Request with typeInfo WRONG"() {
        given:
        def input = new RequestInput()
        input.setStartDate(startDate)
        input.setEndDate(endDate)

        when:
        def newRequest = specialAbsenceRequestService
                .mapToRequest(requester, input, workingDays)

        then:
        newRequest.getSpecialTypeInfo() == SpecialAbsenceReason.WRONG.toString()
        newRequest.getStartDate() == input.getStartDate()
        newRequest.getEndDate() == input.getEndDate()
        newRequest.getRequester() == requester
        newRequest.getWorkingDays() == workingDays
    }



}

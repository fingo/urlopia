package info.fingo.urlopia.request

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import info.fingo.urlopia.config.authentication.WebTokenService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.request.normal.NormalRequestService
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties
import spock.lang.Specification

import java.time.LocalDate


class RequestServiceSpec extends Specification {
    def requesterId = 1L
    def notRequesterId = 2L
    def requester = Mock(User) {
        getId() >> requesterId
    }
    def notRequester = Mock(User) {
        getId() >> notRequesterId
    }
    def startDate = LocalDate.now()
    def endDate = LocalDate.now()
    def type = RequestType.NORMAL
    def workingDays = 1
    def typeInfo = null
    def status = Request.Status.PENDING

    def request = new Request(requester, startDate, endDate, workingDays, type, typeInfo, status)

    def requestRepository = Mock(RequestRepository) {
        findById(request.getId()) >> Optional.of(request)
    }
    def historyLogService = Mock(HistoryLogService)
    def userService = Mock(UserService)
    def webTokenService = Mock(WebTokenService)
    def requestService = new RequestService(requestRepository,
            historyLogService,
            userService,
            webTokenService)

    def webTokenServiceThrowingException = Mock(WebTokenService) {
        ensureAdmin() >> {
            throw UnauthorizedException.unauthorized()
        }
    }

    def requestServiceWithModifiedWebTokenService = new RequestService(requestRepository,
            historyLogService,
            userService,
            webTokenServiceThrowingException)

    def "validateAdminPermissionAndAccept() when admin is accepting request should change request status to ACCEPTED"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            accept(request) >> request.setStatus(Request.Status.ACCEPTED)
        }
        type.setService(normalRequestService)

        when: "Admin user is accepting request"
        requestService.validateAdminPermissionAndAccept(request.getId(), notRequester.getId())

        then: "Request status changed to ACCEPTED"
        request.getStatus() == Request.Status.ACCEPTED
    }

    def "validateAdminPermissionAndAccept() when not admin is accepting request should throw UnauthorizedException"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            accept(request) >> request.setStatus(Request.Status.ACCEPTED)
        }
        type.setService(normalRequestService)

        when: "Not admin user is accepting request"
        requestServiceWithModifiedWebTokenService.validateAdminPermissionAndAccept(request.getId(), requester.getId())

        then: "Exception is thrown"
        thrown(UnauthorizedException)
    }

    def "validateAdminPermissionAndReject() when admin is rejecting request should change request status to REJECTED"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            reject(request) >> request.setStatus(Request.Status.REJECTED)
        }
        type.setService(normalRequestService)

        when: "Admin user is rejecting request"
        requestService.validateAdminPermissionAndReject(request.getId())

        then: "Request status changed to REJECTED"
        request.getStatus() == Request.Status.REJECTED
    }

    def "validateAdminPermissionAndReject() when not admin is rejecting request should throw UnauthorizedException"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            reject(request) >> request.setStatus(Request.Status.REJECTED)
        }
        type.setService(normalRequestService)

        when: "Not admin user is rejecting request"
        requestServiceWithModifiedWebTokenService.validateAdminPermissionAndReject(request.getId())

        then: "Exception is thrown"
        thrown(UnauthorizedException)
    }

    def "cancel() when admin is canceling request should change request status to CANCELED"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            cancel(request) >> request.setStatus(Request.Status.CANCELED)
        }
        type.setService(normalRequestService)

        when: "Admin user is canceling request"
        requestService.cancel(request.getId(), notRequester.getId())

        then: "Request status changed to CANCELED"
        request.getStatus() == Request.Status.CANCELED
    }

    def "cancel() when requester is canceling request should change request status to CANCELED"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            cancel(request) >> request.setStatus(Request.Status.CANCELED)
        }
        type.setService(normalRequestService)

        when: "Requester is canceling request"
        requestService.cancel(request.getId(), requester.getId())

        then: "Request status changed to CANCELED"
        request.getStatus() == Request.Status.CANCELED
    }

    def "cancel() when not admin nor requester is canceling request should throw UnauthorizedException"() {
        given:
        def normalRequestService = Mock(NormalRequestService) {
            cancel(request) >> request.setStatus(Request.Status.CANCELED)
        }
        type.setService(normalRequestService)

        when: "Not admin user nor requester is canceling request"
        requestServiceWithModifiedWebTokenService.cancel(request.getId(), notRequester.getId())

        then: "Exception is thrown"
        thrown(UnauthorizedException)
    }
}

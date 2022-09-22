package info.fingo.urlopia.api.v2.request

import info.fingo.urlopia.acceptance.Acceptance
import info.fingo.urlopia.acceptance.AcceptanceService
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestExcerptProjection
import info.fingo.urlopia.request.RequestInput
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.request.absence.BaseRequestInput
import info.fingo.urlopia.request.absence.SpecialAbsence
import info.fingo.urlopia.request.absence.SpecialAbsenceReason
import info.fingo.urlopia.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
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

    def absenceRequestControllerV2 = new AbsenceRequestControllerV2(acceptanceService, requestService)

    def "createSpecialAbsence() WHEN called by admin SHOULD return instance of RequestOutput"() {
        given:
        requestService.create(requesterId, _ as BaseRequestInput) >> request

        when:
        def output = absenceRequestControllerV2.createSpecialAbsence(specialAbsence)

        then:
        output.getType() == RequestType.SPECIAL
        output.getStartDate() == specialAbsence.startDate()
        output.getEndDate() == specialAbsence.endDate()
        output.getTypeInfo() == specialAbsence.reason().toString()
    }


    def "create() WHEN called SHOULD call service and return data "(){
        given: "http mock"
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> requesterId
        }
        and: "requestInput mock"
        def reqInput = Mock(RequestInput)

        and: "request mock with service"
        def user = Mock(User){
            getFullName() >> "John Smith"
        }
        def request = Mock(Request){
            getId() >> 3
            getRequester() >> user
        }
        requestService.create(_ as Long, reqInput) >> request
        requestService.getById(_ as Long) >> request

        and: "acceptance mock with service"
        def acceptance = Mock(Acceptance){
            getRequester() >> user
            getRequest() >> request
            getLeader() >> user
        }
        acceptanceService.getAcceptancesByRequestId(_ as Long) >> [acceptance]

        when:
        def result = absenceRequestControllerV2.create(reqInput, httpRequest)

        then:
        1 * requestService.create(requesterId, _ as BaseRequestInput) >> request
        result != null
    }

    def "getMyRequest() WHEN called SHOULD call service and return data "(){
        given: "http mock"
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> requesterId
        }
        String[] filterArray = [] as String[]

        and: "page Mock"
        def pageRequest = Mock(Page<RequestExcerptProjection>)

        when:
        absenceRequestControllerV2.getMyRequests(filterArray, _ as Pageable, httpRequest)

        then:
        1 * requestService.getFromUser(_ as Long, _ as Filter, _  as Pageable)  >> pageRequest

    }

    def "getAllRequests() WHEN called SHOULD call service and return data"(){
        given: "empty filterArray"
        String[] filterArray = [] as String[]

        and: "page mock"
        def pageRequest = Mock(Page<RequestExcerptProjection>)

        when:
        absenceRequestControllerV2.getAllRequests(filterArray, _ as Pageable)

        then:
        1 * requestService.get(_ as Filter, _  as Pageable)  >> pageRequest
    }

    def "updateAbsenceRequestStatus WHEN called with CANCELED status SHOULD call cancel method from service"(){
        given: "requestStatus == cancled"
        def status = Request.Status.CANCELED
        def requestStatus = new RequestStatus(status)

        and: "http request mock"
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> requesterId
        }
        and: "requestService mock"
        def request = Mock(Request){
            getStatus() >> status
        }
        requestService.getById(_ as Long) >> request

        when:
        def result = absenceRequestControllerV2.updateAbsenceRequestStatus(requesterId, requestStatus, httpRequest)

        then:
        1 * requestService.cancel(_ as Long, _ as Long)
        result.status() == Request.Status.CANCELED


    }

    def "updateAbsenceRequestStatus WHEN called with ACCEPTED status SHOULD call accept method from service"(){
        given: "requestStatus == accepted"
        def status = Request.Status.ACCEPTED
        def requestStatus = new RequestStatus(status)

        and: "http request mock"
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> requesterId
        }
        and: "requestService mock"
        def request = Mock(Request){
            getStatus() >> status
        }
        requestService.getById(_ as Long) >> request

        when:
        def result = absenceRequestControllerV2.updateAbsenceRequestStatus(requesterId, requestStatus, httpRequest)

        then:
        1 * requestService.validateAdminPermissionAndAccept(_ as Long, _ as Long)
        result.status() == Request.Status.ACCEPTED

    }

    def "updateAbsenceRequestStatus WHEN called with REJECTED status SHOULD call reject method from service"(){
        given: "requestStatus == rejected"
        def status = Request.Status.REJECTED
        def requestStatus = new RequestStatus(status)

        and: "http request mock"
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> requesterId
        }
        and: "requestService mock"
        def request = Mock(Request){
            getStatus() >> status
        }
        requestService.getById(_ as Long) >> request

        when:
        def result = absenceRequestControllerV2.updateAbsenceRequestStatus(requesterId, requestStatus, httpRequest)

        then:
        1 * requestService.validateAdminPermissionAndReject(_ as Long)
        result.status() == Request.Status.REJECTED
    }

}

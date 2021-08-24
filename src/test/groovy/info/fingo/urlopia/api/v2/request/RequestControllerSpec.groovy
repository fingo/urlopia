package info.fingo.urlopia.api.v2.request

import info.fingo.urlopia.request.RequestController
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.normal.NormalRequestService
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class RequestControllerSpec extends Specification{
    private RequestService requestService
    private NormalRequestService normalRequestService
    private RequestController requestController

    private long deciderId = 5L
    private int requestId = 10

    void setup(){
        requestService = Mock(RequestService)
        normalRequestService = Mock(NormalRequestService)
        requestController = new RequestController(requestService,normalRequestService)
    }

    def "accept() WHEN called SHOULD call service layer"() {
        given:
        HttpServletRequest httpServletRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> deciderId
        }

        when:
        requestController.accept(requestId,httpServletRequest)

        then:
        1 *  requestService.validateAdminPermissionAndAccept(requestId,deciderId)

    }

    def "reject() WHEN called SHOULD call service layer"(){
        when:
        requestController.reject(requestId)

        then:
        1 *  requestService.validateAdminPermissionAndReject(requestId)
    }
}

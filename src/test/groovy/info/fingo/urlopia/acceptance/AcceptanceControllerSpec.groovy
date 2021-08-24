package info.fingo.urlopia.acceptance

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class AcceptanceControllerSpec extends Specification{
    private AcceptanceService acceptanceService
    private AcceptanceController acceptanceController
    private long deciderId = 5L
    private int acceptanceId

    void setup(){
        acceptanceService = Mock(AcceptanceService)
        acceptanceController = new AcceptanceController(acceptanceService)
    }

    def "accept() WHEN called SHOULD call service layer"(){
        given:
        HttpServletRequest httpServletRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> deciderId
        }

        when:
        acceptanceController.accept(acceptanceId,httpServletRequest)

        then:
        1 * acceptanceService.accept(acceptanceId,deciderId)
    }

    def "reject() WHEN called SHOULD call service layer"(){
        given:
        HttpServletRequest httpServletRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> deciderId
        }

        when:
        acceptanceController.reject(acceptanceId,httpServletRequest)

        then:
        1 * acceptanceService.reject(acceptanceId,deciderId)
    }
}

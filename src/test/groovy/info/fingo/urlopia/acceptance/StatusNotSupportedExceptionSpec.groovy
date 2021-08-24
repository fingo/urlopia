package info.fingo.urlopia.acceptance

import spock.lang.Specification

class StatusNotSupportedExceptionSpec extends Specification{
    private static final String EXPECTED_MESSAGE = "Status not supported: "


    def "statusNotSupported WHEN called SHOULD return StatusNotSupportedException with expected message"(){
        given:
        def status = "valid status"

        when:
        def result = StatusNotSupportedException.invalidStatus(status)

        then:
        result.getMessage() == EXPECTED_MESSAGE + status
    }
}

package info.fingo.urlopia.api.v2.user

import info.fingo.urlopia.history.WorkTimeResponse
import spock.lang.Specification

class WorkTimeOutputSpec extends Specification{
    private WorkTimeResponse workTimeResponse;

    void setup(){
        workTimeResponse = Mock(WorkTimeResponse)
    }

    def "fromWorkTimeResponse() WHEN called with WorkTimeResponse SHOULD map it correctly to WorkTimeOutput object"(){
        given:
        workTimeResponse.getWorkTimeA() >> workTimeA
        workTimeResponse.getWorkTimeB() >> workTimeB

        and: "valid expected value"
        def correctWorkTimeOutput = new WorkTimeOutput(workTimeA,workTimeB)

        when:
        def result = WorkTimeOutput.fromWorkTimeResponse(workTimeResponse)

        then:
        result == correctWorkTimeOutput

        where:
        workTimeA | workTimeB
        0         | 0
        7         | 9
        1         | 3
        4         | 7
    }
}

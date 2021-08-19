package info.fingo.urlopia.api.v2.user

import info.fingo.urlopia.history.WorkTimeResponse
import spock.lang.Specification

class VacationDaysOutputSpec extends Specification{
    private WorkTimeResponse workTimeResponse;

    void setup(){
        workTimeResponse = Mock(WorkTimeResponse)
    }

    def "fromWorkTimeResponse() WHEN called with WorkTimeResponse SHOULD map it correctly to VacationDaysOutput object"(){
        given:
        workTimeResponse.getDays() >> days
        workTimeResponse.getHours() >> hours

        and: "valid expected value"
        def correctVacationDaysOutput = new VacationDaysOutput(days,hours)

        when:
        def result = VacationDaysOutput.fromWorkTimeResponse(workTimeResponse)

        then:
        result == correctVacationDaysOutput

        where:
        days | hours
        0         | 0.0
        1         | 9.0
        2         | 3.0
        3         | 7.0
    }
}


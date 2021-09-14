package info.fingo.urlopia.api.v2.user

import info.fingo.urlopia.history.WorkTimeResponse
import spock.lang.Specification

class VacationDaysOutputSpec extends Specification{
    private WorkTimeResponse workTimeResponse

    void setup(){
        workTimeResponse = Mock(WorkTimeResponse)
    }

    def "fromWorkTimeResponse() WHEN called with WorkTimeResponse SHOULD map it correctly to VacationDaysOutput object"(){
        given:
        workTimeResponse.getDays() >> days
        workTimeResponse.getHours() >> hours
        workTimeResponse.getWorkTime() >> workTime
        workTimeResponse.getPool() >> pool

        and: "valid expected value"
        def correctVacationDaysOutput = new VacationDaysOutput(days, hours, workTime)

        when:
        def result = VacationDaysOutput.fromWorkTimeResponse(workTimeResponse)

        then:
        result == correctVacationDaysOutput

        where:
        days | hours | workTime | pool
        0    | 0.0   | 8        | 0
        1    | 9.0   | 8        | 0
        2    | 3.0   | 8        | 0
        3    | 7.0   | 8        | 0
        0    | 20.0  | 1        | 20.0
        0    | 30.0  | 4        | 30.0
    }
}


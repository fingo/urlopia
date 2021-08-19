package info.fingo.urlopia.holidays

import info.fingo.urlopia.api.v2.holiday.HolidayOutput
import spock.lang.Specification

import java.time.LocalDate

class HolidayOutputSpec extends Specification{
    private Holiday holiday
    void setup(){
        holiday = Mock(Holiday)
    }

    def "fromHoliday() WHEN called with Holiday SHOULD map it correctly to HolidayOutput object"(){
        given:
        holiday.getId() >> id
        holiday.getName() >> name
        holiday.getDate() >> date

        and: "valid expected value"
        def correctHolidayOutput = new HolidayOutput(id,name,date)

        when:
        def result = HolidayOutput.fromHoliday(holiday)

        then:
        result == correctHolidayOutput

        where:
        id | name       | date
        1  | "holiday1" | LocalDate.of(2021,1,1)
        2  | "holiday2" | LocalDate.of(2021,1,1)
        3  | "holiday3" | LocalDate.of(2021,1,1)
        4  | "holiday4" | LocalDate.of(2021,1,1)
    }
}

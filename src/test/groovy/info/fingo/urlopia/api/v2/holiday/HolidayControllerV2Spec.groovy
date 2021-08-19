package info.fingo.urlopia.api.v2.holiday

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.Holiday
import info.fingo.urlopia.holidays.HolidayService
import spock.lang.Specification

import java.time.LocalDate

class HolidayControllerV2Spec extends Specification{
    private HolidayService holidayService;
    private HolidayControllerV2 holidayControllerV2
    private List<Holiday> holidaysData
    private List<HolidayOutput> holidayOutputData

    void setup(){
        holidayService = Mock(HolidayService)
        holidayControllerV2 = new HolidayControllerV2(holidayService)
        def firstId = 1
        def firstDate = LocalDate.of(2021,1,2)
        def firstName = "holiday"
        Holiday holiday = Mock(Holiday){
            getId() >> firstId
            getDate() >> firstDate
            getName() >> firstName

        }
        def secondId = 2
        def secondDate = LocalDate.of(2021,2,2)
        def secondName = "holiday2"
        Holiday holiday2 = Mock(Holiday){
            getId() >> secondId
            getDate() >> secondDate
            getName() >> secondName

        }
        holidaysData = List.of(holiday,holiday2)
        holidayOutputData = List.of(new HolidayOutput(firstId,firstName,firstDate),
                                    new HolidayOutput(secondId,secondName,secondDate))

    }

    def "getAll() WHEN called with empty Filter SHOULD called service one time and map returned data"(){
        given:
        1 * holidayService.getAll(_ as Filter) >> holidaysData


        when:
        def result = holidayControllerV2.getAll(_ as String)

        then:
        result.containsAll(holidayOutputData)

    }

    def "generateDefault() WHEN called SHOULD called service one time and map returned data"(){
        given:
        def exampleYear = 2021
        1 * holidayService.generateHolidaysList(exampleYear) >> holidaysData

        when:
        def result = holidayControllerV2.generateDefault(exampleYear)

        then:
        result.containsAll(holidayOutputData)

    }

    def "save() WHEN called with holiday outside range SHOULD throw HolidayOutsideSpecifiedRange exception"(){
        given:

        HolidayInput holidayInput = new HolidayInput(startDate,endDate,holidaysData)

        when:
        holidayControllerV2.save(holidayInput)

        then:
        def holidayException = thrown(HolidayOutsideSpecifiedRange)
        holidayException.message == "Holidays are not in specified time period"

        where:

        startDate              | endDate
        LocalDate.of(2021,1,3) | LocalDate.of(2021,2,5)
        LocalDate.of(2021,1,1) | LocalDate.of(2021,2,1)
        LocalDate.of(2021,1,3) | LocalDate.of(2021,2,1)

    }

    def "save() WHEN called without holiday outside range SHOULD call service twice and return mapped Data"(){
        given:

        HolidayInput holidayInput = new HolidayInput(startDate,endDate,holidaysData)
        1 * holidayService.deleteBetweenDates(startDate,endDate)
        1 * holidayService.saveHolidays(holidaysData) >> holidaysData

        when:
        def result = holidayControllerV2.save(holidayInput)

        then:
        notThrown(HolidayOutsideSpecifiedRange)
        result.containsAll(holidayOutputData)

        where:

        startDate              | endDate
        LocalDate.of(2021,1,2) | LocalDate.of(2022,1,1)
        LocalDate.of(2021,1,1) | LocalDate.of(2021,2,2)
        LocalDate.of(2021,1,2) | LocalDate.of(2021,2,2)

    }

}

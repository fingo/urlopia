package info.fingo.urlopia.holidays

import info.fingo.urlopia.config.persistance.filter.Filter
import spock.lang.Specification

import java.time.LocalDate

class HolidayServiceSpec extends Specification{

    private HolidayRepository holidayRepository;
    private List<Holiday> holidaysData;
    private HolidayService holidayService;

     void setup(){
         holidayRepository = Mock(HolidayRepository)
         holidayService = new HolidayService(holidayRepository)
         holidaysData = List.of(new Holiday("holiday", LocalDate.of(2021,1,1)),
                                new Holiday("holiday2",LocalDate.of(2021,6,6)),
                                new Holiday("holiday3",LocalDate.of(2021,12,31)))
    }

    def "getAll() WHEN called with empty Filter SHOULD called repository one time and return all data "(){
        given:
        def filter = Filter.from("")
        1 * holidayRepository.findAll(filter) >> holidaysData


        when:
        def result = holidayService.getAll(filter);

        then:
        result == holidaysData
    }

    def "saveHolidays() WHEN called with holiday list SHOULD called repository one time and return created objects"(){
        given:
        1 * holidayRepository.saveAll(holidaysData) >> holidaysData
        when:
        def resultList = holidayService.saveHolidays(holidaysData);

        then:
        resultList == holidaysData
}

    def "deleteBetweenDates() WHEN called with start and end date SHOULD called repository two times and return nothing "(){
        given:
        def startDate = LocalDate.of(2021,1,1)
        def endDate = LocalDate.of(2021,12,31)
        1 * holidayRepository.findByDateBetween(startDate,endDate) >> holidaysData

        when:
        holidayService.deleteBetweenDates(startDate,endDate);

        then:
        1 * holidayRepository.deleteAll(holidaysData)
    }

}

package info.fingo.urlopia.api.v2.history

import info.fingo.urlopia.history.HistoryLog
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.LocalDate

class UsedHoursFromMonthCalculatorSpec extends Specification{


    def int countDaysBetween(dates){
        def startDate = dates[0] as LocalDate
        def endDate = dates[1] as LocalDate
        return Duration.between(startDate.atStartOfDay(),endDate.atStartOfDay()).toDays()+1
    }

    def workingDaysCalculator = Mock(WorkingDaysCalculator){
        calculate(_ as LocalDate, _ as LocalDate) >> {
            arguments -> return countDaysBetween(arguments)
        }
    }
    def usedHoursFromMonthCalculator = new UsedHoursFromMonthCalculator(workingDaysCalculator)

    @Unroll
    def "countUsedHours WHEN request is not take place in a given month SHOULD return 0 "(){
        given:
        def checkedMonth = 2
        def checkedYear = 2021
        def request = Mock(Request){
            getStartDate() >> startDate
            getEndDate() >> endDate
        }
        def historyLog = Mock(HistoryLog){
            getRequest() >> request
        }

        when:
        def result = usedHoursFromMonthCalculator.countUsedHours(checkedYear, checkedMonth, historyLog)

        then:
        result == 0

        where:
        startDate                | endDate
        LocalDate.of(2021, 1, 1) | LocalDate.of(2021, 1, 5)
        LocalDate.of(2021, 3, 1) | LocalDate.of(2021, 3, 5)
        LocalDate.of(2020, 2, 1) | LocalDate.of(2020, 2, 5)
        LocalDate.of(2022, 2, 1) | LocalDate.of(2022, 2, 5)
    }

    @Unroll
    def "countUsedHoursFromNormalRequest WHEN called with request that's start and end in the same month SHOULD return logHours"(){
        given:
        def checkedMonth = 1
        def checkedYear = 2021
        def request = Mock(Request){
            getStartDate() >> startDate
            getEndDate() >> endDate
        }
        def historyLog = Mock(HistoryLog){
            getHours() >> -8 * countDaysBetween([startDate, endDate])
            getRequest() >> request
        }

        when:
        def result = usedHoursFromMonthCalculator.countUsedHours(checkedYear, checkedMonth, historyLog)

        then:
        result == 8 * daysBetween

        where:
        startDate                 | endDate                   | daysBetween
        LocalDate.of(2021, 1, 1)  | LocalDate.of(2021, 1, 31) | 31
        LocalDate.of(2021, 1, 1)  | LocalDate.of(2021, 1, 1)  | 1
        LocalDate.of(2021, 1, 5)  | LocalDate.of(2021, 1, 15) | 11
        LocalDate.of(2021, 1, 1)  | LocalDate.of(2021, 1, 5)  | 5
        LocalDate.of(2021, 1, 1)  | LocalDate.of(2021, 1, 1)  | 1
    }


    @Unroll
    def "countUsedHoursFromNormalRequest WHEN called with request that's overlap other months SHOULD return logHours only for given month"(){
        given:
        def checkedMonth = 5
        def checkedYear = 2021
        def request = Mock(Request){
            getStartDate() >> startDate
            getEndDate() >> endDate
        }
        def historyLog = Mock(HistoryLog){
            getUserWorkTime() >> 8
            getRequest() >> request
        }

        when:
        def result = usedHoursFromMonthCalculator.countUsedHours(checkedYear, checkedMonth, historyLog)

        then:
        result == 8 * daysBetween

        where:
        startDate                 | endDate                   | daysBetween
        LocalDate.of(2021, 4, 30) | LocalDate.of(2021, 5, 2)  | 2
        LocalDate.of(2021, 4, 1)  | LocalDate.of(2021, 5, 31) | 31
        LocalDate.of(2021, 5, 31) | LocalDate.of(2021, 6, 1)  | 1
        LocalDate.of(2021, 5, 1)  | LocalDate.of(2021, 6, 1)  | 31
        LocalDate.of(2021, 5, 7)  | LocalDate.of(2021, 6, 30) | 25
        LocalDate.of(2021, 4, 30) | LocalDate.of(2021, 6, 1)  | 31
        LocalDate.of(2021, 4, 1)  | LocalDate.of(2021, 6, 1)  | 31
    }

}

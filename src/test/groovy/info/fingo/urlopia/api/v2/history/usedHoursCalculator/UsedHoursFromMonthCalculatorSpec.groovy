package info.fingo.urlopia.api.v2.history.usedHoursCalculator

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

    def requestMonthsOverlappingChecker = Mock(RequestMonthsOverlappingChecker)

    def usedHoursFromMonthCalculator = new UsedHoursFromMonthCalculator(workingDaysCalculator, requestMonthsOverlappingChecker)

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
    def "countUsedHoursFromNormalRequest WHEN called with request is not overlapping SHOULD return logHours"(){
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

        def requestMonthsOverlappingChecker = Mock(RequestMonthsOverlappingChecker) {
            requestNotOverlapOtherMonth(_ as Integer, _ as Integer, _ as Request) >> true
        }

        def usedHoursFromMonthCalculator = new UsedHoursFromMonthCalculator(workingDaysCalculator, requestMonthsOverlappingChecker)

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
    def "countUsedHoursFromNormalRequest WHEN called with request that's overlap prev months SHOULD return logHours only for given month"(){
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

        def requestMonthsOverlappingChecker = Mock(RequestMonthsOverlappingChecker){
            requestOverlapOnlyPrevMonths(_ as Integer, _ as Integer, _ as Request) >> true
        }
        def usedHoursFromMonthCalculator = new UsedHoursFromMonthCalculator(workingDaysCalculator, requestMonthsOverlappingChecker)

        when:
        def result = usedHoursFromMonthCalculator.countUsedHours(checkedYear, checkedMonth, historyLog)

        then:
        result == 8 * daysBetween

        where:
        startDate                 | endDate                   | daysBetween
        LocalDate.of(2021, 4, 30) | LocalDate.of(2021, 5, 2)  | 2
        LocalDate.of(2021, 4, 1)  | LocalDate.of(2021, 5, 31) | 31
    }

    @Unroll
    def "countUsedHoursFromNormalRequest WHEN called with request that's overlap next months SHOULD return logHours only for given month"(){
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

        def requestMonthsOverlappingChecker = Mock(RequestMonthsOverlappingChecker){
            requestOverlapOnlyNextMonths(_ as Integer, _ as Integer, _ as Request) >> true
        }
        def usedHoursFromMonthCalculator = new UsedHoursFromMonthCalculator(workingDaysCalculator, requestMonthsOverlappingChecker)

        when:
        def result = usedHoursFromMonthCalculator.countUsedHours(checkedYear, checkedMonth, historyLog)

        then:
        result == 8 * daysBetween

        where:
        startDate                 | endDate                   | daysBetween
        LocalDate.of(2021, 5, 31) | LocalDate.of(2021, 6, 1)  | 1
        LocalDate.of(2021, 5, 1)  | LocalDate.of(2021, 6, 1)  | 31
        LocalDate.of(2021, 5, 7)  | LocalDate.of(2021, 6, 30) | 25
    }

    @Unroll
    def "countUsedHoursFromNormalRequest WHEN called with request that's overlap prev and next months SHOULD return logHours only for given month"(){
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

        def requestMonthsOverlappingChecker = Mock(RequestMonthsOverlappingChecker){
            requestOverlapNextAndPrevMonth(_ as Integer, _ as Integer, _ as Request) >> true
        }
        def usedHoursFromMonthCalculator = new UsedHoursFromMonthCalculator(workingDaysCalculator, requestMonthsOverlappingChecker)

        when:
        def result = usedHoursFromMonthCalculator.countUsedHours(checkedYear, checkedMonth, historyLog)

        then:
        result == 8 * daysBetween

        where:
        startDate                 | endDate                   | daysBetween
        LocalDate.of(2021, 4, 30) | LocalDate.of(2021, 6, 1)  | 31
        LocalDate.of(2021, 4, 1)  | LocalDate.of(2021, 6, 1)  | 31
    }

}

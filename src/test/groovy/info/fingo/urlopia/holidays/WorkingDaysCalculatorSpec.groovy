package info.fingo.urlopia.holidays

import spock.lang.Specification

import java.time.LocalDate

class WorkingDaysCalculatorSpec extends Specification{

    def holidayService = Mock(HolidayService){
        isWorkingDay(_ as LocalDate) >> true
    }
    def workingDaysCalculator = new WorkingDaysCalculator(holidayService)

    def "calculate WHEN endDate is before startDate SHOULD return 0"(){
        given:
        def endDate = LocalDate.of(2022,1,1)
        def startDate = LocalDate.of(2022,1,2)

        when:
        def result = workingDaysCalculator.calculate(startDate, endDate)

        then:
        result == 0
    }

    def "calculate WHEN endDate is on the same day as startDate and it is working day SHOULD return 1"(){
        given:
        def endDate = LocalDate.of(2022,1,1)
        def startDate = LocalDate.of(2022,1,1)

        when:
        def result = workingDaysCalculator.calculate(startDate, endDate)

        then:
        result == 1
    }

    def "calculate WHEN endDate is two days after startDate and every day is working day SHOULD return 3"(){
        given:
        def endDate = LocalDate.of(2022,1,3)
        def startDate = LocalDate.of(2022,1,1)

        when:
        def result = workingDaysCalculator.calculate(startDate, endDate)

        then:
        result == 3
    }

    def "calculate WHEN day is not working SHOULD not count him"(){
        given:
        def endDate = LocalDate.of(2022,1,2)
        def startDate = LocalDate.of(2022,1,1)
        def holidayService = Mock(HolidayService){
            isWorkingDay(_ as LocalDate) >> false
        }
        def workingDaysCalculator = new WorkingDaysCalculator(holidayService)

        when:
        def result = workingDaysCalculator.calculate(startDate, endDate)

        then:
        result == 0
    }
}

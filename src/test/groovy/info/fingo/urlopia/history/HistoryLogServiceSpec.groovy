package info.fingo.urlopia.history

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.user.User

import java.time.LocalDate
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.user.UserRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class HistoryLogServiceSpec extends Specification {
    def historyLogRepository = Mock(HistoryLogRepository)
    def userRepository = Mock(UserRepository)
    def workingDaysCalculator = Mock(WorkingDaysCalculator)
    def holidayService = Mock(HolidayService)
    def historyLogService = new HistoryLogService(historyLogRepository, userRepository, workingDaysCalculator,holidayService)
    def userID = 5;
    def user = Mock(User){
        getId() >> userID
    }
    def year = 2021



    def "checkIfWorkedFullTimeForTheWholeYear() WHEN called with year and user who was not working all time in fullTime SHOULD return false"() {
        given: "logs which show two different workTime "
        def firstHistoryLog = Mock(HistoryLog) {
            getUserWorkTime() >> 8.0f
        }

        def secondHistoryLog = Mock(HistoryLog) {
            getUserWorkTime() >> 7.0f
        }

        historyLogRepository.findLogsByUserIdAndCreatedBetween(_ as Long,
                _ as LocalDateTime, _ as LocalDateTime) >> [firstHistoryLog, secondHistoryLog]

        when:
        def result = historyLogService.checkIfWorkedFullTimeForTheWholeYear(5L, 2021)

        then:
        !result
    }

    def "checkIfWorkedFullTimeForTheWholeYear() WHEN called with year and user who working all time in fullTime SHOULD return true"() {
        given: "logs which show two different workTime "
        def firstHistoryLog = Mock(HistoryLog) {
            getUserWorkTime() >> 8.0f
        }

        def secondHistoryLog = Mock(HistoryLog) {
            getUserWorkTime() >> 8.0f
        }

        historyLogRepository.findLogsByUserIdAndCreatedBetween(_ as Long,
                _ as LocalDateTime, _ as LocalDateTime) >> [firstHistoryLog, secondHistoryLog]

        when:
        def result = historyLogService.checkIfWorkedFullTimeForTheWholeYear(5L, 2021)

        then:
        result
    }

    def "countTheHoursUsedDuringTheYear() WHEN called with request that start and end in year SHOULD count how many hours he used"() {
        given: "logs which show two different workTime "
        def firstHours = -10.0f
        def firstHistoryLog = Mock(HistoryLog) {
            getHours() >> firstHours
            getRequest() >> Mock(Request){
                getEndDate() >> LocalDate.of(year,1,1);
                getStartDate() >> LocalDate.of(year,1,1)
            }
            getUser() >> user
        }

        def secondHours = -8.0f
        def secondHistoryLog = Mock(HistoryLog) {
            getHours() >> secondHours
            getRequest() >> Mock(Request){
                getEndDate() >> LocalDate.of(year,1,1);
                getStartDate() >> LocalDate.of(year,1,1)
            }
            getUser() >> user
        }

        historyLogRepository.findAll() >> [firstHistoryLog, secondHistoryLog]
        holidayService.isWorkingDay(_ as LocalDate) >> true

        when:
        def result = historyLogService.countTheHoursUsedDuringTheYear(userID, 2021)

        then:
        result == Math.abs(firstHours + secondHours)

    }


    def "countTheHoursUsedDuringTheYear() WHEN called with request that start in past year and end in this SHOULD count how many hours he used from 01.01"() {
        given: "logs which start in 2020 and end in 2021"
        def hours = -10.0f
        def numberOfDays = 2
        def firstHistoryLog = Mock(HistoryLog) {
            getHours() >> hours
            getRequest() >> Mock(Request){
                getEndDate() >> LocalDate.of(year,1,1)
                getStartDate() >> LocalDate.of(year-1,12,31)
            }
            getUser() >> user
        }

        historyLogRepository.findAll() >> [firstHistoryLog]
        holidayService.isWorkingDay(_ as LocalDate) >> true

        when:
        def result = historyLogService.countTheHoursUsedDuringTheYear(userID, 2021)

        then:
        result == Math.abs(hours/numberOfDays)

    }


    def "countTheHoursUsedDuringTheYear() WHEN called with request that start in this year and end in next one SHOULD count how many hours he used to 12.31 "() {
        given: "logs which start in 2020 and end in 2021"
        def hours = -10.0f
        def numberOfDays = 2
        def firstHistoryLog = Mock(HistoryLog) {
            getHours() >> hours
            getRequest() >> Mock(Request){
                getEndDate() >> LocalDate.of(year+1,1,1)
                getStartDate() >> LocalDate.of(year,12,31)
            }
            getUser() >> user
        }

        historyLogRepository.findAll() >> [firstHistoryLog]
        holidayService.isWorkingDay(_ as LocalDate) >> true

        when:
        def result = historyLogService.countTheHoursUsedDuringTheYear(userID, 2021)

        then:
        result == Math.abs(hours/numberOfDays)

    }

    def "get() WHEN called SHOULD return history logs for user and date"() {
        given:
        def dateNow = LocalDate.now()
        def userId = 1L

        when:
        historyLogService.get(dateNow, userId)

        then:
        1 * historyLogRepository.findAll(_ as Filter, HistoryLogExcerptProjection.class)
    }
}

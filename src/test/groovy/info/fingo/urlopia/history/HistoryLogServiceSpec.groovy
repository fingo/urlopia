package info.fingo.urlopia.history

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.user.User
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

        historyLogRepository.findLogsByUserId(userID) >> [firstHistoryLog, secondHistoryLog]
        holidayService.isWorkingDay(_ as LocalDate) >> true

        when:
        def result = historyLogService.countTheHoursUsedDuringTheYear(userID, 2021)

        then:
        result == Math.abs(firstHours + secondHours)

    }

    def "countTheHoursUsedDuringTheYear() WHEN called with request which is overlapping the given year SHOULD return correct number of hours used"() {
        given: "logs which start in 2021 and end in 2022"
        def hours = -24.0f
        def firstHistoryLog = Mock(HistoryLog) {
            getHours() >> hours
            getRequest() >> Mock(Request){
                getEndDate() >> LocalDate.of(year + 1,1,6)
                getStartDate() >> LocalDate.of(year,12,30)
            }
            getUser() >> user
        }

        historyLogRepository.findLogsByUserId(userID) >> [firstHistoryLog]
        holidayService.isWorkingDay(_ as LocalDate) >> {LocalDate date -> {
            def weekendStartDate = LocalDate.of(year + 1, 1, 1)
            if (date == weekendStartDate || date == weekendStartDate.plusDays(1)) {
                return false
            }
            return true
        }}

        when:
        def resultYear2021 = historyLogService.countTheHoursUsedDuringTheYear(userID, 2021)
        def resultYear2022 = historyLogService.countTheHoursUsedDuringTheYear(userID, 2022)

        then:
        resultYear2021 == 8.0f
        resultYear2022 == 16.0f
    }

    def "countTheHoursUsedDuringTheYear() WHEN called with multiple requests SHOULD return correct number of hours used"() {
        given: "user work time"
        def workTime = 8.0f

        def notIncludedLogOnLeft = Mock(HistoryLog) {
            getHours() >> -3 * workTime
            getRequest() >> Mock(Request){
                getStartDate() >> LocalDate.of(year - 1,12,22)
                getEndDate() >> LocalDate.of(year - 1,12,24)
            }
        }

        def logOverlappingOnLeft = Mock(HistoryLog) {
            getHours() >> -4 * workTime
            getRequest() >> Mock(Request){
                getStartDate() >> LocalDate.of(year - 1,12,30)
                getEndDate() >> LocalDate.of(year,1,2)
            }
        }

        def fullyIncludedLog = Mock(HistoryLog) {
            getHours() >> -4 * workTime
            getRequest() >> Mock(Request){
                getStartDate() >> LocalDate.of(year,3,30)
                getEndDate() >> LocalDate.of(year,4,2)
            }
        }

        def logOverlappingOnRight = Mock(HistoryLog) {
            getHours() >> -2 * workTime
            getRequest() >> Mock(Request){
                getStartDate() >> LocalDate.of(year,12,31)
                getEndDate() >> LocalDate.of(year + 1,01,1)
            }
        }

        def notIncludedLogOnRight = Mock(HistoryLog) {
            getHours() >> -3 * workTime
            getRequest() >> Mock(Request){
                getStartDate() >> LocalDate.of(year + 1,02,22)
                getEndDate() >> LocalDate.of(year + 1,02,24)
            }
        }

        historyLogRepository.findLogsByUserId(userID) >> [notIncludedLogOnLeft,
                                                          logOverlappingOnLeft,
                                                          fullyIncludedLog,
                                                          logOverlappingOnRight,
                                                          notIncludedLogOnRight]
        holidayService.isWorkingDay(_ as LocalDate) >> true

        when:
        def result = historyLogService.countTheHoursUsedDuringTheYear(userID, year)

        then:
        result == 7 * workTime
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

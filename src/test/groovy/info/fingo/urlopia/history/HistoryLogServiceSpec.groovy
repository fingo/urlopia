package info.fingo.urlopia.history

import info.fingo.urlopia.config.persistance.filter.Filter
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
    def historyLogService = new HistoryLogService(historyLogRepository, userRepository, workingDaysCalculator)

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

    def "countTheHoursUsedDuringTheYear() WHEN called with year and user SHOULD count how many hours he used"() {
        given: "logs which show two different workTime "
        def firstHours = -10.0f
        def firstHistoryLog = Mock(HistoryLog) {
            getHours() >> firstHours
            getRequest() >> Mock(Request)
        }

        def secondHours = -8.0f
        def secondHistoryLog = Mock(HistoryLog) {
            getHours() >> secondHours
            getRequest() >> Mock(Request)
        }

        historyLogRepository.findLogsByUserIdAndCreatedBetween(_ as Long,
                _ as LocalDateTime, _ as LocalDateTime) >> [firstHistoryLog, secondHistoryLog]

        when:
        def result = historyLogService.countTheHoursUsedDuringTheYear(5L, 2021)

        then:
        result == Math.abs(firstHours + secondHours)

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

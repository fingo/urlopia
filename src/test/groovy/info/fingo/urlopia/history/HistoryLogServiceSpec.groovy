package info.fingo.urlopia.history

import info.fingo.urlopia.api.v2.history.UsedHoursFromMonthCalculator
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.user.UserRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class HistoryLogServiceSpec extends Specification {
    def historyLogRepository = Mock(HistoryLogRepository)
    def userRepository = Mock(UserRepository)
    def workingDaysCalculator = Mock(WorkingDaysCalculator)
    def usedHoursFromMonthCalculator = Mock(UsedHoursFromMonthCalculator)

    def historyLogService = new HistoryLogService(historyLogRepository, userRepository, workingDaysCalculator, usedHoursFromMonthCalculator)
    def userID = 5;




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

    def "get() WHEN called SHOULD return history logs for user and date"() {
        given:
        def dateNow = LocalDate.now()
        def userId = 1L

        when:
        historyLogService.get(dateNow, userId)

        then:
        1 * historyLogRepository.findAll(_ as Filter, HistoryLogExcerptProjection.class)
    }

    def "countUsedHoursInMonth WHEN user has only requests different than normal SHOULD return 0"(){
        given:
        def firstRequest = Mock(Request){
            isNormal() >> false
            getStatus() >> Request.Status.ACCEPTED
        }
        def firstLog = Mock(HistoryLog){
            getRequest() >> firstRequest
        }

        def secondRequest = Mock(Request){
            isNormal() >> false
            getStatus() >> Request.Status.ACCEPTED
        }
        def secondLog = Mock((HistoryLog)){
            getRequest() >> secondRequest
        }

        historyLogRepository.findLogsByUserId(_ as Long) >> [firstLog, secondLog]

        when:
        def result = historyLogService.countUsedHoursInMonth(5,2021,1)

        then:
        result == 0;
    }

    def "countUsedHoursInMonth WHEN user has normal requests SHOULD return sum of calculated hours"(){
        given:
        usedHoursFromMonthCalculator.countUsedHours(_ as Integer, _ as Integer, _ as HistoryLog) >> 8
        def firstRequest = Mock(Request){
            isNormal() >> true
            getStatus() >> Request.Status.ACCEPTED
        }
        def firstLog = Mock(HistoryLog){
            getRequest() >> firstRequest
        }

        def secondRequest = Mock(Request){
            isNormal() >> true
            getStatus() >> Request.Status.ACCEPTED
        }
        def secondLog = Mock((HistoryLog)){
            getRequest() >> secondRequest
        }

        historyLogRepository.findLogsByUserId(_ as Long) >> [firstLog, secondLog]

        when:
        def result = historyLogService.countUsedHoursInMonth(5,2021,1)

        then:
        result == 16;
    }

    def "countUsedHoursInMonth WHEN user has normal and different requests SHOULD return values only for normal one"(){
        given:
        usedHoursFromMonthCalculator.countUsedHours(_ as Integer, _ as Integer, _ as HistoryLog) >> 8
        def firstRequest = Mock(Request){
            isNormal() >> true
            getStatus() >> Request.Status.ACCEPTED
        }
        def firstLog = Mock(HistoryLog){
            getRequest() >> firstRequest
        }

        def secondRequest = Mock(Request){
            isNormal() >> false
            getStatus() >> Request.Status.ACCEPTED
        }
        def secondLog = Mock((HistoryLog)){
            getRequest() >> secondRequest
        }

        historyLogRepository.findLogsByUserId(_ as Long) >> [firstLog, secondLog]

        when:
        def result = historyLogService.countUsedHoursInMonth(5,2021,1)

        then:
        result == 8;
    }
}

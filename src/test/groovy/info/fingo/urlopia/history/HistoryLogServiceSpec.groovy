package info.fingo.urlopia.history

import info.fingo.urlopia.api.v2.history.NoSuchHistoryLogException
import info.fingo.urlopia.api.v2.history.UpdateLogCountingYearInput
import info.fingo.urlopia.api.v2.history.usedHoursCalculator.UsedHoursFromMonthCalculator
import info.fingo.urlopia.config.persistance.filter.Filter
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
    def usedHoursFromMonthCalculator = Mock(UsedHoursFromMonthCalculator)
    def historyLogFromEventHandler = Mock(HistoryLogFromEventHandler)

    def historyLogService = new HistoryLogService(historyLogRepository,
                                                  userRepository,
                                                  workingDaysCalculator,
                                                  usedHoursFromMonthCalculator,
                                                  historyLogFromEventHandler)

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
        result == 0
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
        result == 16
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
        result == 8
    }

    def "updateCountingYear WHEN called with updateLogCountingYearInput SHOULD get log with existing id from it and set countForNextYear field"(){
        given:
        def updateLogCounting = new UpdateLogCountingYearInput(1, countForNextYear)

        def user = Mock(User){
            getFullName() >> ""
        }
        def historyLog = new HistoryLog()
        historyLog.setDecider(user)
        historyLogRepository.findById(_ as Long) >> Optional.of(historyLog)
        historyLogRepository.save(_ as HistoryLog) >> historyLog

        when:
        def result = historyLogService.updateCountingYear(updateLogCounting)

        then:
        result.getCountForNextYear() == countForNextYear

        where:
        countForNextYear << [true,
                            false]
    }

    def "updateCountingYear WHEN called with not existing id SHOULD thrown exception"(){
        given:
        def updateLogCounting = new UpdateLogCountingYearInput(1, countForNextYear)

        historyLogRepository.findById(_ as Long) >> Optional.empty()

        when:
        historyLogService.updateCountingYear(updateLogCounting)

        then:
        thrown(NoSuchHistoryLogException)

        where:
        countForNextYear << [true,
                             false]
    }

    def "delete WHEN id not exist SHOULD throw HistoryLogNotFoundException"() {
        given:
        historyLogRepository.findById(_ as Long) >> Optional.empty()

        when:
        historyLogService.delete(1)

        then:
        thrown(HistoryLogDeleteException)
    }

    def "delete WHEN this was user last log SHOULD delete it"(){
        given:
        def mockLogToDelete = Mock(HistoryLog) {
            getUserDetailsChangeEvent() >> UserDetailsChangeEvent.USER_ACTIVATED
        }
        historyLogRepository.findById(1) >> Optional.of(mockLogToDelete)
        historyLogRepository.findByPrevHistoryLog(_ as HistoryLog) >> Optional.empty()

        when:
        historyLogService.delete(1)

        then:
        1 * historyLogRepository.delete(mockLogToDelete)
    }

    def "delete WHEN this was user first and not last log SHOULD delete it and change prev_log of next one to null"(){
        given:
        def mockLogToDelete = Mock(HistoryLog) {
            getUserDetailsChangeEvent() >> UserDetailsChangeEvent.USER_ACTIVATED
        }
        def nextLog = Mock(HistoryLog)
        historyLogRepository.findById(1) >> Optional.of(mockLogToDelete)
        historyLogRepository.findByPrevHistoryLog(_ as HistoryLog) >> Optional.of(nextLog)

        when:
        historyLogService.delete(1)

        then:
        1 * historyLogRepository.delete(mockLogToDelete)
        1 * nextLog.setPrevHistoryLog(null)
    }

    def "delete WHEN this prev and next log exists SHOULD delete it and change prev_log of next one to prev one"(){
        given:
        def nextLog = Mock(HistoryLog)
        def prevLog = Mock(HistoryLog)
        def mockLogToDelete = Mock(HistoryLog) {
            getPrevHistoryLog() >> prevLog
            getUserDetailsChangeEvent() >> UserDetailsChangeEvent.USER_ACTIVATED
        }
        historyLogRepository.findById(1) >> Optional.of(mockLogToDelete)
        historyLogRepository.findByPrevHistoryLog(_ as HistoryLog) >> Optional.of(nextLog)

        when:
        historyLogService.delete(1)

        then:
        1 * historyLogRepository.delete(mockLogToDelete)
        1 * nextLog.setPrevHistoryLog(prevLog)
    }

    def "countRemainingForCurrentYear WHEN only log without request should sum it"(){
        given:
        def log = Mock(HistoryLog){
            getHours() >> 100
        }
        def log2 = Mock(HistoryLog) {
            getHours() >> -20
        }
        historyLogRepository.findAll(_ as Filter) >>> [[log, log2], [], []]
        usedHoursFromMonthCalculator.countUsedHours(_ as Integer, _ as Integer, _ as HistoryLog) >> -20

        when:
        def result = historyLogService.countRemainingForCurrentYear(1,1)

        then:
        result == 80
    }

    def "countRemainingForCurrentYear WHEN log with request SHOULD not be added"(){
        given:
        def log = Mock(HistoryLog){
            getHours() >> 100
        }
        def log2 = Mock(HistoryLog) {
            getHours() >> -20
            getRequest() >> Mock(Request)
        }
        historyLogRepository.findAll(_ as Filter) >>> [[log, log2], [], []]

        when:
        def result = historyLogService.countRemainingForCurrentYear(1,1)

        then:
        result == 100
    }

    def "countRemainingForCurrentYear WHEN log without request from prev year but countedOnNext SHOULD be added"(){
        given:
        def log = Mock(HistoryLog){
            getHours() >> 100
        }
        def log2 = Mock(HistoryLog) {
            getHours() >> -20
        }
        historyLogRepository.findAll(_ as Filter) >>> [[log], [log2], []]

        when:
        def result = historyLogService.countRemainingForCurrentYear(1,1)

        then:
        result == 80
    }


    def "countRemainingForCurrentYear WHEN remaining from prev year SHOULD be added"(){
        given:
        def log = Mock(HistoryLog){
            getHours() >> 100
        }
        def log2 = Mock(HistoryLog) {
            getHours() >> -20
            getRequest() >> Mock(Request) {
                isNormal() >> true
                getEndDate() >> LocalDate.MAX
                getStartDate() >> LocalDate.of(2022,12,1)
            }
        }
        historyLogRepository.findAll(_ as Filter) >>> [[log], [], [log2]]
        usedHoursFromMonthCalculator.countUsedHours(_ as Integer, _ as Integer, _ as HistoryLog) >> 20

        when:
        def result = historyLogService.countRemainingForCurrentYear(1,1)

        then:
        result == 80
    }



    def "countRemainingForCurrentYear WHEN no logs SHOULD return 0"(){
        given:
        historyLogRepository.findAll(_ as Filter) >> []

        when:
        def result = historyLogService.countRemainingForCurrentYear(1,1)

        then:
        result == 0
    }

    def "delete WHEN event is missing SHOULD throw exception"(){
        given:
        def nextLog = Mock(HistoryLog)
        def prevLog = Mock(HistoryLog)
        def mockLogToDelete = Mock(HistoryLog) {
            getPrevHistoryLog() >> prevLog
        }
        historyLogRepository.findById(1) >> Optional.of(mockLogToDelete)
        historyLogRepository.findByPrevHistoryLog(_ as HistoryLog) >> Optional.of(nextLog)

        when:
        historyLogService.delete(1)

        then:
        thrown(HistoryLogDeleteException)
    }
}

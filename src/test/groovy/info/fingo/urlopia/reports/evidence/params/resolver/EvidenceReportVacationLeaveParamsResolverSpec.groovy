package info.fingo.urlopia.reports.evidence.params.resolver

import info.fingo.urlopia.history.HistoryLog
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.text.DecimalFormat

class EvidenceReportVacationLeaveParamsResolverSpec extends  Specification{
    def historyLogService = Mock(HistoryLogService)
    def decimalFormat = new DecimalFormat()
    def userID = 1
    def user = Mock(User){
        getId() >> userID
        getEc() >> false
    }
    def year = 2021
    def evidenceReportVacationLeaveParamsResolver = new EvidenceReportVacationLeaveParamsResolver(user,
                                                                                                  year,
                                                                                                  historyLogService)
    def ec_label = "urlop"
    def b2b_label = "przerwa"
    def vacationLeaveModelPrefix = "remainingTimeAtYearStart"
    def vacationLabelModelPrefix = "label"

    def "resolve() WHEN called with year and user who work all time in fullTime SHOULD return model that contains prefix and as value count how many free hours user have and divide by 8"(){
        given: "history logs with added hours"
        def firstFreeHours = 4
        def firstLog = Mock(HistoryLog){
            getRequest() >> null
            getHours() >> firstFreeHours
            getCountForNextYear() >> false
        }

        def secondFreeHours = 5
        def secondLog = Mock(HistoryLog){
            getRequest() >> null
            getHours() >> secondFreeHours
            getCountForNextYear() >> false
        }

        and: "historyLogService"
        def lastYearHours = 6
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> true
        historyLogService.countRemainingHoursForYear(_ as Long, _ as Integer) >> lastYearHours
        historyLogService.getFromYear(_ as Long, _ as Integer) >> [firstLog,secondLog]

        def sumOfHours = firstFreeHours + secondFreeHours +lastYearHours
        sumOfHours = sumOfHours / 8

        when:
        def result = evidenceReportVacationLeaveParamsResolver.resolve()

        then:
        result.containsKey(vacationLeaveModelPrefix)
        result.get(vacationLeaveModelPrefix) == decimalFormat.format(sumOfHours)


    }


    def "resolve() WHEN called with year and user who not work all time in fullTime SHOULD return model that contains prefix and as value count how many free hours user have"(){
        given: "history logs with added hours"
        def firstFreeHours = 4
        def firstLog = Mock(HistoryLog){
            getRequest() >> null
            getHours() >> firstFreeHours
            getCountForNextYear() >> false

        }

        def secondFreeHours = 5
        def secondLog = Mock(HistoryLog){
            getRequest() >> null
            getHours() >> secondFreeHours
            getCountForNextYear() >> false

        }

        and: "historyLogService"
        def lastYearHours = 6
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> false
        historyLogService.countRemainingHoursForYear(_ as Long, _ as Integer) >> lastYearHours
        historyLogService.getFromYear(_ as Long, _ as Integer) >> [firstLog,secondLog]

        when:
        def result = evidenceReportVacationLeaveParamsResolver.resolve()

        then:
        def sumOfHours = firstFreeHours + secondFreeHours + lastYearHours
        result.containsKey(vacationLeaveModelPrefix)
        result.get(vacationLeaveModelPrefix) == decimalFormat.format(sumOfHours)


    }


    def "resolve() WHEN called with year and user who is EC SHOULD return model that contains prefix and EC LABEL as value"(){
        given: "ec user"

        user = Mock(User){
            getId() >> userID
            getEc() >> true
        }

        evidenceReportVacationLeaveParamsResolver = new EvidenceReportVacationLeaveParamsResolver(user,
                year,
                historyLogService)

        and: "historyLogService"
        def lastYearHours = 6
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> false
        historyLogService.countRemainingHoursForYear(_ as Long, _ as Integer) >> lastYearHours
        historyLogService.getFromYear(_ as Long, _ as Integer) >> []

        when:
        def result = evidenceReportVacationLeaveParamsResolver.resolve()

        then:
        result.containsKey(vacationLabelModelPrefix)
        result.get(vacationLabelModelPrefix) == ec_label
    }

    def "resolve() WHEN called with year and user who is not EC SHOULD return model that contains prefix and B2B LABEL as value"(){
        given: "not ec user"
        user = Mock(User){
            getId() >> userID
            getEc() >> false
        }
        evidenceReportVacationLeaveParamsResolver = new EvidenceReportVacationLeaveParamsResolver(user,
                year,
                historyLogService)

        and: "historyLogService"
        def lastYearHours = 6
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> false
        historyLogService.countRemainingHoursForYear(_ as Long, _ as Integer) >> lastYearHours
        historyLogService.getFromYear(_ as Long, _ as Integer) >> []

        when:
        def result = evidenceReportVacationLeaveParamsResolver.resolve()

        then:
        result.containsKey(vacationLabelModelPrefix)
        result.get(vacationLabelModelPrefix) == b2b_label
    }

    def "resolve() WHEN called with year and user who work all time in fullTime SHOULD not count historyLogs with countForNextYear = true"(){
        given: "history logs with added hours"
        def firstFreeHours = 4
        def firstLog = Mock(HistoryLog){
            getRequest() >> null
            getHours() >> firstFreeHours
            getCountForNextYear() >> true
        }

        def secondFreeHours = 5
        def secondLog = Mock(HistoryLog){
            getRequest() >> null
            getHours() >> secondFreeHours
            getCountForNextYear() >> false
        }

        and: "historyLogService"
        def lastYearHours = 6
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> true
        historyLogService.countRemainingHoursForYear(_ as Long, _ as Integer) >> lastYearHours
        historyLogService.getFromYear(_ as Long, _ as Integer) >> [firstLog,secondLog]

        def sumOfHours = secondFreeHours +lastYearHours
        sumOfHours = sumOfHours / 8

        when:
        def result = evidenceReportVacationLeaveParamsResolver.resolve()

        then:
        result.containsKey(vacationLeaveModelPrefix)
        result.get(vacationLeaveModelPrefix) == decimalFormat.format(sumOfHours)


    }
}

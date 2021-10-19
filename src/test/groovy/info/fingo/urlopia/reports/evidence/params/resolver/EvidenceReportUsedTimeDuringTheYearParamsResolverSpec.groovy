package info.fingo.urlopia.reports.evidence.params.resolver

import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.text.DecimalFormat

class EvidenceReportUsedTimeDuringTheYearParamsResolverSpec extends Specification{
        def historyLogService= Mock(HistoryLogService)
        def requestService = Mock(RequestService)
        def DECIMAL_FORMAT = new DecimalFormat()
        def FULL_TIME_DIVIDER =8.0f
        def userId = 5L
        def user = Mock(User){
            getId() >> userId
        }
        def year = 2021
        def evidenceReportUsedTimeDuringTheYearParamsResolver = new EvidenceReportUsedTimeDuringTheYearParamsResolver(user,
                                                                                                                      year,
                                                                                                                      historyLogService,
                                                                                                                      requestService)
        def usedTimeModelPrefix = "usedTimeDuringTheYear"
        def timeUnitModelPrefix = "timeUnit"
        def PART_TIME_WORKER_SYMBOL = "godz"
        def FULL_TIME_WORKER_SYMBOL = "dni"




    def "resolve WHEN called with use who work on fullTime all time in year SHOULD return model that contains prefix and his hours divide by 8 as value"(){
            given:
            def usedHours = 8d
            requestService.countTheHoursUsedDuringTheYear(userId, year) >> usedHours
            historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> true

            when:
            def result = evidenceReportUsedTimeDuringTheYearParamsResolver.resolve()

            then:
            result.containsKey(usedTimeModelPrefix)
            result.get(usedTimeModelPrefix) == DECIMAL_FORMAT.format(usedHours/ FULL_TIME_DIVIDER)

        }

        def "resolve WHEN called with use who not work on fullTime all time in year SHOULD return model that contains prefix and his hours as value"(){
            given:
            def usedHours = 8
            requestService.countTheHoursUsedDuringTheYear(_ as Long, _ as Integer) >> usedHours;
            historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> false

            when:
            def result = evidenceReportUsedTimeDuringTheYearParamsResolver.resolve()

            then:
            result.containsKey(usedTimeModelPrefix)
            result.get(usedTimeModelPrefix) ==  DECIMAL_FORMAT.format(usedHours)
        }


    def "resolve() WHEN user was not fullTimeWorker all time in year SHOULD return model that contains prefix and partTime Worker Symbol as value"() {
        given:
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> false

        when:
        def result =  evidenceReportUsedTimeDuringTheYearParamsResolver.resolve()

        then:
        result.containsKey(timeUnitModelPrefix)
        result.get(timeUnitModelPrefix) == PART_TIME_WORKER_SYMBOL
    }

    def "resolve() WHEN user was fullTimeWorker all time in year SHOULD return model that contains prefix and fullTime Worker Symbol as value"() {
        given:
        historyLogService.checkIfWorkedFullTimeForTheWholeYear(_ as Long, _ as Integer) >> true

        when:
        def result =  evidenceReportUsedTimeDuringTheYearParamsResolver.resolve()

        then:
        result.containsKey(timeUnitModelPrefix)
        result.get(timeUnitModelPrefix) == FULL_TIME_WORKER_SYMBOL
    }
}

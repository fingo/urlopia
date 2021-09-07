package info.fingo.urlopia.reports.evidence.params.resolver

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.holidays.Holiday
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.reports.evidence.EvidenceReportModel
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class EvidenceReportDayParamsResolverSpec extends Specification {
    def holidayService = Mock(HolidayService){
        generateHolidaysList(_ as Integer) >> []
    }
    def requestService = Mock(RequestService){
        getByUserAndDate(_ as Long, _ as LocalDate) >> []
    }
    def presenceConfirmationService = Mock(PresenceConfirmationService){
        getByUserAndDate(_ as Long, _ as LocalDate) >> []
    }


    def day = 4
    def month = 1
    def year = 2020
    def validDate = LocalDate.of(year, month, day)
    def userId = 5
    def user = Mock(User) {
        getId() >> userId
    }

    def "resolve() WHEN called with year in future, SHOULD return model that contains prefix and empty string as value"() {
        given: "year from future"
        def year = LocalDate.now().plusYears(2).getYear()
        def day = 1
        def month = 1

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                holidayService,
                                                                                requestService,
                                                                                presenceConfirmationService )
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)


        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == ""
    }

    def "resolve() WHEN called with not existing date from past SHOULD return model that contains prefix and - as value"() {
        given: "not existing date from past"
        def year = 2020
        def day = 31
        def month = 02

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                holidayService,
                                                                                requestService,
                                                                                presenceConfirmationService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) =="-"
    }

    def "resolve() WHEN called with holiday date SHOULD return model that contains prefix and handler result as value"() {
        given: "holiday mock"
        def holidayName = "holiday"
        def holiday = new Holiday(holidayName, validDate)

        and: "holidayService mock that say example date is holiday"
        holidayService.isHoliday(_ as LocalDate) >> true
        holidayService.getHolidayByDate(_ as LocalDate) >> holiday

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)
        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                    holidayService,
                                                                                    requestService,
                                                                                    presenceConfirmationService)
        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)

    }

    def "resolve() WHEN called with weekend date SHOULD return model that contains prefix and handler result as value"() {
        given: "holidayService mock that say example date is weekend"
        def date = LocalDate.of(year,month,day)
        holidayService.isWeekend(date) >> true
        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                holidayService,
                                                                                requestService,
                                                                                presenceConfirmationService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

    def "resolve() WHEN called with working day date without accepted request SHOULD return model that contains prefix and handler result as value"() {
        given: "requestService mock that not return any accepted request on that day"
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> []

        and: "holidayService mock that say example date is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true


        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                holidayService,
                requestService,
                presenceConfirmationService)

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

    def "resolve() WHEN called with working day date with accepted request SHOULD return model that contains prefix and handler result as value"() {
        given: "requestService mock that not return any accepted request on that day"
        def request = Mock(Request) {
            getStatus() >> Request.Status.ACCEPTED
        }
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> [request]

        and: "holidayService mock that say example date is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                holidayService,
                requestService,
                presenceConfirmationService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

}
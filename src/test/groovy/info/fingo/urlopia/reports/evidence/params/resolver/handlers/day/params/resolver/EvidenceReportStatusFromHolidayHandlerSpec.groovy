package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver

import info.fingo.urlopia.holidays.Holiday
import info.fingo.urlopia.holidays.HolidayService
import spock.lang.Specification

import java.time.LocalDate

class EvidenceReportStatusFromHolidayHandlerSpec extends Specification {

    def holidayService = Mock(HolidayService)
    def holidayName = "default"
    def holidayDate = LocalDate.of(2021, 1, 1)
    def evidenceReportStatusFromHolidayHandler = new EvidenceReportStatusFromHolidayHandler(holidayService)
    def defaultHoliday = new Holiday(holidayName, holidayDate)
    def SPECIAL_SYMBOL = "dwś"

    def "handle() WHEN called with holiday that was move from Saturday SHOULD return special symbol"() {
        given:
        holidayService.generateHolidaysList(_ as Integer) >> [defaultHoliday]
        def holiday = new Holiday(holidayName, holidayDate.plusDays(1))

        when:
        def result = evidenceReportStatusFromHolidayHandler.handle(holiday)

        then:
        result == SPECIAL_SYMBOL
    }

    def "handle() WHEN called with holiday that date was not change SHOULD return -"() {
        given:
        holidayService.generateHolidaysList(_ as Integer) >> [defaultHoliday]
        def holiday = new Holiday(holidayName, holidayDate)

        when:
        def result = evidenceReportStatusFromHolidayHandler.handle(holiday)

        then:
        result == "-"
    }
}

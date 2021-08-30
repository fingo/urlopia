package info.fingo.urlopia.api.v2.calendar

import info.fingo.urlopia.config.persistance.filter.Filter
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate

class CalendarControllerV2Spec extends Specification {
    def requesterId = 1L
    def startDate = LocalDate.now()
    def endDate = startDate.plusDays(1)
    def calendarService = Mock(CalendarService)
    def calendar = Mock(Map<LocalDate, SingleDayOutput>)
    def calendarOutput = GroovyMock(CalendarOutput) {
        getCalendar() >> calendar
    }
    def filters = new String[0]


    def calendarControllerV2 = new CalendarControllerV2(calendarService)

    def "getCalendarInformation() WHEN called with proper params SHOULD return calendarOutput"() {
        given:
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> requesterId
        }
        calendarService.getCalendarInfo(requesterId, startDate, endDate, _ as Filter) >> calendarOutput

        when:
        def output = calendarControllerV2.getCalendarInformation(startDate, endDate, filters, httpRequest)

        then:
        output == calendarOutput
    }
}

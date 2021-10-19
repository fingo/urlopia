package info.fingo.urlopia.api.v2.reports.attendance.resolver.handlers.user.params.resolver

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.reports.ReportStatusFromRequestType
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.request.absence.SpecialAbsenceReason
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class MonthlyAttendanceListReportDayHandlerSpec extends Specification {
    def holidayService = Mock(HolidayService)
    def requestService = Mock(RequestService)
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def handler = new MonthlyAttendanceListReportDayHandler(holidayService, requestService, presenceConfirmationService)

    def userId = 1
    def sampleUser = Mock(User) {
        getId() >> userId
    }

    def fallbackValue = "-"
    def today = LocalDate.now()
    def sampleYear = today.getYear() - 1
    def sampleMonth = today.getMonthValue()
    def sampleDay = 1
    def sampleDate = LocalDate.of(sampleYear, sampleMonth, sampleDay)

    def "handle() WHEN user is null SHOULD return no value"() {
        expect:
        handler.handle(sampleYear, sampleMonth, sampleDay, null) == ""
    }

    def "handle() WHEN given date is holiday SHOULD return fallback value"() {
        given:
        holidayService.isHoliday(_ as LocalDate) >> true

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == fallbackValue
    }

    def "handle() WHEN given date is weekend SHOULD return fallback value"() {
        given:
        holidayService.isHoliday(_ as LocalDate) >> false
        holidayService.isWeekend(_ as LocalDate) >> true

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == fallbackValue
    }

    def "handle() WHEN given date is working day and there is accepted normal request SHOULD return proper abbreviation"() {
        given:
        holidayService.isWorkingDay(_ as LocalDate) >> true
        presenceConfirmationService.getByUserAndDate(userId, sampleDate) >> []

        and:
        def normalRequest = Mock(Request) {
            getStatus() >> Request.Status.ACCEPTED
            getType() >> RequestType.NORMAL
            getSpecialTypeInfo() >> ""
        }
        requestService.getByUserAndDate(userId, sampleDate) >> [normalRequest]

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == ReportStatusFromRequestType.NORMAL.getMonthlyPresenceReportStatus()
    }

    def "handle() WHEN given date is working day and there is accepted occasional request SHOULD return proper abbreviation"() {
        given:
        holidayService.isWorkingDay(_ as LocalDate) >> true
        presenceConfirmationService.getByUserAndDate(userId, sampleDate) >> []

        and:
        def occasionalRequest = Mock(Request) {
            getStatus() >> Request.Status.ACCEPTED
            getType() >> RequestType.OCCASIONAL
            getSpecialTypeInfo() >> ""
        }
        requestService.getByUserAndDate(userId, sampleDate) >> [occasionalRequest]

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == ReportStatusFromRequestType.OCCASIONAL.getMonthlyPresenceReportStatus()
    }

    def "handle() WHEN given date is working day and there is accepted special request SHOULD return proper abbreviation"() {
        given:
        holidayService.isWorkingDay(_ as LocalDate) >> true
        presenceConfirmationService.getByUserAndDate(userId, sampleDate) >> []

        and:
        def occasionalRequest = Mock(Request) {
            getStatus() >> Request.Status.ACCEPTED
            getType() >> RequestType.SPECIAL
            getSpecialTypeInfo() >> SpecialAbsenceReason.BLOOD_DONATION.toString()
        }
        requestService.getByUserAndDate(userId, sampleDate) >> [occasionalRequest]

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == ReportStatusFromRequestType.BLOOD_DONATION.getMonthlyPresenceReportStatus()
    }

    def "handle() WHEN given date is working day and there is presence confirmation SHOULD return working hours"() {
        given:
        holidayService.isWorkingDay(_ as LocalDate) >> true
        requestService.getByUserAndDate(userId, sampleDate) >> []

        and:
        def presenceConfirmation = Mock(PresenceConfirmation) {
            getStartTime() >> LocalTime.of(8, 0)
            getEndTime() >> LocalTime.of(15, 30)
        }
        presenceConfirmationService.getByUserAndDate(userId, sampleDate) >> [presenceConfirmation]
        presenceConfirmationService.countWorkingHoursInDay(presenceConfirmation) >> 7

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == "7"
    }

    def "handle() WHEN given date is working day and there is no presence confirmation SHOULD return fallback value"() {
        given:
        holidayService.isWorkingDay(_ as LocalDate) >> true
        presenceConfirmationService.getByUserAndDate(userId, sampleDate) >> []
        requestService.getByUserAndDate(userId, sampleDate) >> []

        when:
        def result = handler.handle(sampleYear, sampleMonth, sampleDay, sampleUser)

        then:
        result == fallbackValue
    }
}

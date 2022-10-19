package info.fingo.urlopia.api.v2.reports.attendance.resolver

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class MonthlyAttendanceListReportUserParamsResolverSpec extends Specification {
    def holidayService = Mock(HolidayService) {
        isWorkingDay(_ as LocalDate) >> false
    }
    def requestService = Mock(RequestService)
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def historyLogService = Mock(HistoryLogService)

    def userFullName = "John Doe"
    def sampleUser = Mock(User) {
        getFullName() >> userFullName
    }
    def sampleYear = 2021
    def sampleMonth = 9

    def resolver = new MonthlyAttendanceListReportUserParamsResolver(sampleUser, sampleYear, sampleMonth,
                                                                     holidayService, requestService,
                                                                     presenceConfirmationService, historyLogService)

    def "resolve() WHEN user is null SHOULD put empty string as full name value"() {
        given:
        def resolver = new MonthlyAttendanceListReportUserParamsResolver(null, sampleYear, sampleMonth,
                                                                         holidayService, requestService,
                                                                         presenceConfirmationService, historyLogService)

        when:
        def result = resolver.resolve()

        then:
        result.containsKey("fullName")
        result.get("fullName") == ""
    }

    def "resolve() SHOULD put user's full name as full name value"() {
        when:
        def result = resolver.resolve()

        then:
        result.containsKey("fullName")
        result.get("fullName") == userFullName
    }

    def "resolve() SHOULD put formatted days from a given month to result map"() {
        when:
        def result = resolver.resolve()

        then:
        result.containsKey("day01")
    }
}

package info.fingo.urlopia.api.v2.reports.attendance.resolver

import spock.lang.Specification

class MonthlyAttendanceListReportDateParamsResolverSpec extends Specification {
    def "resolve() SHOULD return map with correct entries"() {
        given:
        def year = 2021
        def month = 9
        def resolver = new MonthlyAttendanceListReportDateParamsResolver(year, month)

        when:
        def result = resolver.resolve()

        then:
        result.containsKey("month")
        result.get("month") == "9"
        result.containsKey("year")
        result.get("year") == "2021"
    }
}

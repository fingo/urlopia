package info.fingo.urlopia.api.v2.calendar

import info.fingo.urlopia.history.HistoryLogExcerptProjection
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.LocalTime

class VacationHoursModificationOutputSpec extends Specification {
    def comment = "test"
    def decider = "decider1"
    def created = LocalDateTime.now()
    def time = created.toLocalTime()
    def value = 10.0F
    def hoursRemaining = 100.0F
    def userWorkTime = 8.0F
    def workTimeNumerator = 4
    def workTimeDenominator = 5
    def historyLogExcerptProjection = Mock(HistoryLogExcerptProjection) {
        getComment() >> comment
        getDeciderFullName() >> decider
        getCreated() >> created
        getHours() >> value
        getHoursRemaining() >> hoursRemaining
        getUserWorkTime() >> userWorkTime
        getWorkTimeNumerator() >> workTimeNumerator
        getWorkTimeDenominator() >> workTimeDenominator
    }

    def "fromHistoryLogExcerptProjection() WHEN called SHOULD return VacationHoursModificationOutput"() {
        when:
        def output = VacationHoursModificationOutput.fromHistoryLogExcerptProjection(historyLogExcerptProjection)
        
        then:
        output.getComment() == comment
        output.getDeciderFullName() == decider
        output.getTime() == time
        output.getValue() == value
        output.getHoursRemaining() == hoursRemaining
        output.getUserWorkTime() == userWorkTime
        output.getWorkTimeNumerator() == workTimeNumerator
        output.getWorkTimeDenominator() == workTimeDenominator
    }
}

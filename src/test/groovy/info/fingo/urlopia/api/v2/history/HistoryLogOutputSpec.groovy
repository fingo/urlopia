package info.fingo.urlopia.api.v2.history

import info.fingo.urlopia.history.HistoryLogExcerptProjection
import spock.lang.Specification

import java.time.LocalDateTime

class HistoryLogOutputSpec extends Specification {

    def projection = Mock(HistoryLogExcerptProjection) {
        getId() >> 1L
        getCreated() >> LocalDateTime.now()
        getComment() >> "comment"
        getDeciderFullName() >> "Sample decider"
        getWorkTimeDenominator() >> 1
        getWorkTimeNumerator() >> 1
        getUserWorkTime() >> 8.0
        getHours() >> 1.0
        getHoursRemaining() >> 10.0
    }

    def "from() should return List of instances with same values at mapped fields"() {
        given:
        def projections = List.of(projection)

        when:
        def result = HistoryLogOutput.from(projections)

        then:
        for(def i=0; i < result.size(); i++) {
            def currentLog = result.get(i)
            def currentProj = projections.get(i)
            currentLog.getId() == currentProj.getId()
            currentLog.getCreated() == currentProj.getCreated()
            currentLog.getComment() == currentProj.getComment()
            currentLog.getDeciderFullName() == currentProj.getDeciderFullName()
            currentLog.getWorkTimeDenominator() == currentProj.getWorkTimeDenominator()
            currentLog.getWorkTimeNumerator() == currentProj.getWorkTimeNumerator()
            currentLog.getUserWorkTime() == currentProj.getUserWorkTime()
            currentLog.getHours() == currentProj.getHours()
            currentLog.getHoursRemaining() == currentProj.getHoursRemaining()
        }
    }
}

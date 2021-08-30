package info.fingo.urlopia.api.v2.calendar

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import spock.lang.Specification

import java.time.LocalTime

class PresenceConfirmationOutputSpec extends Specification {
    def "fromPresenceConfirmation() WHEN called with PresenceConfirmation SHOULD return PresenceConfirmationOutput"() {
        given:
        def startTime = LocalTime.now()
        def endTime = startTime.plusHours(8)
        def presenceConfirmation = Mock(PresenceConfirmation) {
            getStartTime() >> startTime
            getEndTime() >> endTime
        }

        when:
        def output = PresenceConfirmationOutput.fromPresenceConfirmation(presenceConfirmation)

        then:
        output.getStartTime() == startTime
        output.getEndTime() == endTime
        output.isConfirmed()
    }

    def "fromPresenceConfirmation() WHEN called with null SHOULD return PresenceConfirmationOutput"() {
        when:
        def output = PresenceConfirmationOutput.fromPresenceConfirmation(null)

        then:
        output.getStartTime() == null
        output.getEndTime() == null
        !output.isConfirmed()
    }
}

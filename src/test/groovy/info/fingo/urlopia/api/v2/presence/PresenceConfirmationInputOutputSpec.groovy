package info.fingo.urlopia.api.v2.presence

import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class PresenceConfirmationInputOutputSpec extends Specification {
    def userId = 1L
    def sampleDate = LocalDate.of(2021, 8, 10)
    def sampleStartTime = LocalTime.of(8, 0)
    def sampleEndTime = LocalTime.of(16, 0)

    def "from WHEN given presence confirmation SHOULD create presence confirmation dto"() {
        given:
        def user = Mock(User) { getId() >> userId }
        def confirmation = new PresenceConfirmation(user, sampleDate, sampleStartTime, sampleEndTime)

        when:
        def dto = PresenceConfirmationInputOutput.from(confirmation)

        then:
        dto.getUserId() == userId
        dto.getDate() == sampleDate
        dto.getStartTime() == sampleStartTime
        dto.getEndTime() == sampleEndTime
    }
}

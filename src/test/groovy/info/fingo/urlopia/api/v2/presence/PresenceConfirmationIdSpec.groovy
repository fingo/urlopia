package info.fingo.urlopia.api.v2.presence

import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class PresenceConfirmationIdSpec extends Specification {
    def userId = 1L
    def sampleDate = LocalDate.of(2021, 8, 10)

    def "from WHEN user id and date are given SHOULD create presence confirmation id"() {
        when:
        def confirmationId = PresenceConfirmationId.from(userId, sampleDate)

        then:
        confirmationId.getUserId() == userId
        confirmationId.getDate() == sampleDate
    }

    def "from WHEN user and date are given SHOULD create presence confirmation id"() {
        given:
        def user = Mock(User) { getId() >> userId }

        when:
        def confirmationId = PresenceConfirmationId.from(user, sampleDate)

        then:
        confirmationId.getUserId() == userId
        confirmationId.getDate() == sampleDate
    }
}

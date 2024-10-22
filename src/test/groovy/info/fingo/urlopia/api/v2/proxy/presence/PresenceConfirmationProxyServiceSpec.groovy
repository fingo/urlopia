package info.fingo.urlopia.api.v2.proxy.presence

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationInputOutput
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.api.v2.proxy.ProxyException
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class PresenceConfirmationProxyServiceSpec extends Specification {
    def sampleProxyToken = "sampleProxyToken"
    def userService = Mock(UserService)
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def presenceConfirmationProxyService = new PresenceConfirmationProxyService(userService, presenceConfirmationService)

    def "confirmPresence() WHEN hours are blank SHOULD confirm his presence with time based on his work time"() {
        given:
        def sampleUserId = 1L
        def sampleEmail = "test@test.com"
        def sampleUser = Mock(User) {
            getId() >> sampleUserId
            getWorkTime() >> 8.0f
        }

        and:
        def presenceConfirmationProxyInput = new PresenceConfirmationProxyInput(sampleProxyToken, sampleEmail, "")
        userService.getByMail(sampleEmail) >> sampleUser

        when:
        presenceConfirmationProxyService.confirmPresence(presenceConfirmationProxyInput)

        then:
        1 * presenceConfirmationService.confirmPresence(sampleUser, {
            it instanceof PresenceConfirmationInputOutput
            it.getDate() == LocalDate.now()
            it.getStartTime() == LocalTime.of(8, 0)
            it.getEndTime() == LocalTime.of(8, 0).plusMinutes((60 * sampleUser.getWorkTime()) as long)
            it.getUserId() == sampleUserId
        })
    }

    def "confirmPresence() WHEN hours are valid SHOULD confirm his presence"() {
        given:
        def sampleUserId = 1L
        def sampleEmail = "test@test.com"
        def sampleUser = Mock(User) {
            getId() >> sampleUserId
            getWorkTime() >> 8.0f
        }

        and:
        def presenceConfirmationProxyInput = new PresenceConfirmationProxyInput(sampleProxyToken, sampleEmail, testHours)
        userService.getByMail(sampleEmail) >> sampleUser

        when:
        presenceConfirmationProxyService.confirmPresence(presenceConfirmationProxyInput)

        then:
        1 * presenceConfirmationService.confirmPresence(sampleUser, {
            it instanceof PresenceConfirmationInputOutput
            it.getDate() == LocalDate.now()
            it.getStartTime() == LocalTime.of(stHour, stMinute)
            it.getEndTime() == LocalTime.of(etHour, etMinute)
            it.getUserId() == sampleUserId
        })

        where:
        testHours     | stHour | stMinute | etHour | etMinute
        "9-9"         | 9      | 0        | 9      | 0
        "09-9"        | 9      | 0        | 9      | 0
        "9:30-9"      | 9      | 30       | 9      | 0
        "09:30-9"     | 9      | 30       | 9      | 0
        "9-09"        | 9      | 0        | 9      | 0
        "09-09"       | 9      | 0        | 9      | 0
        "9:30-09"     | 9      | 30       | 9      | 0
        "09:30-09"    | 9      | 30       | 9      | 0
        "9-9:30"      | 9      | 0        | 9      | 30
        "09-9:30"     | 9      | 0        | 9      | 30
        "9:30-9:30"   | 9      | 30       | 9      | 30
        "09:30-9:30"  | 9      | 30       | 9      | 30
        "9-09:30"     | 9      | 0        | 9      | 30
        "09-09:30"    | 9      | 0        | 9      | 30
        "9:30-09:30"  | 9      | 30       | 9      | 30
        "09:30-09:30" | 9      | 30       | 9      | 30
    }

    def "confirmPresence() WHEN hours are invalid SHOULD throw an exception"() {
        given:
        def sampleEmail = "test@test.com"
        def sampleUser = Mock(User) {
            getWorkTime() >> 8.0f
        }

        and:
        def presenceConfirmationProxyInput = new PresenceConfirmationProxyInput(sampleProxyToken, sampleEmail, testHours)
        userService.getByMail(sampleEmail) >> sampleUser

        when:
        presenceConfirmationProxyService.confirmPresence(presenceConfirmationProxyInput)

        then:
        def ex = thrown(ProxyException)
        ex.getMessage() == ProxyException.invalidTimeRange().getMessage()

        where:
        testHours    | _
        "9:3-9"      | _
        "09:3-9"     | _
        "9:3-09"     | _
        "09:3-09"    | _
        "9-9:3"      | _
        "09-9:3"     | _
        "9:3-9:3"    | _
        "9:30-9:3"   | _
        "09:3-9:3"   | _
        "09:30-9:3"  | _
        "9:3-9:30"   | _
        "09:3-9:30"  | _
        "9-09:3"     | _
        "09-09:3"    | _
        "9:3-09:3"   | _
        "9:30-09:3"  | _
        "09:3-09:3"  | _
        "09:30-09:3" | _
        "9:3-09:30"  | _
        "09:3-09:30" | _
    }
}

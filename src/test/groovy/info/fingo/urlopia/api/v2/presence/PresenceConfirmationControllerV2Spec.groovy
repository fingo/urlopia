package info.fingo.urlopia.api.v2.presence

import info.fingo.urlopia.config.authentication.oauth.OAuthUserIdInterceptor
import info.fingo.urlopia.user.User
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate
import java.time.LocalTime

class PresenceConfirmationControllerV2Spec extends Specification {
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def presenceConfirmationControllerV2 = new PresenceConfirmationControllerV2(presenceConfirmationService)

    def authenticatedUserId = 1L
    def sampleDate = LocalDate.of(2021, 8, 10)
    def sampleStartTime = LocalTime.of(8, 0)
    def sampleEndTime = LocalTime.of(16, 0)

    def samplePresenceConfirmation(userId, LocalDate date) {
        def user = Mock(User) { getId() >> userId }
        return new PresenceConfirmation(user, date, sampleStartTime, sampleEndTime)
    }

    def "getPresenceConfirmations() WHEN user is authenticated SHOULD return presence confirmations"() {
        given: "an http request with authenticated user id"
        def httpRequest = Mock(HttpServletRequest) {
            getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE) >> authenticatedUserId
        }

        and: "any filters"
        def filters = [] as String[]

        and: "a presence confirmation service that returns user confirmations"
        def presenceConfirmations = [
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(1)),
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(2)),
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(3)),
        ]
        presenceConfirmationService.getPresenceConfirmations(authenticatedUserId, filters) >> presenceConfirmations

        when: "user tries to get presence confirmations"
        def dtos = presenceConfirmationControllerV2.getPresenceConfirmations(filters, httpRequest)

        then: "user presence confirmations are returned"
        def expectedDtos = presenceConfirmations.stream()
                .map(PresenceConfirmationInputOutput::from)
                .toList()
        dtos == expectedDtos
    }

    def "savePresenceConfirmation() WHEN user is authenticated and dto is valid SHOULD return saved presence confirmation"() {
        given: "an http request with authenticated user id"
        def httpRequest = Mock(HttpServletRequest) {
            getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE) >> authenticatedUserId
        }

        and: "a valid dto"
        def inputDto = new PresenceConfirmationInputOutput()
        inputDto.setUserId(authenticatedUserId)
        inputDto.setDate(sampleDate)
        inputDto.setStartTime(sampleStartTime)
        inputDto.setEndTime(sampleEndTime)

        and: "a presence confirmation service that returns added confirmation"
        presenceConfirmationService.confirmPresence(_ as Long, _ as PresenceConfirmationInputOutput) >> {
            Long userId, PresenceConfirmationInputOutput dto -> {
                def user = Mock(User) { getId() >> authenticatedUserId }
                return new PresenceConfirmation(user, dto.getDate(), dto.getStartTime(), dto.getEndTime())
            }
        }

        when: "confirmation is successful"
        def dto = presenceConfirmationControllerV2.savePresenceConfirmation(inputDto, httpRequest)

        then: "saved presence is returned"
        dto.getUserId() == authenticatedUserId
        dto.getDate() == sampleDate
        dto.getStartTime() == sampleStartTime
        dto.getEndTime() == sampleEndTime
    }
}

package info.fingo.urlopia.api.v2.presence

import info.fingo.urlopia.config.authentication.AuthInterceptor
import info.fingo.urlopia.user.User
import org.springframework.http.HttpStatus
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

    def "savePresenceConfirmation() WHEN user is authenticated and dto is valid SHOULD return saved presence confirmation"() {
        given: "an http request with authenticated user id"
        def httpRequest = Mock(HttpServletRequest) {
            getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE) >> authenticatedUserId
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
        def response = presenceConfirmationControllerV2.savePresenceConfirmation(inputDto, httpRequest)

        then: "a response with added presence confirmation is returned"
        response.statusCode == HttpStatus.CREATED
        response.body == inputDto
    }
}

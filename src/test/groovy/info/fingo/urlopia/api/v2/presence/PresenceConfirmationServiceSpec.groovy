package info.fingo.urlopia.api.v2.presence

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class PresenceConfirmationServiceSpec extends Specification {
    def presenceConfirmationRepository = Mock(PresenceConfirmationRepository) {
        save(_ as PresenceConfirmation) >> { PresenceConfirmation pc -> pc }
    }
    def requestService = Mock(RequestService)
    def holidayService = Mock(HolidayService)
    def userService = Mock(UserService)
    def presenceConfirmationService = new PresenceConfirmationService(presenceConfirmationRepository, requestService, holidayService, userService)

    def authenticatedUserId = 1L
    def userWhomPresenceIsAddedId = 999L
    def sampleDate = LocalDate.of(2021, 8, 10)
    def sampleStartTime = LocalTime.of(8, 0)
    def sampleEndTime = LocalTime.of(16, 0)

    def samplePresenceConfirmationDTO(userId) {
        def dto = new PresenceConfirmationInputOutput()
        dto.setDate(sampleDate)
        dto.setStartTime(sampleStartTime)
        dto.setEndTime(sampleEndTime)
        dto.setUserId(userId)
        return dto
    }

    def matchesSampleValues(presenceConfirmation) {
        presenceConfirmation.getDate() == sampleDate
        presenceConfirmation.getStartTime() == sampleStartTime
        presenceConfirmation.getEndTime() == sampleEndTime
    }

    def samplePresenceConfirmation(userId, LocalDate date) {
        def user = Mock(User) { getId() >> userId }
        return new PresenceConfirmation(user, date, sampleStartTime, sampleEndTime)
    }

    def "getPresenceConfirmations() WHEN user is not admin SHOULD return only authenticated user confirmations"() {
        given: "any filters"
        def filters = [] as String[]

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a presence confirmation repository that returns user confirmations"
        def userPresenceConfirmations = [
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(1)),
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(2))
        ]
        1 * presenceConfirmationRepository.findAll(_ as Filter) >> userPresenceConfirmations

        when: "user tries to get his confirmations"
        def confirmations = presenceConfirmationService.getPresenceConfirmations(authenticatedUserId, filters)

        then: "his confirmations are returned"
        confirmations == userPresenceConfirmations
    }

    def "getPresenceConfirmations() WHEN user is admin SHOULD return confirmations of all users"() {
        given: "any filters"
        def filters = [] as String[]

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> true
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a presence confirmation repository that returns all user confirmations"
        def presenceConfirmationList = [
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(1)),
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(2)),
                samplePresenceConfirmation(2L, sampleDate.plusDays(1)),
                samplePresenceConfirmation(3L, sampleDate.plusDays(3))
        ]
        1 * presenceConfirmationRepository.findAll(_ as Filter) >> presenceConfirmationList

        when: "user tries to get his confirmations"
        def confirmations = presenceConfirmationService.getPresenceConfirmations(authenticatedUserId, filters)

        then: "his confirmations are returned"
        confirmations == presenceConfirmationList
    }

    def "confirmPresence() WHEN user is not admin and is confirming his own presence SHOULD add the presence"() {
        given: "a valid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a holiday service that tells that every day is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        and: "a request service that returns no requests"
        requestService.getByUserAndDate(authenticatedUserId, sampleDate) >> []

        when: "user tries to confirm his presence"
        def presenceConfirmation = presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "a correct presence confirmation is added"
        matchesSampleValues(presenceConfirmation)
        presenceConfirmation.getUserId() == authenticatedUserId
    }

    def "confirmPresence() WHEN user is not admin and is confirming presence of someone else SHOULD throw an exception"() {
        given: "any dto"
        def dto = samplePresenceConfirmationDTO(userWhomPresenceIsAddedId)

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        when: "user tries to confirm presence of someone else"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is thrown"
        thrown(PresenceConfirmationException)
    }

    def "confirmPresence() WHEN user is admin and is confirming his own presence SHOULD add the presence"() {
        given: "a valid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> true
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a holiday service that tells that every day is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        and: "a request service that returns no requests"
        requestService.getByUserAndDate(authenticatedUserId, sampleDate) >> []

        when: "user tries to confirm his presence"
        def presenceConfirmation = presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "a correct presence confirmation is added"
        matchesSampleValues(presenceConfirmation)
        presenceConfirmation.getUserId() == authenticatedUserId
    }

    def "confirmPresence() WHEN user is admin and is confirming presence of someone else SHOULD add the presence"() {
        given: "a valid dto"
        def dto = samplePresenceConfirmationDTO(userWhomPresenceIsAddedId)

        and: "a user service that returns proper users"
        def authenticatedUser = Mock(User) {
            isAdmin() >> true
            getId() >> authenticatedUserId
        }
        def userWhomPresenceIsAdded = Mock(User) {
            getId() >> userWhomPresenceIsAddedId
        }
        userService.get(authenticatedUserId) >> authenticatedUser
        userService.get(userWhomPresenceIsAddedId) >> userWhomPresenceIsAdded

        and: "a holiday service that tells that every day is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        and: "a request service that returns no requests"
        requestService.getByUserAndDate(userWhomPresenceIsAddedId, sampleDate) >> []

        when: "user tries to confirm presence of someone else"
        def presenceConfirmation = presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "a correct presence confirmation is added"
        matchesSampleValues(presenceConfirmation)
        presenceConfirmation.getUserId() == userWhomPresenceIsAddedId
    }

    def "confirmPresence() WHEN given day is non working day SHOULD throw an exception"() {
        given: "a valid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a holiday service that tells that every day is non working day"
        holidayService.isWorkingDay(_ as LocalDate) >> false

        and: "a request service that returns no requests"
        requestService.getByUserAndDate(authenticatedUserId, sampleDate) >> []

        when: "user tries to confirm his presence"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is thrown"
        thrown(PresenceConfirmationException)
    }

    def "confirmPresence() WHEN user is on vacation on a given day SHOULD throw an exception"() {
        given: "a valid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a holiday service that tells that every day is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        and: "a request service that returns some requests"
        requestService.getByUserAndDate(authenticatedUserId, sampleDate) >> [new Request()]

        when: "user tries to confirm his presence"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is thrown"
        thrown(PresenceConfirmationException)
    }
}
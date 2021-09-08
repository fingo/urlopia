package info.fingo.urlopia.api.v2.presence

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.HolidayService
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
    def sampleDate = LocalDate.now()
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


    def "getByUserAndDate() WHEN called SHOULD return only filtered authenticated user confirmations"() {
        given: "a presence confirmation repository that returns user confirmations"
        def userPresenceConfirmations = [
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(1)),
                samplePresenceConfirmation(authenticatedUserId, sampleDate.plusDays(2))
        ]
        1 * presenceConfirmationRepository.findAll(_ as Filter) >> userPresenceConfirmations

        and: "userId and day from what we want get confirmations "
        def userId = 5

        when:
        def confirmations = presenceConfirmationService.getByUserAndDate(userId, sampleDate)

        then:
        confirmations == userPresenceConfirmations
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
        thrown(ForbiddenConfirmationException)
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

        and: "a request service that tells that user is vacationing"
        requestService.isVacationing(authenticatedUser, dto.getDate()) >> true

        when: "user tries to confirm his presence"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is thrown"
        thrown(PresenceConfirmationException)
    }

    def "confirmPresence() WHEN date is in the future SHOULD throw an exception"() {
        given: "an invalid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)
        dto.setDate(LocalDate.now().plusDays(30))

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        when: "user tries to confirm his presence"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is thrown"
        thrown(PresenceConfirmationException)
    }

    def "confirmPresence() WHEN date is in the past and authenticated user is not an admin SHOULD throw an exception"() {
        given: "an invalid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)
        dto.setDate(LocalDate.now().minusWeeks(2).minusDays(1))

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> false
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        when: "user tries to confirm his presence"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is thrown"
        thrown(PresenceConfirmationException)
    }

    def "confirmPresence() WHEN date is in the past and authenticated user is an admin SHOULD not throw an exception"() {
        given: "an invalid dto"
        def dto = samplePresenceConfirmationDTO(authenticatedUserId)
        dto.setDate(LocalDate.now().minusWeeks(2).minusDays(1))

        and: "a user service that returns authenticated user"
        def authenticatedUser = Mock(User) {
            isAdmin() >> true
            getId() >> authenticatedUserId
        }
        userService.get(authenticatedUserId) >> authenticatedUser

        and: "a holiday service that tells that every day is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        and: "a request service that tells that user is not vacationing"
        requestService.isVacationing(authenticatedUser, dto.getDate()) >> false

        when: "user tries to confirm his presence"
        presenceConfirmationService.confirmPresence(authenticatedUserId, dto)

        then: "an exception is not thrown"
        notThrown(PresenceConfirmationException)
    }

    def "deletePresenceConfirmations() SHOULD delete presence confirmation in a given date range"() {
        given: "sample arguments"
        def startDate = LocalDate.of(2021, 3, 4)
        def endDate = LocalDate.of(2021, 3, 5)

        and: "presence confirmation repository that returns filtered confirmations"
        def userPresenceConfirmations = [
                samplePresenceConfirmation(authenticatedUserId, startDate),
                samplePresenceConfirmation(authenticatedUserId, endDate)
        ]
        1 * presenceConfirmationRepository.findAll(_ as Filter) >> userPresenceConfirmations

        when: "user tries to delete presence confirmations"
        presenceConfirmationService.deletePresenceConfirmations(authenticatedUserId, startDate, endDate)

        then: "the presence confirmation are deleted"
        1 * presenceConfirmationRepository.deleteAll(userPresenceConfirmations)
    }

    def "getPresenceConfirmation() WHEN presence confirmation exists SHOULD return optional of it"() {
        given:
        def userId = 1L
        def date = LocalDate.now()
        def presenceConfirmation = samplePresenceConfirmation(userId, date)

        presenceConfirmationRepository.findAll(_ as Filter) >> [presenceConfirmation]

        def presenceConfirmationService = new PresenceConfirmationService(presenceConfirmationRepository, requestService, holidayService, userService)

        when:
        def output = presenceConfirmationService.getPresenceConfirmation(userId, date)

        then:
        output == Optional.of(presenceConfirmation)
    }

    def "getPresenceConfirmation() WHEN does not exist SHOULD return empty optional"() {
        given:
        def userId = 1L
        def date = LocalDate.now()

        presenceConfirmationRepository.findAll(_ as Filter) >> []

        def presenceConfirmationService = new PresenceConfirmationService(presenceConfirmationRepository, requestService, holidayService, userService)

        when:
        def output = presenceConfirmationService.getPresenceConfirmation(userId, date)

        then:
        output == Optional.empty()
    }
}

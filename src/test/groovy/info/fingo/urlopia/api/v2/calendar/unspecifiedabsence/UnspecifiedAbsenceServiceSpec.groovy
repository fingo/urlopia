package info.fingo.urlopia.api.v2.calendar.unspecifiedabsence

import info.fingo.urlopia.api.v2.preferences.UserPreferencesService
import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreference
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.Holiday
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.LocalDate

class UnspecifiedAbsenceServiceSpec extends Specification {
    def requestService = Mock(RequestService)
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def userService = Mock(UserService)
    def holidayService = Spy(HolidayService)
    def userPreferencesService = Mock(UserPreferencesService)
    def unspecifiedAbsenceService = new UnspecifiedAbsenceService(requestService,
                                                                  presenceConfirmationService,
                                                                  userService,
                                                                  holidayService,
                                                                  userPreferencesService)

    def TODAY = LocalDate.now()

    def createSampleUser(userId) {
        return Mock(User) {
            getId() >> userId
            getB2b() >> false
        }
    }

    def createSampleRequest(user, startDate, endDate) {
        return Mock(Request) {
            getRequester() >> user
            getStartDate() >> startDate
            getEndDate() >> endDate
        }
    }

    def createSamplePresenceConfirmation(userId, date) {
        return Mock(PresenceConfirmation) {
            getUserId() >> userId
            getDate() >> date
        }
    }

    def "getEmployeesWithUnspecifiedAbsences() WHEN user doesn't have any presence confirmation SHOULD not include him in response"() {
        given:
        def sampleUsers = [createSampleUser(1L)]

        and:
        userService.get(_ as Filter) >> sampleUsers
        presenceConfirmationService.getAll(_ as Filter) >> []
        requestService.getAll(_ as Filter) >> []
        holidayService.getAll(_ as Filter) >> []

        and:
        presenceConfirmationService.getFirstUserConfirmationFromStartDate(_ as Long, _ as LocalDate) >> Optional.empty()

        when:
        def result = unspecifiedAbsenceService.getEmployeesWithUnspecifiedAbsences(false)

        then:
        !result.users().containsKey(1L)
    }

    def "getEmployeesWithUnspecifiedAbsences() WHEN user has presence confirmations SHOULD check all dates after earliest confirmation"() {
        given:
        def sampleUsers = [createSampleUser(1L)]

        and:
        userService.get(_ as Filter) >> sampleUsers
        requestService.getAll(_ as Filter) >> []
        holidayService.getAll(_ as Filter) >> []

        and:
        def samplePresenceConfirmations = [
                createSamplePresenceConfirmation(sampleUsers[0].getId(), TODAY.minusDays(15))
        ]
        presenceConfirmationService.getAll(_ as Filter) >> samplePresenceConfirmations

        and:
        presenceConfirmationService.getFirstUserConfirmationFromStartDate(_ as Long, _ as LocalDate) >> Optional.of(samplePresenceConfirmations[0])

        and:
        userPreferencesService.getWorkingHoursPreferenceOf(1L) >> UserWorkingHoursPreference.getDefault(1L)

        when:
        def result = unspecifiedAbsenceService.getEmployeesWithUnspecifiedAbsences(false)

        def startDate = samplePresenceConfirmations[0].getDate().plusDays(1)
        def expectedDays = workingDaysRange(startDate, TODAY)

        then:
        result.users().containsKey(1L)
        result.users().get(1L) == expectedDays
    }

    def "getEmployeesWithUnspecifiedAbsences() WHEN user has presence confirmations SHOULD not take holidays into account"() {
        given:
        def sampleUsers = [createSampleUser(1L)]

        and:
        userService.get(_ as Filter) >> sampleUsers
        requestService.getAll(_ as Filter) >> []

        and:
        def sampleHolidays = [
                Mock(Holiday) { getDate() >> TODAY.minusDays(5)},
                Mock(Holiday) { getDate() >> TODAY.minusDays(2)},
        ]
        holidayService.getAll(_ as Filter) >> sampleHolidays

        and:
        def samplePresenceConfirmations = [
                createSamplePresenceConfirmation(sampleUsers[0].getId(), TODAY.minusDays(8))
        ]
        presenceConfirmationService.getAll(_ as Filter) >> samplePresenceConfirmations

        and:
        presenceConfirmationService.getFirstUserConfirmationFromStartDate(_ as Long, _ as LocalDate) >> Optional.of(samplePresenceConfirmations[0])

        and:
        userPreferencesService.getWorkingHoursPreferenceOf(1L) >> UserWorkingHoursPreference.getDefault(1L)

        when:
        def result = unspecifiedAbsenceService.getEmployeesWithUnspecifiedAbsences(false)

        def startDate = samplePresenceConfirmations[0].getDate().plusDays(1)
        def expectedDays = workingDaysRange(startDate, TODAY).stream()
                .filter(date -> date != sampleHolidays[0].getDate())
                .filter(date -> date != sampleHolidays[1].getDate())
                .toList()

        then:
        result.users().containsKey(1L)
        result.users().get(1L) == expectedDays
    }

    def "getEmployeesWithUnspecifiedAbsences() WHEN given users with different use cases SHOULD return correct result"() {
        given:
        def sampleUsers = [
                createSampleUser(1L), // User with no presence confirmations
                createSampleUser(2L), // User who has presence confirmation but is on vacation since then
                createSampleUser(3L)  // User with unspecified absences
        ]

        and:
        userService.get(_ as Filter) >> sampleUsers

        and:
        requestService.getAll(_ as Filter) >> [
                createSampleRequest(sampleUsers[1], TODAY.minusWeeks(1), TODAY.plusDays(2)),
                createSampleRequest(sampleUsers[2], TODAY.minusDays(11), TODAY.minusDays(9))
        ]

        and:
        def samplePresenceConfirmations = [
                createSamplePresenceConfirmation(sampleUsers[1].getId(), TODAY.minusWeeks(1).minusDays(1)),
                createSamplePresenceConfirmation(sampleUsers[2].getId(), TODAY.minusDays(12)),
                createSamplePresenceConfirmation(sampleUsers[2].getId(), TODAY.minusDays(8)),
                createSamplePresenceConfirmation(sampleUsers[2].getId(), TODAY.minusDays(5)),
                createSamplePresenceConfirmation(sampleUsers[2].getId(), TODAY.minusDays(1)),
        ]
        presenceConfirmationService.getAll(_ as Filter) >> samplePresenceConfirmations

        and:
        def sampleHolidays = [
                Mock(Holiday) {getDate() >> TODAY.minusDays(7)},
                Mock(Holiday) {getDate() >> TODAY.minusDays(3)},
        ]
        holidayService.getAll(_ as Filter) >> sampleHolidays

        and:
        presenceConfirmationService.getFirstUserConfirmationFromStartDate(1L, _ as LocalDate) >> Optional.empty()
        presenceConfirmationService.getFirstUserConfirmationFromStartDate(2L, _ as LocalDate) >> Optional.of(samplePresenceConfirmations[0])
        presenceConfirmationService.getFirstUserConfirmationFromStartDate(3L, _ as LocalDate) >> Optional.of(samplePresenceConfirmations[1])


        and:
        userPreferencesService.getWorkingHoursPreferenceOf(1L) >> UserWorkingHoursPreference.getDefault(1L)
        userPreferencesService.getWorkingHoursPreferenceOf(2L) >> UserWorkingHoursPreference.getDefault(2L)
        userPreferencesService.getWorkingHoursPreferenceOf(3L) >> UserWorkingHoursPreference.getDefault(3L)

        when:
        def result = unspecifiedAbsenceService.getEmployeesWithUnspecifiedAbsences(false)
        def expectedDays = [TODAY.minusDays(6), TODAY.minusDays(4), TODAY.minusDays(2)].stream()
                .filter(date -> !holidayService.isWeekend(date))
                .toList()

        then:
        !result.users().containsKey(1L)
        !result.users().containsKey(2L)
        result.users().containsKey(3L)
        result.users().get(3L) == expectedDays
    }

    def workingDaysRange(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return startDateInclusive.datesUntil(endDateExclusive)
                .filter(date -> !holidayService.isWeekend(date))
                .toList()
    }
}

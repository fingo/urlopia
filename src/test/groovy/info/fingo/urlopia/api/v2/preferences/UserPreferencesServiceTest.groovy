package info.fingo.urlopia.api.v2.preferences

import info.fingo.urlopia.api.v2.preferences.working.hours.SingleDayHourPreference
import info.fingo.urlopia.api.v2.preferences.working.hours.SingleDayHourPreferenceDTO
import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreference
import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreferenceDTO
import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreferenceRepository
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationRepository
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class UserPreferencesServiceTest extends Specification {
    def userWorkingHoursPreferenceRepository = Mock(UserWorkingHoursPreferenceRepository) {
        save(_ as UserWorkingHoursPreference) >> { UserWorkingHoursPreference pref -> pref }
    }
    def presenceConfirmationRepository = Mock(PresenceConfirmationRepository) {
        save(_ as PresenceConfirmation) >> { PresenceConfirmation pc -> pc }
    }
    def userService = Mock(UserService)
    def userPreferencesService = new UserPreferencesService(userWorkingHoursPreferenceRepository,
                                                            presenceConfirmationRepository,
                                                            userService)

    def userId = 1L
    def sampleUser = Mock(User) {
        getId() >> userId
        isAdmin() >> false
    }

    def adminId = 2L
    def sampleAdmin = Mock(User) {
        getId() >> adminId
        isAdmin() >> true
    }

    def "getWorkingHoursPreference() WHEN user is not admin and preference is not present SHOULD generate default one"() {
        given:
        userService.get(userId) >> sampleUser

        and:
        def defaultPreference = UserWorkingHoursPreference.getDefault(userId)
        userWorkingHoursPreferenceRepository.existsById(userId) >> false
        userWorkingHoursPreferenceRepository.getById(userId) >> defaultPreference

        when:
        def result = userPreferencesService.getWorkingHoursPreference(userId)

        then:
        result.size() == 1
        result.containsKey(userId)
        result.get(userId) == defaultPreference
        1 * userWorkingHoursPreferenceRepository.save(_ as UserWorkingHoursPreference)
    }

    def "getWorkingHoursPreference() WHEN user is admin SHOULD return preferences of all users"() {
        given:
        userService.get(adminId) >> sampleAdmin

        and:
        def defaultPreference1 = UserWorkingHoursPreference.getDefault(11L)
        def defaultPreference2 = UserWorkingHoursPreference.getDefault(12L)
        def defaultPreference3 = UserWorkingHoursPreference.getDefault(13L)
        userWorkingHoursPreferenceRepository.findAll(_ as Filter) >> [
                defaultPreference1, defaultPreference2, defaultPreference3
        ]

        when:
        def result = userPreferencesService.getWorkingHoursPreference(adminId)

        then:
        result.size() == 3
        result.containsKey(11L)
        result.get(11L) == defaultPreference1
        result.containsKey(12L)
        result.get(12L) == defaultPreference2
        result.containsKey(13L)
        result.get(13L) == defaultPreference3
        0 * userWorkingHoursPreferenceRepository.save(_ as UserWorkingHoursPreference)
    }

    def "changeWorkingHoursPreference() WHEN preference was present SHOULD replace it with new one"() {
        given:
        def mondayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def tuesdayDto = sampleSingleDayDTO(true, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def wednesdayDto = sampleSingleDayDTO(true, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def thursdayDto = sampleSingleDayDTO(true, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def fridayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def sampleDto = new UserWorkingHoursPreferenceDTO()
        sampleDto.setDayPreferences([
                1: mondayDto,
                2: tuesdayDto,
                3: wednesdayDto,
                4: thursdayDto,
                5: fridayDto
        ])

        and:
        def samplePreference = Spy(UserWorkingHoursPreference.getDefault(userId))
        samplePreference.getChanged() >> LocalDateTime.now()
        userWorkingHoursPreferenceRepository.findById(userId) >> Optional.of(samplePreference)

        when:
        def result = userPreferencesService.changeWorkingHoursPreference(userId, sampleDto)

        then:
        result.getUserId() == userId
        result.getDayPreferences().size() == 5
        result.getDayPreferences().get(1) == mondayDto
        result.getDayPreferences().get(2) == tuesdayDto
        result.getDayPreferences().get(3) == wednesdayDto
        result.getDayPreferences().get(4) == thursdayDto
        result.getDayPreferences().get(5) == fridayDto
    }

    def "changeWorkingHoursPreference() WHEN preference was not present SHOULD create new one"() {
        given:
        def mondayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def tuesdayDto = sampleSingleDayDTO(true, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def wednesdayDto = sampleSingleDayDTO(true, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def thursdayDto = sampleSingleDayDTO(true, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def fridayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def sampleDto = new UserWorkingHoursPreferenceDTO()
        sampleDto.setDayPreferences([
                1: mondayDto,
                2: tuesdayDto,
                3: wednesdayDto,
                4: thursdayDto,
                5: fridayDto
        ])

        and:
        userWorkingHoursPreferenceRepository.findById(userId) >> Optional.empty()

        when:
        def result = userPreferencesService.changeWorkingHoursPreference(userId, sampleDto)

        then:
        result.getUserId() == userId
        result.getDayPreferences().size() == 5
        result.getDayPreferences().get(1) == mondayDto
        result.getDayPreferences().get(2) == tuesdayDto
        result.getDayPreferences().get(3) == wednesdayDto
        result.getDayPreferences().get(4) == thursdayDto
        result.getDayPreferences().get(5) == fridayDto
    }

    def "changeWorkingHoursPreference() WHEN preference was present SHOULD fix presence confirmations"() {
        given: "previous preference"
        def previousPreference = Spy(UserWorkingHoursPreference.getDefault(userId))
        previousPreference.getChanged() >> LocalDateTime.now().minusWeeks(5)
        def mondayPref = previousPreference.getDayPreferenceBy(DayOfWeek.MONDAY).get()
        mondayPref.setNonWorking(true)
        def tuesdayPref = previousPreference.getDayPreferenceBy(DayOfWeek.TUESDAY).get()
        tuesdayPref.setNonWorking(true)
        userWorkingHoursPreferenceRepository.findById(userId) >> Optional.of(previousPreference)

        and: "new preference"
        def mondayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def tuesdayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def wednesdayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def thursdayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def fridayDto = sampleSingleDayDTO(false, LocalTime.of(8, 0), LocalTime.of(16, 0))
        def sampleDto = new UserWorkingHoursPreferenceDTO()
        sampleDto.setDayPreferences([
                1: mondayDto,
                2: tuesdayDto,
                3: wednesdayDto,
                4: thursdayDto,
                5: fridayDto
        ])

        and:
        userService.get(userId) >> sampleUser
        userWorkingHoursPreferenceRepository.findById(userId) >> Optional.of(previousPreference)

        when:
        userPreferencesService.changeWorkingHoursPreference(userId, sampleDto)

        then:
        10 * presenceConfirmationRepository.save(_ as PresenceConfirmation)
    }

    def static sampleSingleDayDTO(boolean nonWorking, LocalTime startTime, LocalTime endTime) {
        var dto = new SingleDayHourPreferenceDTO()
        dto.setNonWorking(nonWorking)
        dto.setStartTime(startTime)
        dto.setEndTime(endTime)
        return dto
    }
}

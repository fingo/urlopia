package info.fingo.urlopia.api.v2.calendar

import info.fingo.urlopia.api.v2.calendar.unspecifiedabsence.UnspecifiedAbsenceService
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.holidays.Holiday
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.team.Team
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class CalendarServiceSpec extends Specification {
    def userId = 1L
    def user = Mock(User) {
        getId() >> userId
    }

    def filter = Filter.from("")

    def holidayService = Mock(HolidayService)
    def requestService = Mock(RequestService)
    def userService = Mock(UserService)
    def presenceConfirmationService = Mock(PresenceConfirmationService)

    def unspecifiedAbsenceService = new UnspecifiedAbsenceService(requestService, presenceConfirmationService, userService, holidayService)
    def calendarOutputProvider = new CalendarOutputProvider(holidayService, userService, presenceConfirmationService, unspecifiedAbsenceService)
    def calendarService = new CalendarService(calendarOutputProvider)

    def "getCalendarInfo() WHEN normal day SHOULD return calendar output"() {
        given:
        def startDate = LocalDate.of(2021, 8, 26)
        def endDate = startDate
        def startTime = LocalTime.of(8, 0)
        def endTime = startTime.plusHours(8)

        and: "Expected absent user output"
        def absentUser = Mock(User) {
            getId() >> 2L
            getFullName() >> "Jan Kowalski"
            getTeams() >> [Mock(Team) {
                getName() >> "ZPH"
            }]
        }

        and: "Expected current user information"
        def presenceConfirmation = Mock(PresenceConfirmation) {
            getDate() >> startDate
            getStartTime() >> startTime
            getEndTime() >> endTime
        }
        def presenceConfirmationOutput = new PresenceConfirmationOutput(true, startTime, endTime)
        def vacationHoursModifications = []

        def currentUserInformation = new CurrentUserInformationOutput()
        currentUserInformation.setAbsent(false)
        currentUserInformation.setPresenceConfirmation(presenceConfirmationOutput)
        currentUserInformation.setVacationHoursModifications(vacationHoursModifications)

        and: "Expected single day output"
        def singleDayOutput = new SingleDayOutput()
        singleDayOutput.setWorkingDay(true)
        singleDayOutput.setHolidays([])
        singleDayOutput.setAbsentUsers([AbsentUserOutput.of(absentUser)])
        singleDayOutput.setCurrentUserInformation(currentUserInformation)

        and: "Expected calendar output"
        def expectedCalendarMap = new HashMap<LocalDate, SingleDayOutput>()
        expectedCalendarMap.put(startDate, singleDayOutput)
        def expectedCalendarOutput = new CalendarOutput(expectedCalendarMap)

        and: "Working services"
        userService.get(userId) >> user
        userService.get(_ as Filter) >> [user, absentUser]
        holidayService.getAll(_ as Filter) >> []
        requestService.getAll(_ as Filter) >> [new Request(absentUser, startDate, endDate, 1, RequestType.NORMAL, null, Request.Status.ACCEPTED)]
        presenceConfirmationService.getAll(_ as Filter) >> [presenceConfirmation]

        when:
        def output = calendarService.getCalendarInfo(userId, startDate, endDate, filter)

        then:
        output == expectedCalendarOutput
    }

    def "getCalendarInfo() WHEN holidays day SHOULD return calendar output"() {
        given:
        def startDate = LocalDate.of(2021, 12, 25)
        def endDate = LocalDate.of(2021, 12, 26)

        and: "Expected holidays"
        def holidayName = "Boże narodzenie"
        def holiday = Mock(Holiday) {
            getName() >> holidayName
            getDate() >> startDate
        }
        def holidays = [holiday]

        and: "Expected current user information"
        def presenceConfirmationOutput1 = new PresenceConfirmationOutput(true, null, null)
        def currentUserInformation1 = new CurrentUserInformationOutput()
        currentUserInformation1.setAbsent(false)
        currentUserInformation1.setPresenceConfirmation(presenceConfirmationOutput1)
        currentUserInformation1.setVacationHoursModifications([])

        def singleDayOutput1 = new SingleDayOutput()
        singleDayOutput1.setWorkingDay(false)
        singleDayOutput1.setAbsentUsers([])
        singleDayOutput1.setHolidays([holidayName])
        singleDayOutput1.setCurrentUserInformation(currentUserInformation1)

        def presenceConfirmationOutput2 = new PresenceConfirmationOutput(false, null, null)
        def currentUserInformation2 = new CurrentUserInformationOutput()
        currentUserInformation2.setAbsent(false)
        currentUserInformation2.setPresenceConfirmation(presenceConfirmationOutput2)
        currentUserInformation2.setVacationHoursModifications([])

        def singleDayOutput2 = new SingleDayOutput()
        singleDayOutput2.setWorkingDay(false)
        singleDayOutput2.setAbsentUsers([])
        singleDayOutput2.setHolidays([])
        singleDayOutput2.setCurrentUserInformation(currentUserInformation2)

        and: "Expected calendar output"
        def expectedCalendarMap = new HashMap<LocalDate, SingleDayOutput>()
        expectedCalendarMap.put(startDate, singleDayOutput1)
        expectedCalendarMap.put(endDate, singleDayOutput2)
        def expectedCalendarOutput = new CalendarOutput(expectedCalendarMap)

        and: "Expected first user presence confirmation"
        def firstUserConfirmation = new PresenceConfirmation(user, startDate, null, null)

        and: "Working services"
        holidayService.getAll(_ as Filter) >> holidays
        userService.get(userId) >> user
        userService.get(_ as Filter) >> [user]
        requestService.getAll(_ as Filter) >> []
        presenceConfirmationService.getAll(_ as Filter) >> [firstUserConfirmation]

        when:
        def output = calendarService.getCalendarInfo(userId, startDate, endDate, filter)

        then:
        output == expectedCalendarOutput
    }
}

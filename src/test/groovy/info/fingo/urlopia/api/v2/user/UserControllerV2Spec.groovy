package info.fingo.urlopia.api.v2.user

import info.fingo.urlopia.api.v2.automatic.vacation.days.AutomaticVacationDayService
import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDay
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.history.HistoryLogInput
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.history.WorkTimeResponse
import info.fingo.urlopia.request.normal.NormalRequestService
import info.fingo.urlopia.team.TeamExcerptProjection
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserExcerptProjection
import info.fingo.urlopia.user.UserService
import org.springframework.data.domain.Sort
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class UserControllerV2Spec extends Specification{
    private UserService userService
    private NormalRequestService normalRequestService;
    private HistoryLogService historyLogService;
    private AutomaticVacationDayService automaticVacationDayService
    private UserControllerV2 userControllerV2
    private List<UserExcerptProjection> usersData
    private List<UserOutput> userOutputs
    private List<String> listName

    void setup(){
        userService = Mock(UserService)
        normalRequestService = Mock(NormalRequestService)
        historyLogService = Mock(HistoryLogService)
        automaticVacationDayService = Mock(AutomaticVacationDayService)
        userControllerV2 = new UserControllerV2(userService,normalRequestService,historyLogService,automaticVacationDayService)

        def team1Name = "team1"
        listName = List.of(team1Name)
        TeamExcerptProjection teamExcerptProjection = Mock(TeamExcerptProjection)
        teamExcerptProjection.getName() >> team1Name
        Set<TeamExcerptProjection> firstTeams = new HashSet<TeamExcerptProjection>()
        firstTeams.add(teamExcerptProjection)
        def firstFullName = "John Smith"
        def firstId = 5L
        def firstMail = "mail@some_domain"
        def firstWorkTime = 7.0
        def firstWorkTimeOutput = new WorkTimeOutput(7, 8)
        def firstUserExcerptProjection = Mock(UserExcerptProjection){
            getName() >> firstFullName
            getId() >> firstId
            getMail() >> firstMail
            getTeams() >> firstTeams
            getWorkTime() >> firstWorkTime
        }

        def secondTeams = new HashSet<TeamExcerptProjection>()
        secondTeams.add(teamExcerptProjection)
        def secondFullName = "Mary Brown"
        def secondId = 6L
        def secondMail = "m@some_domain"
        def secondWorkTime = 6.0
        def secondWorkTimeOutput = new WorkTimeOutput(3, 4)
        def secondUserExcerptProjection = Mock(UserExcerptProjection){
            getName() >> secondFullName
            getId() >> secondId
            getMail() >> secondMail
            getTeams() >> secondTeams
            getWorkTime() >> secondWorkTime
        }

        usersData= List.of(firstUserExcerptProjection,secondUserExcerptProjection)

        def firstUserOutput = new UserOutput(firstFullName,
                                             firstId,
                                             firstMail,
                                             List.of(team1Name),
                                             firstWorkTime,
                                             firstWorkTimeOutput)
        def secondUserOutput = new UserOutput(secondFullName,
                                              secondId,
                                              secondMail,
                                              List.of(team1Name),
                                              secondWorkTime,
                                              secondWorkTimeOutput)
        userOutputs = List.of(firstUserOutput,secondUserOutput)
    }

    def "getAll() WHEN called with any Filter and any Sort SHOULD called service one time and map returned data"(){
        given:
        userService.get(_ as Filter,_ as Sort) >> usersData


        when:
        def result = userControllerV2.getAll(_ as String[],_ as Sort)

        then:
        result.containsAll(userOutputs)
    }

    def "getPendingDays() WHEN called SHOULD called service with id taken from request attribute"(){
        given: "authId with mocked request"
        def authId = 5L
        var httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> authId
        }
        and: "valid DayHourTime object"
        def days = 3
        def hours = 3
        def dayHour = new PendingDaysOutput(days, hours)
        normalRequestService.getPendingRequestsTimeV2(authId) >> dayHour


        when:
        def result = userControllerV2.getPendingDays(httpRequest)

        then:
        result.pendingHours() == hours
        result.pendingDays() == days

    }

    def "getRemainingDays() WHEN called SHOULD called service with id taken from request attribute"(){
        given: "authId with mocked request"
        def authId = 5L
        var httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> authId
        }
        and: "valid WorkTimeResponse mock"
        def days = 3
        def hours = 3
        def workTime = 8
        def workTimeResponse = Mock(WorkTimeResponse){
            getDays() >> days
            getHours() >> hours
            getWorkTime() >> workTime
        }
        and: "valid VacationDaysOutput mapped from WorkTimeResponse"
        def vacationDaysOutput = new VacationDaysOutput(days, hours, workTime)
        1 * historyLogService.countRemainingDays(authId) >> workTimeResponse

        when:
        def result = userControllerV2.getRemainingDays(httpRequest)
        then:
        result == vacationDaysOutput
    }

    def "setWorkTime() WHEN called SHOULD called service and return saved value"(){
        given: "userID"
        def userId = 5

        and: "valid WorkTimeInput object"
        def numerator  = 7
        def denominator = 8
        def workTimeInputValue = numerator+"/"+denominator
        def workTimeInput = new WorkTimeInput(workTimeInputValue)

        and: "valid WorkTimeResponse mock"
        def days = 3
        def hours = 3
        def workTimeResponse = Mock(WorkTimeResponse){
            getDays() >> days
            getHours() >> hours
        }
        historyLogService.countRemainingDays(userId) >> workTimeResponse

        when:
        userControllerV2.setWorkTime(userId,workTimeInput)
        then:
        1 * userService.setWorkTime(userId,workTimeInputValue)
    }

    def "getWorkTime() WHEN called SHOULD called service and return saved value"(){
        given: "userID"
        def userId = 5

        and: "valid WorkTimeResponse mock"
        def numerator  = 7
        def denominator = 8
        def workTimeResponse = Mock(WorkTimeResponse){
            getWorkTimeA() >> numerator
            getWorkTimeB() >> denominator
        }

        and: "valid WorkTimeOutput mapped from WorkTimeResponse"
        def workTimeOutput = new WorkTimeOutput(numerator,denominator)
        1 * historyLogService.countRemainingDays(userId) >> workTimeResponse

        when:
        def result = userControllerV2.getWorkTime(userId)
        then:
        result == workTimeOutput
    }
    def "getVacationDays() WHEN called SHOULD call service and return saved value"() {
        given: "userID"
        def userId = 5

        and: "valid WorkTimeResponse mock"
        def days = 3
        def hours = 3d
        def workTime = 8
        def workTimeResponse = Mock(WorkTimeResponse){
            getDays() >> days
            getHours() >> hours
            getWorkTime() >> workTime
        }

        and: "valid VacationDaysOutput mapped from WorkTimeResponse"
        def vacationDaysOutput = new VacationDaysOutput(days, hours, workTime)
        1 * historyLogService.countRemainingDays(userId) >> workTimeResponse;

        when:
        def result = userControllerV2.getVacationDays(userId)

        then:
        result == vacationDaysOutput
    }

    def "getAutomaticVacationDays() WHEN called SHOULD call service and return saved value"() {
        given: "authId with mocked request"
        def authId = 5L
        var httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> authId
        }

        and: "valid AutomaticVacationDay mock"
        def hours = 160
        def days = 20
        def user = Mock(User){
            getEc() >> false
        }
        def automaticVacationDayResponse = Mock(AutomaticVacationDay){
            getNextYearHoursProposition() >> hours
            getNextYearDaysBase() >> days
            getUser() >> user
        }
        automaticVacationDayService.getAutomaticVacationDayFor(_ as Long) >> automaticVacationDayResponse

        and: "valid automaticVacationDaysOutput mapped from AutomaticVacationDay"
        def automaticVacationDaysOutput = new AutomaticVacationDayOutput(hours, days, false)

        when:
        def result = userControllerV2.getAutomaticVacationDays(httpRequest)

        then:
        result == automaticVacationDaysOutput
    }

    def "addVacationHours() WHEN called SHOULD called service and return updated value"() {
        given: "authId with mocked request"
        def authId = 5L
        def httpRequest = Mock(HttpServletRequest){
            getAttribute(_ as String) >> authId
        }

        and: "HistoryLogInput Mock"
        def historyLogInput = Mock(HistoryLogInput)

        and: "valid WorkTimeResponse mock"
        def days = 3
        def hours = 3d
        def workTimeResponse = Mock(WorkTimeResponse){
            getDays() >> days
            getHours() >> hours
        }

        and: "userId"
        def userId = 7L;

        historyLogService.countRemainingDays(userId) >> workTimeResponse

        when:
        userControllerV2.addVacationHours(userId, historyLogInput, httpRequest)

        then:
        1 * historyLogService.create(historyLogInput, userId, authId)
    }
}

package info.fingo.urlopia.api.v2.reports

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.api.v2.reports.attendance.MonthlyAttendanceListReportFactory
import info.fingo.urlopia.api.v2.reports.holidays.UserHolidaysReportFactory
import info.fingo.urlopia.api.v2.user.UserFilterFactory
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.history.HistoryLog
import info.fingo.urlopia.history.HistoryLogExcerptProjection
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.history.UserDetailsChangeEvent
import info.fingo.urlopia.reports.ReportTemplateLoader
import info.fingo.urlopia.reports.XlsxTemplateResolver
import info.fingo.urlopia.reports.evidence.EvidenceReportModel
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

class ReportServiceSpec extends Specification{

    private UserService userService
    private ReportTemplateLoader reportTemplateLoader
    private XlsxTemplateResolver xlsxTemplateResolver
    private EvidenceReportModelFactory evidenceReportModelFactory
    private MonthlyAttendanceListReportFactory monthlyPresenceReportFactory
    private UserHolidaysReportFactory userHolidaysReportFactory
    private ReportService reportService
    private RequestService requestService
    private PresenceConfirmationService presenceConfirmationService
    private UserFilterFactory userFilterFactory
    private HistoryLogService historyLogService

    def createSampleUser(userId, isActive, isB2B, isEC) {
        return Mock(User) {
            getId() >> userId
            isActive() >> isActive
            getB2b() >> isB2B
            getEc() >> isEC
        }
    }

    def activeECUser = createSampleUser(1, true, false, true)
    def inactiveECUser = createSampleUser(2, false, false, true)
    def activeB2BUser = createSampleUser(3, true, true, false)

    void setup(){
        def activeECUsersFilter = Mock(Filter)
        def inactiveECUsersFilter = Mock(Filter)
        def activeB2BUsersFilter  = Mock(Filter)

        userFilterFactory = Mock(){
            getActiveB2BUsersFilter() >> activeB2BUsersFilter
            getActiveECUsersFilter() >> activeECUsersFilter
            getInactiveECUsersFilter() >> inactiveECUsersFilter
        }
        userService = Mock(UserService){
            get(activeECUsersFilter) >> [activeECUser]
            get(inactiveECUsersFilter) >> [inactiveECUser]
            get(activeB2BUsersFilter) >> [activeB2BUser]
        }
        reportTemplateLoader = Mock(ReportTemplateLoader)
        xlsxTemplateResolver = Mock(XlsxTemplateResolver)
        evidenceReportModelFactory = Mock(EvidenceReportModelFactory)
        userHolidaysReportFactory = Mock(UserHolidaysReportFactory)
        requestService = Mock(RequestService)
        presenceConfirmationService = Mock(PresenceConfirmationService)
        historyLogService = Mock(HistoryLogService)

        reportService = new ReportService(userService,
                                          requestService,
                                          presenceConfirmationService,
                                          reportTemplateLoader,
                                          xlsxTemplateResolver,
                                          evidenceReportModelFactory,
                                          monthlyPresenceReportFactory,
                                          userHolidaysReportFactory,
                                          userFilterFactory,
                                          historyLogService)
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN called SHOULD return every active ec worker"(){
        given:
        def requiredUsers = [activeECUser]
        def undesirableUsers = [inactiveECUser, activeB2BUser]
        historyLogService.get(_ as Long, _ as YearMonth, _ as UserDetailsChangeEvent) >> []
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.containsAll(requiredUsers)
        !undesirableUsers.any({user -> result.contains(user)})

    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN inactive EC user has presence in given time period SHOULD also return him"(){
        given:
        def requiredUsers = [activeECUser, inactiveECUser]
        def undesirableUsers = [activeB2BUser]
        presenceConfirmationService.hasPresenceByUserAndDateInterval(_ as LocalDate, _ as LocalDate, inactiveECUser.getId()) >> true
        historyLogService.get(_ as Long, _ as YearMonth, _ as UserDetailsChangeEvent) >> []
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.containsAll(requiredUsers)
        !undesirableUsers.any({user -> result.contains(user)})
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN inactive EC user has leave in given time period SHOULD also be added"(){
        given:
        def requiredUsers = [activeECUser, inactiveECUser]
        def undesirableUsers = [activeB2BUser]
        requestService.hasAcceptedByDateIntervalAndUser(_ as LocalDate, _ as LocalDate, _ as Long) >> true
        historyLogService.get(_ as Long, _ as YearMonth, _ as UserDetailsChangeEvent) >> []
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.containsAll(requiredUsers)
        !undesirableUsers.any({user -> result.contains(user)})
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN user switches from EC to B2B SHOULD also be added"(){
        given:
        def requiredUsers = [activeECUser, activeB2BUser]
        def undesirableUsers = [inactiveECUser]
        presenceConfirmationService.hasPresenceByUserAndDateInterval(_ as LocalDate, _ as LocalDate, activeB2BUser.getId()) >> true
        historyLogService.get(_ as Long, _ as YearMonth, _ as UserDetailsChangeEvent) >> []
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.containsAll(requiredUsers)
        !undesirableUsers.any({user -> result.contains(user)})
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN user switches from EC to B2B in first day of month by event SHOULD not be added"(){
        given:
        def requiredUsers = [activeECUser]
        presenceConfirmationService.hasPresenceByUserAndDateInterval(_ as LocalDate, _ as LocalDate, activeB2BUser.getId()) >> false
        def mockedLog = Mock(HistoryLogExcerptProjection) {
            getCreated() >> LocalDateTime.of(LocalDate.of(2022,1,1), LocalTime.MIN)
        }
        historyLogService.get(_ as Long, _ as YearMonth, UserDetailsChangeEvent.USER_CHANGE_TO_B2B) >> [mockedLog]
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.containsAll(requiredUsers)
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN user switches from EC to B2B after first day of month by event SHOULD be added"(){
        given:
        def requiredUsers = [activeECUser, activeB2BUser]
        presenceConfirmationService.hasPresenceByUserAndDateInterval(_ as LocalDate, _ as LocalDate, activeB2BUser.getId()) >> false
        def mockedLog = Mock(HistoryLogExcerptProjection) {
            getCreated() >> LocalDateTime.of(LocalDate.of(2022,1,2), LocalTime.MIN)
        }
        historyLogService.get(_ as Long, _ as YearMonth, UserDetailsChangeEvent.USER_CHANGE_TO_B2B) >> [mockedLog]
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.containsAll(requiredUsers)
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN user has activation event after given month SHOULD be removed from list"(){
        given:
        historyLogService.get(_ as Long, _ as YearMonth, UserDetailsChangeEvent.USER_CHANGE_TO_B2B) >> []
        def logMock = Mock(HistoryLog){
            getUser() >> activeECUser
        }
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> [logMock]

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        !result.contains(activeECUser)
    }

    def "findEmployeesNeededToBeInAttendanceList() WHEN user not has activation event after given month SHOULD not be removed from list"(){
        given:
        historyLogService.get(_ as Long, _ as YearMonth, UserDetailsChangeEvent.USER_CHANGE_TO_B2B) >> []
        historyLogService.getBy(_ as YearMonth, UserDetailsChangeEvent.USER_ACTIVATED) >> []

        when:
        def result = reportService.findEmployeesNeededToBeInAttendanceList(1, 1)

        then:
        result.contains(activeECUser)
    }


    def "getWorkTimeEvidenceReportName() WHEN called with id and year SHOULD return formatted file name string"(){
        given: "valid EvidenceReportModel"

        Map<String, String> model = new HashMap<>()
        model.put("reportDate.year",reportDateYear)
        model.put("user.lastName",userLastName)
        model.put("user.firstName",userFirstName)
        def evidenceReportModel = new EvidenceReportModel(model)

        and: "mock for evidenceReportModelFactory that return our EvidenceReportModel"
        def user = Mock(User)
        def year = Integer.valueOf(reportDateYear)
        userService.get(_ as Long) >> user
        evidenceReportModelFactory.generateModelForFileName(user, year) >> evidenceReportModel


        and: "every valid string prefix and suffix"
        def prefix = "attachment; filename="
        def suffix =".xlsx"

        and: "prefix for generate part"
        def generatePrefix = "ewidencja_czasu_pracy_"

        and: "expected result"
        def builder = new StringBuilder()
        builder.append(prefix)
        builder.append(generatePrefix)
        builder.append(reportDateYear)
        builder.append("_")
        builder.append(userLastName)
        builder.append(userFirstName)
        builder.append(suffix)
        def expected = builder.toString()

        when:
        def userId = 5L
        def result = reportService.getWorkTimeEvidenceReportName(userId, year)

        then:
        result == expected

        where:
        reportDateYear | userLastName | userFirstName
        "2021"         | "Snow"       | "John"
        "2020"         | "Brown"      | "Mary"

    }
}

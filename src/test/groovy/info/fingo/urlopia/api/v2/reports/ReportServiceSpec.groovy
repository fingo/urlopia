package info.fingo.urlopia.api.v2.reports

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.api.v2.reports.attendance.MonthlyAttendanceListReportFactory
import info.fingo.urlopia.api.v2.reports.holidays.UserHolidaysReportFactory
import info.fingo.urlopia.reports.ReportTemplateLoader
import info.fingo.urlopia.reports.XlsxTemplateResolver
import info.fingo.urlopia.reports.evidence.EvidenceReportModel
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

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

    void setup(){
        userService = Mock(UserService)
        reportTemplateLoader = Mock(ReportTemplateLoader)
        xlsxTemplateResolver = Mock(XlsxTemplateResolver)
        evidenceReportModelFactory = Mock(EvidenceReportModelFactory)
        userHolidaysReportFactory = Mock(UserHolidaysReportFactory)
        requestService = Mock(RequestService)
        presenceConfirmationService = Mock(PresenceConfirmationService)
        reportService = new ReportService(userService,
                                          requestService,
                                          presenceConfirmationService,
                                          reportTemplateLoader,
                                          xlsxTemplateResolver,
                                          evidenceReportModelFactory,
                                          monthlyPresenceReportFactory,
                                          userHolidaysReportFactory)
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
        def builder = new StringBuilder();
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

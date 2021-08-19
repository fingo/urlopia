package info.fingo.urlopia.api.v2.reports

import info.fingo.urlopia.reports.ReportTemplateLoader
import info.fingo.urlopia.reports.XlsxTemplateResolver
import info.fingo.urlopia.reports.evidence.EvidenceReportModel
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

class ReportServiceSpec extends Specification{

    private UserService userService;
    private ReportTemplateLoader reportTemplateLoader;
    private XlsxTemplateResolver xlsxTemplateResolver;
    private EvidenceReportModelFactory evidenceReportModelFactory;
    private ReportService reportService

    void setup(){
        userService = Mock(UserService)
        reportTemplateLoader = Mock(ReportTemplateLoader)
        xlsxTemplateResolver = Mock(XlsxTemplateResolver)
        evidenceReportModelFactory = Mock(EvidenceReportModelFactory)
        reportService = new ReportService(userService,reportTemplateLoader,
                                            xlsxTemplateResolver,evidenceReportModelFactory)
    }

    def "getWorkTimeEvidenceReportName() WHEN called with id and year SHOULD return formatted file name string"(){
        given: "valid EvidenceReportModel"

        Map<String, String> model = new HashMap<>()
        model.put("reportDate.year",reportDateYear)
        model.put("user.lastName",userLastName)
        model.put("user.firstName",userFirstName)
        def evidenceReportModel = new EvidenceReportModel(model)

        and: "mock for evidenceReportModelFactory that return our EbidenceReportModel"
        def user = Mock(User)
        def year = Integer.valueOf(reportDateYear)
        userService.get(_ as Long) >> user
        evidenceReportModelFactory.create(user, year) >> evidenceReportModel


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
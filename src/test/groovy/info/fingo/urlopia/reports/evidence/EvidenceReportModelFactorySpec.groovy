package info.fingo.urlopia.reports.evidence

import info.fingo.urlopia.history.HistoryLog
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.time.LocalDate

class EvidenceReportModelFactorySpec extends Specification{
    private EvidenceReportModelFactory modelFactory;
    private RequestService requestService;
    private HolidayService holidayService;
    private HistoryLogService historyLogService;
    private EvidenceReportStatusFromRequestMapper statusFromRequestMapper;

    private User user;
    private int year = 2021
    private HistoryLog historyLog
    private exampleWorkingDay = "01_01"
    private exampleHolidayDay = "01_01"


    void setup(){
        requestService = Mock(RequestService)
        holidayService = Mock(HolidayService)
        historyLog = Mock(HistoryLog)
        historyLogService = Mock(HistoryLogService){
            countRemainingHoursForYear(1,year-1) >> 0.0f
            getFromYear(1,year) >> [historyLog]
        }
        statusFromRequestMapper = Mock(EvidenceReportStatusFromRequestMapper){
            getEvidenceReportStatusFromRequest(_ as Request) >> "status"
        }
        modelFactory = new EvidenceReportModelFactory(requestService,holidayService,
                                                        historyLogService,statusFromRequestMapper)
        user = Mock(User){
            getFirstName() >> "John"
            getLastName() >> "Snow"
            getWorkTime() >> 7.0f
            getId() >> 1
        }

    }

    def "create() WHEN called with ACCEPTED request on working day SHOULD return evidence with valid status"(){
        given:
        def request = Mock(Request){
            getStatus() >> Request.Status.ACCEPTED
            getType() >> RequestType.NORMAL
        }
        historyLog.getRequest() >> request
        holidayService.isWorkingDay(_ as LocalDate) >> true


        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> [request]

        when:
        def result = modelFactory.create(user,year)

        then:

        result.getModel().get("day."+exampleWorkingDay) == "status"
    }


    def "create() WHEN called with not accepted request on working day SHOULD return evidence with userWorkTime as status"(){
        given:

        def request = Mock(Request){
            getStatus() >> Request.Status.CANCELED
            getType() >> RequestType.NORMAL
        }
        historyLog.getRequest() >> request
        holidayService.isWorkingDay(_ as LocalDate) >> true
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> [request]

        when:
        def result = modelFactory.create(user,year)

        then:

        result.getModel().get("day."+exampleWorkingDay) == "7"
    }

    def "create() WHEN called with on holidayDate day SHOULD return -"(){
        given:

        def request = Mock(Request){
            getStatus() >> Request.Status.ACCEPTED
            getType() >> RequestType.NORMAL
        }
        historyLog.getRequest() >> request

        holidayService.isWorkingDay(_ as LocalDate) >> false

        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> [request]


        when:
        def result = modelFactory.create(user,year)

        then:

        result.getModel().get("day." + exampleHolidayDay) == "-"
    }
}

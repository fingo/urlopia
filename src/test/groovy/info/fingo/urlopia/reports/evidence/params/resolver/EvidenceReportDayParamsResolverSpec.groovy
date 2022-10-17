package info.fingo.urlopia.reports.evidence.params.resolver

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.history.HistoryLogExcerptProjection
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.history.UserDetailsChangeEvent
import info.fingo.urlopia.holidays.Holiday
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.reports.ReportStatusFromRequestType
import info.fingo.urlopia.reports.evidence.EvidenceReportModel
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.request.absence.SpecialAbsenceReason
import info.fingo.urlopia.user.User
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class EvidenceReportDayParamsResolverSpec extends Specification {

    def holidayService = Mock(HolidayService){
        generateHolidaysList(_ as Integer) >> []
    }
    def requestService = Mock(RequestService){
        getByUserAndDate(_ as Long, _ as LocalDate) >> []
    }
    def presenceConfirmationService = Mock(PresenceConfirmationService){
        getByUserAndDate(_ as Long, _ as LocalDate) >> []
    }
    def historyLogService = Mock(HistoryLogService){
        get(_ as Long, _ as Integer, _ as UserDetailsChangeEvent) >> []
    }


    def day = 4
    def month = 1
    def year = 2020
    def validDate = LocalDate.of(year, month, day)
    def userId = 5
    def user = Mock(User) {
        getId() >> userId
    }

    def EMPTY_KEY = ""
    def DEFAULT_KEY = "-"

    def "resolve() WHEN called with year in future, SHOULD return model that contains prefix and empty string as value"() {
        given: "year from future"
        def year = LocalDate.now().plusYears(2).getYear()
        def day = 1
        def month = 1

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                holidayService,
                                                                                requestService,
                                                                                presenceConfirmationService,
                                                                                historyLogService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)


        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == ""
    }

    def "resolve() WHEN called with not existing date from past SHOULD return model that contains prefix and - as value"() {
        given: "not existing date from past"
        def year = 2020
        def day = 31
        def month = 02

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                holidayService,
                                                                                requestService,
                                                                                presenceConfirmationService,
                                                                                historyLogService)

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) =="-"
    }

    def "resolve() WHEN called with holiday date and status from request not in special handle list SHOULD return model that contains prefix and holiday handler result as value"() {
        given: "holiday mock"
        def holidayName = "holiday"
        def holiday = new Holiday(holidayName, validDate)

        and: "holidayService mock that say example date is holiday"
        holidayService.isHoliday(_ as LocalDate) >> true
        holidayService.getHolidayByDate(_ as LocalDate) >> holiday

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)
        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                    holidayService,
                                                                                    requestService,
                                                                                    presenceConfirmationService,
                                                                                    historyLogService)
        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

    @Unroll
    def "resolve() WHEN called with holiday date and status from request in special handle list SHOULD return model that contains prefix and holiday handler result as value"() {
        given: "holiday mock"
        def holidayName = "holiday"
        def holiday = new Holiday(holidayName, validDate)

        and: "holidayService mock that say example date is holiday"
        holidayService.isHoliday(_ as LocalDate) >> true
        holidayService.getHolidayByDate(_ as LocalDate) >> holiday

        and: "requestService mock with special request"
        def request = Mock(Request){
            getStatus() >> Request.Status.ACCEPTED
            getType() >> RequestType.SPECIAL
            getSpecialTypeInfo() >> specialTypeInfo

        }
        requestService = Mock(RequestService){
            getByUserAndDate(_ as Long, _ as LocalDate) >> [request]
        }

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)
        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                holidayService,
                requestService,
                presenceConfirmationService,
                historyLogService)
        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == valueFromHandler

        where:
        specialTypeInfo                                          | valueFromHandler
        SpecialAbsenceReason.PARENTAL_LEAVE.toString()           | ReportStatusFromRequestType.PARENTAL_LEAVE.getEvidenceReportStatus()
        SpecialAbsenceReason.MATERNITY_LEAVE.toString()          | ReportStatusFromRequestType.MATERNITY_LEAVE.getEvidenceReportStatus()
        SpecialAbsenceReason.PATERNITY_LEAVE.toString()          | ReportStatusFromRequestType.PATERNITY_LEAVE.getEvidenceReportStatus()
        SpecialAbsenceReason.SICK_LEAVE_EMPLOYEE.toString()      | ReportStatusFromRequestType.SICK_LEAVE_EMPLOYEE.getEvidenceReportStatus()
        SpecialAbsenceReason.SICK_LEAVE_CHILD.toString()         | ReportStatusFromRequestType.SICK_LEAVE_CHILD.getEvidenceReportStatus()
        SpecialAbsenceReason.SICK_LEAVE_FAMILY.toString()        | ReportStatusFromRequestType.SICK_LEAVE_FAMILY.getEvidenceReportStatus()
        SpecialAbsenceReason.QUARANTINE_OR_ISOLATION.toString()  | ReportStatusFromRequestType.QUARANTINE_OR_ISOLATION.getEvidenceReportStatus()
        SpecialAbsenceReason.CHILDCARE.toString()                | ReportStatusFromRequestType.CHILDCARE.getEvidenceReportStatus()

    }

    def "resolve() WHEN called with weekend date SHOULD return model that contains prefix and handler result as value"() {
        given: "holidayService mock that say example date is weekend"
        def date = LocalDate.of(year,month,day)
        holidayService.isWeekend(date) >> true
        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                                                                                holidayService,
                                                                                requestService,
                                                                                presenceConfirmationService,
                                                                                historyLogService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

    def "resolve() WHEN called with working day date without accepted request SHOULD return model that contains prefix and handler result as value"() {
        given: "requestService mock that not return any accepted request on that day"
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> []

        and: "holidayService mock that say example date is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                holidayService,
                requestService,
                presenceConfirmationService,
                historyLogService)

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

    def "resolve() WHEN called with working day date with accepted request SHOULD return model that contains prefix and handler result as value"() {
        given: "requestService mock that not return any accepted request on that day"
        def request = Mock(Request) {
            getStatus() >> Request.Status.ACCEPTED
        }
        requestService.getByUserAndDate(_ as Long, _ as LocalDate) >> [request]

        and: "holidayService mock that say example date is working day"
        holidayService.isWorkingDay(_ as LocalDate) >> true
        historyLogService.get(_ as Long, year, _ as UserDetailsChangeEvent) >> []

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,year,
                holidayService,
                requestService,
                presenceConfirmationService,
                historyLogService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
    }

    def "resolve() WHEN called with date that user was after switch from ec to b2b SHOULD return default value "() {
        given:

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,givenDate.getYear(),
                holidayService,
                requestService,
                presenceConfirmationService,
                historyLogService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, givenDate.getMonthValue(), givenDate.getDayOfMonth())

        and: "mock change from ec to b2b"
        def mockedLog = Mock(HistoryLogExcerptProjection){
            getCreated() >> givenDate
        }

        historyLogService.get(_ as Long, _ as Integer, UserDetailsChangeEvent.USER_CHANGE_TO_B2B) >> [mockedLog]

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == DEFAULT_KEY

        where:
        changeDate             | givenDate
        LocalDate.of(2021,1,2) | LocalDate.of(2021,1,2)
        LocalDate.of(2021,1,1) | LocalDate.of(2021,1,2)
    }

    def "resolve() WHEN called with date that user was before switch from b2b to ec SHOULD return default value "() {
        given:

        def evidenceReportDayParamsResolver = new EvidenceReportDayParamsResolver(user,givenDate.getYear(),
                holidayService,
                requestService,
                presenceConfirmationService,
                historyLogService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, givenDate.getMonthValue(), givenDate.getDayOfMonth())

        and: "mock change from ec to b2b"
        def mockedLog = Mock(HistoryLogExcerptProjection){
            getCreated() >> givenDate
        }

        historyLogService.get(_ as Long, _ as Integer, UserDetailsChangeEvent.USER_CHANGE_TO_EC) >> [mockedLog]

        when:
        def result = evidenceReportDayParamsResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == DEFAULT_KEY

        where:
        changeDate             | givenDate
        LocalDate.of(2021,1,2) | LocalDate.of(2021,1,1)
    }

}

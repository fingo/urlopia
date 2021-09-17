package info.fingo.urlopia.reports.evidence

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.request.RequestService
import info.fingo.urlopia.user.User
import spock.lang.Specification

class EvidenceReportModelFactorySpec extends Specification {

    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def holidayService = Mock(HolidayService)
    def requestService = Mock(RequestService)
    def historyLogService = Mock(HistoryLogService){
        getFromYear(_ as Long, _ as Integer) >> []
    }

    def evidenceReportModelFactory = new EvidenceReportModelFactory(presenceConfirmationService,holidayService,
                                                                    requestService,historyLogService)
    def year = 2021
    def user = Mock(User){
        getId() >> 5
    }

    def START_TIME_PREFIX = "startTime";
    def REPORT_DATE_PREFIX = "reportDate";
    def END_TIME_PREFIX = "endTime";
    def USER_METADATA_PREFIX = "user";
    def VACATION_LEAVE_PREFIX = "vacationLeave";
    def DAY_STATUS_PREFIX = "day";
    def USED_TIME_PREFIX = "usedTime";

    def "create WHEN called with year SHOULD resolve all prefixes to build a model representing this year "(){
        when:
        def result = evidenceReportModelFactory.create(user, year)

        then:
        containsKeyStartsWith(result.getModel(),START_TIME_PREFIX)
        containsKeyStartsWith(result.getModel(),REPORT_DATE_PREFIX)
        containsKeyStartsWith(result.getModel(),END_TIME_PREFIX)
        containsKeyStartsWith(result.getModel(),USER_METADATA_PREFIX)
        containsKeyStartsWith(result.getModel(),VACATION_LEAVE_PREFIX)
        containsKeyStartsWith(result.getModel(),DAY_STATUS_PREFIX)
        containsKeyStartsWith(result.getModel(),USED_TIME_PREFIX)
    }

    def "generateModelForFileName WHEN called with year SHOULD resolve only prefixes needed to build a model representing fileName "(){
        when:
        def result = evidenceReportModelFactory.generateModelForFileName(user, year)

        then:
        !containsKeyStartsWith(result.getModel(),START_TIME_PREFIX)
        containsKeyStartsWith(result.getModel(),REPORT_DATE_PREFIX)
        !containsKeyStartsWith(result.getModel(),END_TIME_PREFIX)
        containsKeyStartsWith(result.getModel(),USER_METADATA_PREFIX)
        !containsKeyStartsWith(result.getModel(),VACATION_LEAVE_PREFIX)
        !containsKeyStartsWith(result.getModel(),DAY_STATUS_PREFIX)
        !containsKeyStartsWith(result.getModel(),USED_TIME_PREFIX)
    }


    private boolean containsKeyStartsWith(Map<String,String> model,
                                          String prefix){
        var keys = model.keySet()
        for (String key: keys){
            if (key.startsWith(prefix)){
                return true
            }
        }
        return false
    }
}

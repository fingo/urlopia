package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime

class EvidenceReportDayWithPresenceHandlerSpec extends Specification{

    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def evidenceReportDayWithPresenceHandler = new EvidenceReportDayWithPresenceHandler(presenceConfirmationService)
    def userID = 5
    def user = Mock(User){
        getId() >> userID
    }
    def exampleDate = LocalDate.now()
    def  decimalFormat = new DecimalFormat();


    def "handle() WHEN called on day without presence confirmations SHOULD return - "(){
        given:
        presenceConfirmationService.getByUserAndDate(_ as Long, _ as LocalDate) >> []

        when:
        def result = evidenceReportDayWithPresenceHandler.handle(user,exampleDate)

        then:
        result == "-"
    }

    def "handle() WHEN called on day with presence confirmations SHOULD count duration between startTime and endTime"(){
        given:
        def startTime = LocalTime.of(10,0)
        def endTime = LocalTime.of(12,0)
        def presenceConfirmation = Mock(PresenceConfirmation){
            getStartTime() >> startTime
            getEndTime() >> endTime
        }
        def hoursBetween = 2
        presenceConfirmationService.getByUserAndDate(_ as Long, _ as LocalDate) >> [presenceConfirmation]

        when:
        def result = evidenceReportDayWithPresenceHandler.handle(user,exampleDate)

        then:
        result == decimalFormat.format(hoursBetween)
    }

}

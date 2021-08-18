package info.fingo.urlopia.request.absence

import info.fingo.urlopia.request.RequestType
import spock.lang.Specification

import java.time.LocalDate

class SpecialAbsenceRequestInputSpec extends Specification {

    def startDate = LocalDate.now()
    def endDate = LocalDate.now()
    def requesterId = 1L
    def reason = SpecialAbsenceReason.OTHER

    def "fromSpecialAbsence() SHOULD return SpecialAbsenceRequest with same value at corresponding fields"() {
        given:
        def specialAbsence = new SpecialAbsence(requesterId, startDate, endDate, reason)

        when:
        def input = SpecialAbsenceRequestInput.fromSpecialAbsence(specialAbsence)

        then:
        input.getStartDate() == specialAbsence.startDate()
        input.getEndDate() == specialAbsence.endDate()
        input.getReason() == specialAbsence.reason()
        input.getType() == RequestType.SPECIAL
    }
}

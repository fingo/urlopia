package info.fingo.urlopia.reports.evidence.params.resolver

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.holidays.HolidayService
import info.fingo.urlopia.reports.evidence.EvidenceReportModel
import info.fingo.urlopia.user.User
import spock.lang.Specification
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EvidenceReportPresenceConfirmationTimeParamResolverSpec extends Specification{

    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def holidayService = Mock(HolidayService)
    def dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    def exampleId = 5
    def user = Mock(User) {
        getId() >> exampleId
    }


    def "resolve() WHEN called with year in future, SHOULD return model that contains prefix and and empty string as value"() {
        given: "year from future"
        def year = LocalDate.now().plusYears(2).getYear()
        def day = 1
        def month = 1

        def evidenceReportPresenceConfirmationTimeParamResolver =
                EvidenceReportPresenceConfirmationTimeParamResolver.ofStartTime(user,
                                                                              year,
                                                                              presenceConfirmationService,
                                                                              holidayService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result = evidenceReportPresenceConfirmationTimeParamResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == ""
    }

    def "resolve() WHEN called with not existing date SHOULD return model that contains prefix and - as value"() {
        given: "not existing date from past"
        def year = 2020
        def day = 31
        def month = 02

        def evidenceReportPresenceConfirmationTimeParamResolver =
                EvidenceReportPresenceConfirmationTimeParamResolver.ofStartTime(user,
                        year,
                        presenceConfirmationService,
                        holidayService)

        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        when:
        def result =  evidenceReportPresenceConfirmationTimeParamResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == "-"
    }

    def "resolve() WHEN called with not working day SHOULD return model that contains prefix and - as value "() {
        given: "valid date from past"
        def day = 1
        def month = 1
        def year = 2021
        def date = LocalDate.of(year, month, day)

        def evidenceReportPresenceConfirmationTimeParamResolver =
                EvidenceReportPresenceConfirmationTimeParamResolver.ofStartTime(user,
                        year,
                        presenceConfirmationService,
                        holidayService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)


        and: "holidayService mock that say example date is not working day"
        holidayService.isWorkingDay(date) >> false

        when:
        def result =evidenceReportPresenceConfirmationTimeParamResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == "-"
    }

    def "resolve() WHEN called with date with presenceConfirmation and startTime factor method SHOULD return model that contains prefix and formatted startTime as value"() {
        given: "valid date from past"
        def day = 1
        def month = 1
        def year = 2021
        def date = LocalDate.of(year, month, day)


        def time = LocalTime.now();
        def evidenceReportPresenceConfirmationTimeParamResolver =
                EvidenceReportPresenceConfirmationTimeParamResolver.ofStartTime(user,
                        year,
                        presenceConfirmationService,
                        holidayService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        and: "holidayService mock that say example date is not working day"
        holidayService.isWorkingDay(date) >> true

        and: "presenceConfirmationService mock that return date without presenceConfirmation"
        def presence = Mock(PresenceConfirmation) {
            getStartTime() >> time
        }
        presenceConfirmationService.getByUserAndDate(_ as Long, _ as LocalDate) >> [presence]

        when:
        def result = evidenceReportPresenceConfirmationTimeParamResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == dateTimeFormatter.format(time)
    }

    def "resolve() WHEN called with date with presenceConfirmation and endTime factor method SHOULD return model that contains prefix and formatted endTime as value"() {
        given: "valid date from past"
        def day = 1
        def month = 1
        def year = 2021
        def date = LocalDate.of(year, month, day)


        def time = LocalTime.now();
        def evidenceReportPresenceConfirmationTimeParamResolver =
                EvidenceReportPresenceConfirmationTimeParamResolver.ofEndTime(user,
                        year,
                        presenceConfirmationService,
                        holidayService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)

        and: "holidayService mock that say example date is not working day"
        holidayService.isWorkingDay(date) >> true

        and: "presenceConfirmationService mock that return date without presenceConfirmation"
        def presence = Mock(PresenceConfirmation) {
            getEndTime() >> time
        }
        presenceConfirmationService.getByUserAndDate(_ as Long, _ as LocalDate) >> [presence]

        when:
        def result = evidenceReportPresenceConfirmationTimeParamResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == dateTimeFormatter.format(time)
    }

    def "resolve() WHEN called with date with presenceConfirmation SHOULD return model that contains prefix and  - as value"() {
        given: "valid date from past"
        def day = 1
        def month = 1
        def year = 2021
        def date = LocalDate.of(year, month, day)

        def evidenceReportPresenceConfirmationTimeParamResolver =
                EvidenceReportPresenceConfirmationTimeParamResolver.ofStartTime(user,
                        year,
                        presenceConfirmationService,
                        holidayService)
        def key = String.format(EvidenceReportModel.DATE_FORMATTING, month, day)


        and: "holidayService mock that say example date is working day"
        holidayService.isWorkingDay(date) >> true

        and: "presenceConfirmationService mock that return date without presenceConfirmation"
        presenceConfirmationService.getByUserAndDate(exampleId, date) >> []

        when:
        def result = evidenceReportPresenceConfirmationTimeParamResolver.resolve()

        then:
        result.containsKey(key)
        result.get(key) == "-"
    }

}

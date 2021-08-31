package info.fingo.urlopia.reports.evidence.params.resolver

import info.fingo.urlopia.user.User
import spock.lang.Specification

import java.text.DecimalFormat

class EvidenceReportUserParamsResolverSpec extends Specification{
    def DECIMAL_FORMAT = new DecimalFormat()
    def firstName = "John"
    def lastName = "Snow"
    def workTime = 8.0f
    def user = Mock(User){
        getLastName() >> lastName
        getFirstName() >> firstName
        getWorkTime() >> workTime
    }
    def evidenceReportUserParamsResolver = new EvidenceReportUserParamsResolver(user)
    def firstNameModelPrefix = "firstName"
    def lastNameModelPrefix = "lastName"
    def workTimeModelPrefix = "workTime"

    def "resolve() WHEN called with user SHOULD return model that contains prefix and his  firstName as value"() {
        when:
        def result = evidenceReportUserParamsResolver.resolve()

        then:
        result.containsKey(firstNameModelPrefix)
        result.get(firstNameModelPrefix) == firstName
    }

    def "resolve() WHEN called with user SHOULD return model that contains prefix and his lastName as value"() {
        when:
        def result = evidenceReportUserParamsResolver.resolve()

        then:
        result.containsKey(lastNameModelPrefix)
        result.get(lastNameModelPrefix) == lastName
    }



    def "resolve() WHEN called with user SHOULD return model that contains prefix and format his workTime as value"() {
        when:
        def result = evidenceReportUserParamsResolver.resolve()

        then:
        result.containsKey(workTimeModelPrefix)
        result.get(workTimeModelPrefix) == DECIMAL_FORMAT.format(workTime)
    }
}

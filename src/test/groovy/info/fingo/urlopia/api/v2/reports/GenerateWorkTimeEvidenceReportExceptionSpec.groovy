package info.fingo.urlopia.api.v2.reports

import spock.lang.Specification

class GenerateWorkTimeEvidenceReportExceptionSpec extends Specification{

    def "fromIOException WHEN called with fileName SHOULD return new GenerateWorkTimeEvidenceReportException object with formatted message"(){
        given:
        def fileName = "exampleFile"
        def errorMessagePrefix = "Unable to generate report with name: "
        def expectedMessage = errorMessagePrefix + fileName

        when:
        def result = GenerateWorkTimeEvidenceReportException.fromIOException(fileName)

        then:
        result.getMessage() == expectedMessage
    }
}

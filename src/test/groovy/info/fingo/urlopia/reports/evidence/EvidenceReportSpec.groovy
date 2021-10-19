package info.fingo.urlopia.reports.evidence

import spock.lang.Specification

class EvidenceReportSpec extends Specification {
    private static final VALID_TEMPLATE_FILE_NAME = "working_time_evidence.xlsx"

    def "templateName() WHEN called SHOULD return valid template file name"() {
        given:
        EvidenceReport evidenceReport = new EvidenceReport()

        when:
        def result = evidenceReport.templateName()

        then:
        result == VALID_TEMPLATE_FILE_NAME
    }
}

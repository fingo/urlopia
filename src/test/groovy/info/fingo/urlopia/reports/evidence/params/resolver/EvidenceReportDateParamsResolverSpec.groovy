package info.fingo.urlopia.reports.evidence.params.resolver

import spock.lang.Specification

class EvidenceReportDateParamsResolverSpec extends Specification {
    def modelPrefix = "year"
    def "resolve() WHEN called with year SHOULD return model that contains prefix and year mapped to string and return"() {
        given:
        int exampleYear = 2021;
        EvidenceReportDateParamsResolver evidenceReportDateParamsResolver = new EvidenceReportDateParamsResolver(exampleYear);

        when:
        def result = evidenceReportDateParamsResolver.resolve()

        then:
        result == Map.of(modelPrefix,String.valueOf(exampleYear))
    }
}

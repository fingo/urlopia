package info.fingo.urlopia.api.v2.reports

import org.apache.poi.ss.usermodel.Workbook
import spock.lang.Specification

class reportControllerV2Spec extends Specification{
    private ReportService reportService;
    private ReportControllerV2 reportControllerV2

    void setup(){
        reportService = Mock(ReportService)
        reportControllerV2 = new ReportControllerV2(reportService)
    }

    def "generateWorkTimeEvidenceReport() WHEN service throw IO exception SHOULD catch it and throw GenerateWorkTimeEvidenceReportException"(){
        given:
        def userID = 5L
        def year = 2021
        reportService.generateWorkTimeEvidenceReport(userID,year) >> {
            throw new IOException();
        }
        reportService.getWorkTimeEvidenceReportName(userID, year) >> ""

        when:
        reportControllerV2.generateWorkTimeEvidenceReport(userID,year)

        then:
        thrown(GenerateWorkTimeEvidenceReportException)
    }

    def "generateWorkTimeEvidenceReport() WHEN service not throw IO exception SHOULD not throw GenerateWorkTimeEvidenceReportException"(){
        given: "Mock for Workbook objet"
        def userID = 5L
        def year = 2021
        def workbook = Mock(Workbook){
            write(_ as OutputStream) >> null
        }

        and: "reportService mock"
        reportService.generateWorkTimeEvidenceReport(userID,year) >> workbook
        reportService.getWorkTimeEvidenceReportName(userID,year) >> "validFileName"

        when:
        reportControllerV2.generateWorkTimeEvidenceReport(userID,year)

        then:
        notThrown(GenerateWorkTimeEvidenceReportException)
    }

}
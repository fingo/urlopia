package info.fingo.urlopia.api.v2.reports;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.api.v2.reports.converters.AttendanceListExcelConverter;
import info.fingo.urlopia.config.persistance.filter.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v2/reports")
public class ReportControllerV2 {
    private final ReportService reportService;

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(path = "/work-time-evidence/user/{userId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> generateWorkTimeEvidenceReport(@PathVariable Long userId,
                                                                                @RequestParam int year) {

        var fileName = reportService.getWorkTimeEvidenceReportName(userId, year);
        var anonymizedFileName = Anonymizer.anonymizeYearlyReportFileName(fileName);
        try {
            var workTimeEvidenceReport = reportService.generateWorkTimeEvidenceReport(userId, year);
            var headers = getFileContentHeaders(fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(workTimeEvidenceReport::write);
        } catch (IOException ioException){
            log.error("Could not generate report with name: {}", anonymizedFileName);
            throw GenerateWorkTimeEvidenceReportException.fromIOException(anonymizedFileName);
        }
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(path = "/monthly-presence/{year}/{month}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> generateMonthlyPresenceReport(@PathVariable Integer year,
                                                                               @PathVariable Integer month) {

        var fileName = reportService.getMonthlyPresenceReportName(year, month);
        try {
            var monthlyPresenceReports = reportService.generateAttendanceList(year, month);
            var headers = getFileContentHeaders(fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream -> AttendanceListExcelConverter.convertToPDF(monthlyPresenceReports, outputStream));
        } catch (IOException ioException) {
            log.error("Could not generate report with name: {}", fileName);
            throw GenerateWorkTimeEvidenceReportException.fromIOException(fileName);
        }
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(path = "/holidays/user/{userId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> generateMonthlyPresenceReport(@PathVariable Long userId,
                                                                               @RequestParam(name = "filter", defaultValue = "") String[] filters) {

        var fileName = reportService.getHolidaysReportName(userId);
        var filter = Filter.from(filters);
        var headers = getFileContentHeaders(fileName);
        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream -> reportService.generateUserHolidaysReport(userId, outputStream, filter));
    }

    private HttpHeaders getFileContentHeaders(String fileName) {
        var contentDispositionBuilderType = "inline";
        var headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder(contentDispositionBuilderType)
                                              .filename(fileName, StandardCharsets.UTF_8)
                                              .build());
        return headers;
    }
}

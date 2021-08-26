package info.fingo.urlopia.api.v2.reports;

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
    @GetMapping(path = "/work-time-evidence/user/{userId}",
                produces= MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> generateWorkTimeEvidenceReport(@PathVariable Long userId,
                                                                                @RequestParam int year){

        var fileName = reportService.getWorkTimeEvidenceReportName(userId,year);
        try{
            var workTimeEvidenceReport = reportService.generateWorkTimeEvidenceReport(userId, year);

            var headers = getFileContentHeaders(fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(workTimeEvidenceReport::write);
        }catch (IOException ioException){
            log.error("Could not generate report with name: {}", fileName);
            throw GenerateWorkTimeEvidenceReportException.fromIOException(fileName);
        }

    }

    private HttpHeaders getFileContentHeaders(String fileName){
        var contentDispositionBuilderType = "inline";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder(contentDispositionBuilderType)
                .filename(fileName, StandardCharsets.UTF_8)
                .build());
        return headers;
    }

}

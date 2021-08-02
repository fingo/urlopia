package info.fingo.urlopia.reports;

import info.fingo.urlopia.reports.evidence.EvidenceReport;
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory;
import info.fingo.urlopia.user.UserService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ReportController {

    private final UserService userService;

    private final ReportTemplateLoader reportTemplateLoader;

    private final XlsxTemplateResolver xlsxTemplateResolver;

    private final EvidenceReportModelFactory evidenceReportModelFactory;

    @Autowired
    public ReportController(UserService userService,
                            ReportTemplateLoader reportTemplateLoader,
                            XlsxTemplateResolver xlsxTemplateResolver,
                            EvidenceReportModelFactory evidenceReportModelFactory) {
        this.userService = userService;
        this.reportTemplateLoader = reportTemplateLoader;
        this.xlsxTemplateResolver = xlsxTemplateResolver;
        this.evidenceReportModelFactory = evidenceReportModelFactory;
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(path = "/reports")
    public void getExcelReport(HttpServletResponse response,
                               @RequestParam Long userId,
                               @RequestParam int year) throws IOException {
        var user = this.userService.get(userId);

        var report = new EvidenceReport();
        var model = this.evidenceReportModelFactory.create(user, year);
        var templateResource = this.reportTemplateLoader.load(report.templateName());

        var inputStream = templateResource.getInputStream();
        try (var reportFile = new XSSFWorkbook(inputStream)) {
            this.xlsxTemplateResolver.resolve(reportFile, model.getModel());
            response.setHeader("Content-Type", report.mimeType());
            response.setHeader("Content-disposition",
                    "attachment; filename=%s".formatted(report.fileName(model)));
            reportFile.write(response.getOutputStream());
        }
    }
}

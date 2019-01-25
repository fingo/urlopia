package info.fingo.urlopia.reports;

import info.fingo.urlopia.reports.evidence.EvidenceReport;
import info.fingo.urlopia.reports.evidence.EvidenceReportModel;
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ReportController {

    private final UserService userService;

    private final ReportTemplateLoader reportTemplateLoader;

    private final XlsxTemplateResolver xlsxTemplateResolver;

    private final EvidenceReportModelFactory evidenceReportModelFactory;

    @Autowired
    public ReportController(UserService userService,
                            ReportTemplateLoader reportTemplateLoader,
                            XlsxTemplateResolver xlsxTemplateResolver, EvidenceReportModelFactory evidenceReportModelFactory) {
        this.userService = userService;
        this.reportTemplateLoader = reportTemplateLoader;
        this.xlsxTemplateResolver = xlsxTemplateResolver;
        this.evidenceReportModelFactory = evidenceReportModelFactory;
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/reports", method = RequestMethod.GET)
    public void getExcelReport(HttpServletResponse response,
                               @RequestParam Long userId,
                               @RequestParam int year) throws IOException {
        User user = this.userService.get(userId);

        EvidenceReport report = new EvidenceReport();
        EvidenceReportModel model = this.evidenceReportModelFactory.create(user, year);
        Resource templateResource = this.reportTemplateLoader.load(report.templateName());

        InputStream template = templateResource.getInputStream();
        try (Workbook reportFile = new XSSFWorkbook(template)) {
            this.xlsxTemplateResolver.resolve(reportFile, model.getModel());
            response.setHeader("Content-Type", report.mimeType());
            response.setHeader("Content-disposition", "attachment; filename=" + report.fileName(model));
            reportFile.write(response.getOutputStream());
        }
    }
}

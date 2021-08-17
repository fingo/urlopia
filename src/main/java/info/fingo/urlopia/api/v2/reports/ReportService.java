package info.fingo.urlopia.api.v2.reports;

import info.fingo.urlopia.reports.ReportTemplateLoader;
import info.fingo.urlopia.reports.XlsxTemplateResolver;
import info.fingo.urlopia.reports.evidence.EvidenceReport;
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final UserService userService;

    private final ReportTemplateLoader reportTemplateLoader;

    private final XlsxTemplateResolver xlsxTemplateResolver;

    private final EvidenceReportModelFactory evidenceReportModelFactory;


    public Workbook generateWorkTimeEvidenceReport(Long userId,
                                                   int year) throws IOException {
        var user = this.userService.get(userId);
        var report = new EvidenceReport();
        var model = this.evidenceReportModelFactory.create(user, year);
        var templateResource = this.reportTemplateLoader.load(report.templateName());
        var inputStream = templateResource.getInputStream();
        var reportFile = new XSSFWorkbook(inputStream) ;
        this.xlsxTemplateResolver.resolve(reportFile, model.getModel());
        return reportFile;
    }

    public String getWorkTimeEvidenceReportName(Long userId,
                                                int year){
        var user = this.userService.get(userId);
        var report = new EvidenceReport();
        var model = this.evidenceReportModelFactory.create(user, year);
        return "attachment; filename=%s".formatted(report.fileName(model));

    }
}

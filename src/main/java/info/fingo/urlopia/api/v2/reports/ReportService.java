package info.fingo.urlopia.api.v2.reports;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.api.v2.reports.attendance.AttendanceListPage;
import info.fingo.urlopia.api.v2.reports.attendance.MonthlyAttendanceListReport;
import info.fingo.urlopia.api.v2.reports.attendance.MonthlyAttendanceListReportFactory;
import info.fingo.urlopia.api.v2.reports.holidays.UserHolidaysReportFactory;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.reports.ReportTemplateLoader;
import info.fingo.urlopia.reports.XlsxTemplateResolver;
import info.fingo.urlopia.reports.evidence.EvidenceReport;
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {
    private final UserService userService;
    private final ReportTemplateLoader reportTemplateLoader;
    private final XlsxTemplateResolver xlsxTemplateResolver;
    private final EvidenceReportModelFactory evidenceReportModelFactory;
    private final MonthlyAttendanceListReportFactory monthlyPresenceReportFactory;
    private final UserHolidaysReportFactory userHolidaysReportFactory;

    public Workbook generateWorkTimeEvidenceReport(Long userId,
                                                   int year) throws IOException {
        var user = this.userService.get(userId);
        var report = new EvidenceReport();
        var model = this.evidenceReportModelFactory.create(user, year);
        var templateResource = this.reportTemplateLoader.load(report.templateName());
        var inputStream = templateResource.getInputStream();
        var reportFile = new XSSFWorkbook(inputStream);
        this.xlsxTemplateResolver.resolve(reportFile, model.getModel());
        return reportFile;
    }

    public void generateZipWithReports(ZipOutputStream zipOut,
                                       int year,
                                       Filter filter) {
        var users = userService.get(filter);
        var fileName = "";

        try {
            ByteArrayOutputStream byteOutput;

            for (User user : users) {
                var evidenceReport = generateWorkTimeEvidenceReport(user.getId(), year);
                fileName = "EwidencjaCzasuPracy_%d_%s_%d.xlsx".formatted(year, user.getFullName(), user.getId());

                zipOut.putNextEntry(new ZipEntry(fileName));
                byteOutput = new ByteArrayOutputStream();
                evidenceReport.write(byteOutput);
                byteOutput.writeTo(zipOut);
                zipOut.closeEntry();
                byteOutput.close();
            }

            zipOut.close();
        }
        catch (IOException ioException) {
            log.error("Could not generate report with name: {}",
                    Anonymizer.anonymizeYearlyReportFileName(fileName));
            throw GenerateWorkTimeEvidenceReportException.fromIOException(fileName);
        }
    }

    public String getWorkTimeEvidenceReportName(Long userId,
                                                int year) {
        var user = this.userService.get(userId);
        var report = new EvidenceReport();
        var model = this.evidenceReportModelFactory.generateModelForFileName(user,year);
        return "attachment; filename=%s".formatted(report.fileName(model));
    }

    public List<Workbook> generateAttendanceList(Integer year,
                                                 Integer month) throws IOException {
        var filter = Filter.newBuilder()
                .and("active", Operator.EQUAL, "true")
                .and("ec", Operator.EQUAL, "true")
                .build();

        var employees = userService.get(filter).stream()
                .sorted((u1, u2) -> u1.getLastName().compareToIgnoreCase(u2.getLastName()))
                .toList();

        return partitionUsersToPages(employees).stream()
                .map(page -> generateAttendanceListPage(year, month, page))
                .toList();
    }

    private List<AttendanceListPage> partitionUsersToPages(List<User> users) {
        List<AttendanceListPage> result = new LinkedList<>();

        var currentPage = new AttendanceListPage();
        for (var user : users) {
            currentPage.addUser(user);
            if (currentPage.isFull()) {
                result.add(currentPage);
                currentPage = new AttendanceListPage();
            }
        }

        if (!currentPage.isEmpty()) {
            result.add(currentPage);
        }

        return result;
    }

    private Workbook generateAttendanceListPage(Integer year, Integer month, AttendanceListPage page) {
        var report = new MonthlyAttendanceListReport();
        var model = monthlyPresenceReportFactory.create(month, year, page);
        var templateResource = reportTemplateLoader.load(report.templateName());

        Workbook reportFile;
        try {
            var inputStream = templateResource.getInputStream();
            reportFile = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.info("Couldn't load attendance list template from classpath");
            e.printStackTrace();
            reportFile = new XSSFWorkbook();
        }

        this.xlsxTemplateResolver.resolve(reportFile, model.getModel());
        evaluateFormulasInAttendanceListPage(reportFile);

        return reportFile;
    }

    private void evaluateFormulasInAttendanceListPage(Workbook workbook) {
        var formulaRowNums = List.of("39", "40", "41", "42", "43");
        var formulaColumns = List.of("C", "D", "E", "F");

        var sheet = workbook.getSheetAt(0);
        var evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (var rowNum : formulaRowNums) {
            var fullNameCell = getCellFromStringRepresentation(sheet, "A" + rowNum);
            if (fullNameCell.getStringCellValue().equals("")) {
                continue;
            }

            for (var column : formulaColumns) {
                var formulaCell = column + rowNum;
                var cell = getCellFromStringRepresentation(sheet, formulaCell);
                evaluator.evaluateInCell(cell);
            }
        }
    }

    private Cell getCellFromStringRepresentation(Sheet sheet, String stringRepresentation) {
        var cellRef = new CellReference(stringRepresentation);
        var row = sheet.getRow(cellRef.getRow());
        return row.getCell(cellRef.getCol());
    }

    public String getMonthlyPresenceReportName(Integer year, Integer month) {
        return "lista_obecności_%s_%s.pdf".formatted(month, year);
    }

    public void generateUserHolidaysReport(Long userId, OutputStream outputStream, Filter requestFilter) {
        userHolidaysReportFactory.createAsPDF(userId, outputStream, requestFilter);
    }

    public String getHolidaysReportName(Long userId) {
        var user = userService.get(userId);
        var userFirstName = user.getFirstName();
        var userLastName = user.getLastName();
        var date = LocalDate.now();
        return "wnioski_urlopowe_%s_%s_%s.pdf".formatted(userFirstName, userLastName, date);
    }
}

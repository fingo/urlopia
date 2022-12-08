package info.fingo.urlopia.api.v2.reports;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.api.v2.reports.attendance.AttendanceListPage;
import info.fingo.urlopia.api.v2.reports.attendance.MonthlyAttendanceListReport;
import info.fingo.urlopia.api.v2.reports.attendance.MonthlyAttendanceListReportFactory;
import info.fingo.urlopia.api.v2.reports.holidays.UserHolidaysReportFactory;
import info.fingo.urlopia.api.v2.user.UserFilterFactory;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLog;
import info.fingo.urlopia.history.HistoryLogExcerptProjection;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import info.fingo.urlopia.reports.ReportTemplateLoader;
import info.fingo.urlopia.reports.XlsxTemplateResolver;
import info.fingo.urlopia.reports.evidence.EvidenceReport;
import info.fingo.urlopia.reports.evidence.EvidenceReportModelFactory;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserFullNameComparator;
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
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {
    private final UserService userService;
    private final RequestService requestService;
    private final PresenceConfirmationService presenceConfirmationService;
    private final ReportTemplateLoader reportTemplateLoader;
    private final XlsxTemplateResolver xlsxTemplateResolver;
    private final EvidenceReportModelFactory evidenceReportModelFactory;
    private final MonthlyAttendanceListReportFactory monthlyPresenceReportFactory;
    private final UserHolidaysReportFactory userHolidaysReportFactory;
    private final UserFilterFactory userFilterFactory;

    private final HistoryLogService historyLogService;

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
        var employees = findEmployeesNeededToBeInAttendanceList(year, month);
        employees.sort(new UserFullNameComparator());

        return partitionUsersToPages(employees).stream()
                .map(page -> generateAttendanceListPage(year, month, page))
                .toList();
    }

    List<User> findEmployeesNeededToBeInAttendanceList(Integer year,
                                                       Integer month) {
        var filter = userFilterFactory.getActiveECUsersFilter();
        var employees = userService.get(filter);
        employees.addAll(findInactiveUsersNeededToBeInReport(year ,month));
        employees.addAll(findUsersThatChangedToB2BAndNeededToBeInReport(year, month));
        employees.removeAll(findAllUserThatWasNotActiveYet(year, month));
        return employees;
    }

    private List<User> findAllUserThatWasNotActiveYet(Integer year,
                                                      Integer month) {
        var nextMonth = YearMonth.of(year,month).plusMonths(1);
        var logsWithActivationEventFromFuture = historyLogService.getBy(nextMonth, UserDetailsChangeEvent.USER_ACTIVATED);
        return logsWithActivationEventFromFuture.stream()
                .map(HistoryLog::getUser)
                .toList();
    }

    private List<User> findInactiveUsersNeededToBeInReport(Integer year,
                                                           Integer month) {
        var filter = userFilterFactory.getInactiveECUsersFilter();
        var employees = userService.get(filter);

        var yearMonth = YearMonth.of(year,month);
        var firstDayOfMonth = yearMonth.atDay(1);
        var lastDayOfMonth = yearMonth.atEndOfMonth();
        var usersWithAcceptedRequests= employees.stream()
                .filter(user -> requestService.hasAcceptedByDateIntervalAndUser(firstDayOfMonth,
                                                                                lastDayOfMonth,
                                                                                user.getId()))
                .collect(Collectors.toSet());
        var usersWithPresence = getEmployeesWithPresence(employees, firstDayOfMonth, lastDayOfMonth);

        usersWithAcceptedRequests.addAll(usersWithPresence);
        return  usersWithAcceptedRequests.stream()
                                         .toList();
    }

    private List<User> findUsersThatChangedToB2BAndNeededToBeInReport(Integer year,
                                                                      Integer month) {
        var filter = userFilterFactory.getActiveB2BUsersFilter();
        var employees = userService.get(filter);

        var yearMonth = YearMonth.of(year,month);
        var firstDayOfMonth = yearMonth.atDay(1);
        var lastDayOfMonth = yearMonth.atEndOfMonth();

        var usersWithPresence = getEmployeesWithPresence(employees, firstDayOfMonth, lastDayOfMonth);
        var usersWithChangeEvent = getB2BWithChangeEvent(yearMonth, employees);

        usersWithPresence.addAll(usersWithChangeEvent);
        return usersWithPresence.stream().toList();
    }

    private Set<User> getB2BWithChangeEvent(YearMonth yearMonth,
                                            List<User> employees){
        return employees.stream()
                .filter(user -> hasChangeToB2B(user, yearMonth))
                .collect(Collectors.toSet());
    }

    private boolean hasChangeToB2B(User user,
                                   YearMonth yearMonth){
        var changeToB2BLogs = historyLogService.get(user.getId(), yearMonth, UserDetailsChangeEvent.USER_CHANGE_TO_B2B);
        if (changeToB2BLogs.isEmpty()){
            return false;
        }
        changeToB2BLogs.sort(Comparator.comparing(HistoryLogExcerptProjection::getCreated));
        var firstLog = changeToB2BLogs.get(0);
        var firstDayOfMonth = 1;
        return firstLog.getCreated().getDayOfMonth() != firstDayOfMonth;
    }

    private Set<User> getEmployeesWithPresence(List<User> employees,
                                               LocalDate firstDayOfMonth,
                                               LocalDate lastDayOfMonth){
        return employees.stream()
                .filter(user -> hasPresence(firstDayOfMonth, lastDayOfMonth, user))
                .collect(Collectors.toSet());
    }

    private boolean hasPresence(LocalDate firstDayOfMonth,
                                LocalDate lastDayOfMonth,
                                User user){
        return presenceConfirmationService.hasPresenceByUserAndDateInterval(firstDayOfMonth, lastDayOfMonth, user.getId());
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

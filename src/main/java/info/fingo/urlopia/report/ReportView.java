package info.fingo.urlopia.report;

import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Builds .xlsx report file from employee data.
 */

@Component
public class ReportView extends AbstractXlsxView {

    private final static String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
    private final static String[] labelStrings = {"ogółem", "czas normatywny", "godziny nadliczbowe 50%",
            "godziny nadliczbowe 100%", "praca nocna", "niedziele i święta", "dodatkowe dni wolne",
            "postojowe", "urlop wypoczynkowy", "urlop okolicznościowy", "urlop - opieka nad dzieckiem",
            "choroba do 33 dni", "choroba pow. 33 dni", "opieka nad chorym", "opieka nad dzieckiem",
            "nieob. inne płatne", "nieob. inne niepłatne", "nieob. nieusprawiedliwione", "dyżury"
    };

    private final UserService userService;

    private final RequestService requestService;

    private final HolidayService holidayService;

    private final HistoryLogService historyLogService;

    @Value("${report.company.name}")
    private String companyName;

    @Value("${report.company.address}")
    private String companyAddress;

    @Autowired
    public ReportView(UserService userService, RequestService requestService, HolidayService holidayService, HistoryLogService historyLogService, WorkingDaysCalculator workingDaysCalculator) {
        this.userService = userService;
        this.requestService = requestService;
        this.holidayService = holidayService;
        this.historyLogService = historyLogService;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Long userId = (Long) model.get("userId");
        int year = (int) model.get("currentYear");
        User user = userService.get(userId);
        List<Request> requests = requestService.get(userId, year);
        float worktime = user.getWorkTime();

        // build doc
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Ewidencja czasu pracy");
        sheet.setDefaultColumnWidth(3);

        // default font & style for most cells
        XSSFCellStyle defaultStyle = (XSSFCellStyle) workbook.createCellStyle();
        defaultStyle.setBorderTop(CellStyle.BORDER_THIN);
        defaultStyle.setBorderRight(CellStyle.BORDER_THIN);
        defaultStyle.setBorderBottom(CellStyle.BORDER_THIN);
        defaultStyle.setBorderLeft(CellStyle.BORDER_THIN);

        XSSFFont defaultFont = (XSSFFont) workbook.createFont();
        defaultFont.setFontHeight(9);
        defaultFont.setFontName("Arial CE");
        defaultStyle.setFont(defaultFont);

        XSSFCellStyle defaultCenterStyle = (XSSFCellStyle) workbook.createCellStyle();
        defaultCenterStyle.setBorderTop(CellStyle.BORDER_THIN);
        defaultCenterStyle.setBorderRight(CellStyle.BORDER_THIN);
        defaultCenterStyle.setBorderBottom(CellStyle.BORDER_THIN);
        defaultCenterStyle.setBorderLeft(CellStyle.BORDER_THIN);
        defaultCenterStyle.setAlignment(CellStyle.ALIGN_CENTER);
        defaultCenterStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        XSSFFont defaultCenterFont = (XSSFFont) workbook.createFont();
        defaultCenterFont.setFontHeight(9);
        defaultCenterFont.setFontName("Arial CE");
        defaultCenterStyle.setFont(defaultCenterFont);

        // labels on top
        sheet.createRow(1).createCell(1).setCellValue(companyName);
        sheet.createRow(2).createCell(1).setCellValue(companyAddress);
        sheet.createRow(4).createCell(1).setCellValue("ROCZNA KARTA EWIDENCJI OBECNOŚCI W PRACY");

        // big box with full name and year
        XSSFRow labelsRow = sheet.createRow(6);
        labelsRow.setHeightInPoints(120);

        sheet.addMergedRegion(new CellRangeAddress(5, 6, 0, 31));

        XSSFCellStyle fullNameYearStyle = (XSSFCellStyle) workbook.createCellStyle();
        fullNameYearStyle.setBorderTop(CellStyle.BORDER_THIN);
        fullNameYearStyle.setBorderRight(CellStyle.BORDER_THIN);
        fullNameYearStyle.setBorderBottom(CellStyle.BORDER_THIN);
        fullNameYearStyle.setBorderLeft(CellStyle.BORDER_THIN);
        fullNameYearStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        fullNameYearStyle.setAlignment(CellStyle.ALIGN_CENTER);
        XSSFFont fullNameYearCellFont = (XSSFFont) workbook.createFont();
        fullNameYearCellFont.setFontName("Arial CE");
        fullNameYearCellFont.setFontHeight(12);
        fullNameYearStyle.setFont(fullNameYearCellFont);

        XSSFRow workTimeRow = sheet.createRow(5);
        XSSFCell fullNameYearCell = workTimeRow.createCell(0);
        fullNameYearCell.setCellStyle(fullNameYearStyle);
        String fullNameYearCellValue = "nazwisko i imię: " + user.getFirstName() + " " + user.getLastName()
                + " rok: " + LocalDate.now().getYear();
        fullNameYearCell.setCellValue(fullNameYearCellValue);

        // labels
        XSSFCellStyle labelCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        labelCellStyle.setBorderTop(CellStyle.BORDER_THIN);
        labelCellStyle.setBorderRight(CellStyle.BORDER_THIN);
        labelCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        labelCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        labelCellStyle.setRotation((short) 90);
        XSSFFont labelCellStyleFont = (XSSFFont) workbook.createFont();
        labelCellStyleFont.setFontName("Arial CE");
        labelCellStyleFont.setFontHeight(9);
        labelCellStyle.setFont(labelCellStyleFont);

        sheet.addMergedRegion(new CellRangeAddress(5, 6, 32, 32));
        XSSFCell nominalCell = workTimeRow.createCell(32);
        nominalCell.setCellStyle(labelCellStyle);
        nominalCell.setCellValue("czas pracy nominalny");

        sheet.addMergedRegion(new CellRangeAddress(5, 5, 33, 40));
        XSSFCell timeWorkedCell = workTimeRow.createCell(33);
        timeWorkedCell.setCellStyle(defaultStyle);
        timeWorkedCell.setCellValue("czas przepracowany");

        sheet.addMergedRegion(new CellRangeAddress(5, 5, 41, 51));
        XSSFCell timeNotWorkedCell = workTimeRow.createCell(41);
        timeNotWorkedCell.setCellStyle(defaultStyle);
        timeNotWorkedCell.setCellValue("czas nieprzepracowany");

        for (int i = 0; i < labelStrings.length; ++i) {
            XSSFCell labelCell = labelsRow.createCell(33 + i);
            labelCell.setCellStyle(labelCellStyle);
            labelCell.setCellValue(labelStrings[i]);
        }

        // day and month labels
        XSSFRow daysInMonthRow = sheet.createRow(7);
        XSSFCell mcCell = daysInMonthRow.createCell(0);
        mcCell.setCellStyle(defaultStyle);
        mcCell.setCellValue("M-C");

        for (int i = 1; i <= 31; ++i) {
            XSSFCell dayInMonthCell = daysInMonthRow.createCell(i);
            dayInMonthCell.setCellStyle(defaultStyle);
            dayInMonthCell.setCellValue(i);
        }

        // ...then rest of the row with blanks
        for (int j = 32; j < 52; ++j) {
            XSSFCell dayInMonthCell = daysInMonthRow.createCell(j);
            dayInMonthCell.setCellStyle(defaultCenterStyle);
        }

        // for every month row till today
        int previousMonth = LocalDate.now().getMonthValue() - 1;
        for (int i = 0; i < previousMonth; ++i) {
            XSSFRow monthRow = sheet.createRow(8 + i);
            XSSFCell monthNumberCell = monthRow.createCell(0);
            monthNumberCell.setCellStyle(defaultStyle);
            monthNumberCell.setCellValue(romanNumerals[i]);

            // start filling day in month cells
            int monthLength = LocalDate.of(year, i + 1, 1).lengthOfMonth();
            for (int j = 1; j <= monthLength; ++j) {
                XSSFCell dayCell = monthRow.createCell(j);
                dayCell.setCellStyle(defaultCenterStyle);

                LocalDate currentDay = LocalDate.of(year, i + 1, j);
                if (!holidayService.isWorkingDay(currentDay)) {
                    dayCell.setCellValue("-");
                } else {
                    dayCell.setCellValue(worktime);
                }
            }

            // fill in rest of the days till 31th
            for (int j = monthLength + 1; j <= 31; ++j) {
                XSSFCell dayCell = monthRow.createCell(j);
                dayCell.setCellStyle(defaultCenterStyle);
                dayCell.setCellValue("-");
            }

            // ...then rest of the row with blanks
            for (int j = 32; j < 52; ++j) {
                XSSFCell dayCell = monthRow.createCell(j);
                dayCell.setCellStyle(defaultCenterStyle);
            }
        }

        // fill in rest of the months
        for (int i = previousMonth; i < 12; ++i) {
            XSSFRow monthRow = sheet.createRow(8 + i);
            XSSFCell monthNumberCell = monthRow.createCell(0);
            monthNumberCell.setCellStyle(defaultStyle);
            monthNumberCell.setCellValue(romanNumerals[i]);

            // blank cells
            for (int j = 1; j < 52; ++j) {
                XSSFCell dayCell = monthRow.createCell(j);
                dayCell.setCellStyle(defaultCenterStyle);
            }
        }

        // for every user holidays request
        if (requests != null) {
            for (Request r : requests) {
                boolean isAccepted = r.getStatus() == Request.Status.ACCEPTED;

                if (isAccepted) {
                    for (LocalDate k = r.getStartDate(); !k.isAfter(r.getEndDate()); k = k.plusDays(1)) {
                        if (k.getMonthValue() <= previousMonth && holidayService.isWorkingDay(k)) {
                            XSSFCell cell = sheet.getRow(7 + k.getMonthValue()).getCell(k.getDayOfMonth());
                            if (r.getType() == RequestType.NORMAL) {
                                cell.setCellValue("uw");
                            } else if (r.getType() == RequestType.OCCASIONAL) {
                                cell.setCellValue("uo");
                            }

                        }
                    }
                }
            }
        }

        XSSFRow summaryRow = sheet.createRow(20);
        summaryRow.setHeightInPoints(50);

        // entitled days/hours pool
        sheet.addMergedRegion(new CellRangeAddress(20, 20, 0, 5));
        XSSFCell entitledTimeCell = summaryRow.createCell(0);
        entitledTimeCell.setCellStyle(defaultCenterStyle);
        String entitledTimeString = (Math.abs(8f - worktime) < 0.1) ?
                "urlop wypocz.\nprzysług. dni" : "urlop wypocz.\nprzysług. godz.";
        entitledTimeCell.setCellValue(entitledTimeString);

        sheet.addMergedRegion(new CellRangeAddress(20, 20, 6, 7));
        XSSFCell entitledTimeValueCell = summaryRow.createCell(6);
        entitledTimeValueCell.setCellStyle(defaultCenterStyle);

        // used days/hours pool
        sheet.addMergedRegion(new CellRangeAddress(20, 20, 8, 13));
        XSSFCell usedTimeCell = summaryRow.createCell(8);
        usedTimeCell.setCellStyle(defaultCenterStyle);
        String usedTimeString = (Math.abs(8f - worktime) < 0.1) ?
                "wykorzystano dni" : "wykorzystano godz.";
        usedTimeCell.setCellValue(usedTimeString);

        sheet.addMergedRegion(new CellRangeAddress(20, 20, 14, 15));
        XSSFCell usedTimeValueCell = summaryRow.createCell(14);
        usedTimeValueCell.setCellStyle(defaultCenterStyle);

        // days/hours left
        sheet.addMergedRegion(new CellRangeAddress(20, 20, 16, 21));
        XSSFCell leftTimeCell = summaryRow.createCell(16);
        leftTimeCell.setCellStyle(defaultCenterStyle);
        String leftTimeString = (Math.abs(8f - worktime) < 0.1) ?
                "pozostało dni" : "pozostało godz.";
        leftTimeCell.setCellValue(leftTimeString);

        sheet.addMergedRegion(new CellRangeAddress(20, 20, 22, 23));
        XSSFCell leftTimeValueCell = summaryRow.createCell(22);
        leftTimeValueCell.setCellStyle(defaultCenterStyle);
        double leftTimeValueNumber = historyLogService.countRemainingHours(user.getId());
        leftTimeValueNumber = (Math.abs(8f - worktime) < 0.1) ? leftTimeValueNumber / 8 : leftTimeValueNumber;
        leftTimeValueCell.setCellValue(leftTimeValueNumber);

        // total label
        sheet.addMergedRegion(new CellRangeAddress(20, 20, 24, 31));
        XSSFCell totalCell = summaryRow.createCell(24);
        totalCell.setCellStyle(defaultCenterStyle);
        totalCell.setCellValue("ogółem");

        // fill in rest of this row
        for (int i = 0; i < 20; ++i) {
            XSSFCell sumCell = summaryRow.createCell(32 + i);
            sumCell.setCellStyle(defaultCenterStyle);
        }

        // calculate work time sum of a month
        for (int i = 0; i < previousMonth; ++i) {
            float sum = 0;
            XSSFRow sumRow = sheet.getRow(8 + i);
            for (int j = 1; j <= 31; ++j) {
                XSSFCell valueCell = sumRow.getCell(j);
                double cellValue;
                try {
                    cellValue = valueCell.getNumericCellValue();
                    sum += cellValue;
                } catch (Exception e) {
                }
            }
            XSSFCell sumCell = sumRow.getCell(32);
            sumCell.setCellValue(sum);
        }

        // calculate used holidays time
        for (int i = 0; i < previousMonth; ++i) {
            float time = (Math.abs(8f - worktime) < 0.1) ? user.getWorkTime() / 8 : user.getWorkTime();
            float uwSum = 0;
            float uoSum = 0;
            XSSFRow sumRow = sheet.getRow(8 + i);
            for (int j = 1; j <= 31; ++j) {
                XSSFCell valueCell = sumRow.getCell(j);
                String cellValue;
                try {
                    cellValue = valueCell.getStringCellValue();
                    if ("uw".equals(cellValue)) {
                        uwSum += time;
                    } else if ("uo".equals(cellValue)) {
                        uoSum += 1;
                    }
                } catch (Exception e) {
                }
            }
            XSSFCell uwCell = sumRow.getCell(41);
            uwCell.setCellValue(uwSum);
            XSSFCell uoCell = sumRow.getCell(42);
            uoCell.setCellValue(uoSum);
        }

        // total used holidays time
        XSSFRow totalUsedRow = sheet.getRow(20);
        float totalUsedUwSum = 0;
        float totalUsedUoSum = 0;
        for (int i = 0; i < previousMonth; ++i) {
            XSSFRow sumRow = sheet.getRow(8 + i);

            XSSFCell valueCell = sumRow.getCell(41);
            double cellValue;
            try {
                cellValue = valueCell.getNumericCellValue();
                totalUsedUwSum += cellValue;
            } catch (Exception e) {
            }

            valueCell = sumRow.getCell(42);
            try {
                cellValue = valueCell.getNumericCellValue();
                totalUsedUoSum += cellValue;
            } catch (Exception e) {
            }
        }
        XSSFCell totalUsedUwCell = totalUsedRow.getCell(41);
        totalUsedUwCell.setCellValue(totalUsedUwSum);
        XSSFCell totalUsedUoCell = totalUsedRow.getCell(42);
        totalUsedUoCell.setCellValue(totalUsedUoSum);
    }
}

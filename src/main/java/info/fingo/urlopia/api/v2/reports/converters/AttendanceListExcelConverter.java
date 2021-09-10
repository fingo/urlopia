package info.fingo.urlopia.api.v2.reports.converters;

import info.fingo.urlopia.api.v2.reports.converters.pdf.ExcelToPDFConverter;
import info.fingo.urlopia.api.v2.reports.converters.pdf.PDFGenerationSettings;
import info.fingo.urlopia.api.v2.reports.converters.pdf.rules.RowRule;
import info.fingo.urlopia.api.v2.reports.converters.pdf.rules.BlankRow;
import info.fingo.urlopia.api.v2.reports.converters.pdf.rules.ColspanRow;
import info.fingo.urlopia.api.v2.reports.converters.pdf.rules.HeaderRow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttendanceListExcelConverter {
    private static final int HEADER_FONT_SIZE = 14;

    public static void convertToPDF(Workbook workbook, OutputStream out) {
        convertToPDF(List.of(workbook), out);
    }

    public static void convertToPDF(List<Workbook> workbooks, OutputStream out) {
        var settings = PDFGenerationSettings.defaultSettings();
        var rules = conversionRules();
        var converter = new ExcelToPDFConverter(settings, rules);
        converter.convertAndWrite(workbooks, out);
    }

    private static List<RowRule> conversionRules() {
        List<RowRule> rules = new LinkedList<>();

        rules.addAll(stylingRules());
        rules.addAll(summaryTableRules());

        return rules;
    }

    private static List<RowRule> stylingRules() {
        List<RowRule> rules = new LinkedList<>();

        rules.add(HeaderRow.when(row -> row.getRowNum() == 0, true, HEADER_FONT_SIZE));
        rules.add(HeaderRow.when(row -> row.getRowNum() == 2, false, HEADER_FONT_SIZE));

        rules.add(BlankRow.after(row -> row.getRowNum() == 0));
        rules.add(BlankRow.after(row -> row.getRowNum() == 2));
        rules.add(BlankRow.after(row -> row.getRowNum() == 35));

        return rules;
    }

    private static List<RowRule> summaryTableRules() {
        var colspanRows = List.of(37, 38, 39, 40, 41, 42);
        var colspan = 2;

        return List.of(ColspanRow.when(row -> colspanRows.contains(row.getRowNum()), colspan));
    }
}

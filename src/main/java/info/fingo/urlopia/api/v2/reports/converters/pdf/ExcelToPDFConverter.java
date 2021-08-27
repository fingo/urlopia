package info.fingo.urlopia.api.v2.reports.converters.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import info.fingo.urlopia.api.v2.reports.converters.pdf.rules.RowRule;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.OutputStream;
import java.util.*;

public class ExcelToPDFConverter {
    private final ExcelToPDFConverterSettings settings;
    private final Map<RowRule.ApplyMethod, RowRules> rules;

    public ExcelToPDFConverter(ExcelToPDFConverterSettings settings, List<RowRule> rules) {
        this.settings = settings;
        this.rules = initializeRules(rules, settings);
    }

    private Map<RowRule.ApplyMethod, RowRules> initializeRules(List<RowRule> rules,
                                                               ExcelToPDFConverterSettings settings) {
        Map<RowRule.ApplyMethod, RowRules> result = new HashMap<>();

        for (var applyMethodType : RowRule.ApplyMethod.values()) {
            result.put(applyMethodType, RowRules.withSettings(settings));
        }
        rules.forEach(rule -> result.get(rule.getApplyMethod()).addRule(rule));

        return result;
    }

    public void convertAndWrite(Workbook workbook, OutputStream outputStream) {
        try {
            var pdf = createNewDocument(outputStream);
            pdf.open();

            for (var sheet : workbook) {
                var numberOfColumns = ExcelToPDFConverterUtils.maxNumberOfColumnsIn(sheet);
                var table = createNewTable(numberOfColumns);
                var font = settings.getFont();

                for (var row : sheet) {
                    processRow(row, table, font);
                }

                pdf.newPage();
                pdf.add(table);
            }

            pdf.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private Document createNewDocument(OutputStream outputStream) throws DocumentException {
        var pdf = new Document();
        PdfWriter.getInstance(pdf, outputStream);
        return pdf;
    }

    private PdfPTable createNewTable(int numOfColumns) {
        var table = new PdfPTable(numOfColumns);
        table.setWidthPercentage(settings.getWidthPercentage());
        return table;
    }

    private void processRow(Row row, PdfPTable table, Font font) {
        applyRulesOfType(RowRule.ApplyMethod.PRE, row, table);

        var shouldSkipCellConversion = applyRulesOfType(RowRule.ApplyMethod.REPLACE, row, table);

        if (!shouldSkipCellConversion) {
            for (var cell : row) {
                table.addCell(convertedCell(cell, font));
            }
        }

        applyRulesOfType(RowRule.ApplyMethod.POST, row, table);
    }

    private boolean applyRulesOfType(RowRule.ApplyMethod applyMethodType, Row row, PdfPTable table) {
        return rules.get(applyMethodType).applyTo(row, table);
    }

    private PdfPCell convertedCell(Cell cell, Font font) {
        var cellStringValue = ExcelToPDFConverterUtils.resolveCellValue(cell);
        var pdfPCell = new PdfPCell(new Phrase(cellStringValue, font));
        var cellAlignment = cell.getCellStyle().getAlignment();
        pdfPCell.setHorizontalAlignment(ExcelToPDFConverterUtils.mapHorizontalAlignment(cellAlignment));
        return pdfPCell;
    }
}

package info.fingo.urlopia.api.v2.reports.converters.pdf.rules;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import info.fingo.urlopia.api.v2.reports.converters.pdf.PDFGenerationSettings;
import info.fingo.urlopia.api.v2.reports.converters.pdf.ExcelToPDFConverterUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ColspanRow extends RowRule {
    private final int colspan;

    private ColspanRow(Predicate<Row> predicate, int colspan) {
        super(ApplyMethod.REPLACE, predicate);
        this.colspan = colspan;
    }

    public static ColspanRow when(Predicate<Row> predicate, int colspan) {
        return new ColspanRow(predicate, colspan);
    }

    @Override
    public void apply(Row row, PdfPTable table, PDFGenerationSettings settings) {
        var colspanCell = createColspanCell(row, settings);
        table.addCell(colspanCell);
        cellsAfterColspan(row, table.getNumberOfColumns(), settings).forEach(table::addCell);
    }

    private PdfPCell createColspanCell(Row row, PDFGenerationSettings settings) {
        var cell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        var cellValue = cell.getStringCellValue();
        var font = settings.getFont();

        var colspanCell = new PdfPCell(new Phrase(cellValue, font));
        colspanCell.setColspan(colspan);

        return colspanCell;
    }

    private List<PdfPCell> cellsAfterColspan(Row row, int numberOfColumns, PDFGenerationSettings settings) {
        var font = settings.getFont();
        return IntStream.range(colspan, numberOfColumns)
                .mapToObj(idx -> row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
                .map(cell -> mapCellToPdfCell(cell, font))
                .toList();
    }

    private PdfPCell mapCellToPdfCell(Cell cell, Font font) {
        var pdfCell = new PdfPCell(new Phrase(ExcelToPDFConverterUtils.resolveCellValue(cell), font));
        var cellAlignment = cell.getCellStyle().getAlignment();
        pdfCell.setHorizontalAlignment(ExcelToPDFConverterUtils.mapHorizontalAlignment(cellAlignment));
        return pdfCell;
    }
}

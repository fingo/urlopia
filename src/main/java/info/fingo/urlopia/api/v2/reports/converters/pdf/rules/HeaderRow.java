package info.fingo.urlopia.api.v2.reports.converters.pdf.rules;

import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import info.fingo.urlopia.api.v2.reports.converters.pdf.PDFGenerationSettings;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.Predicate;

public class HeaderRow extends RowRule {
    private final boolean withUnderline;
    private final int headerFontSize;

    private HeaderRow(Predicate<Row> predicate, boolean withUnderline, int headerFontSize) {
        super(ApplyMethod.REPLACE, predicate);
        this.withUnderline = withUnderline;
        this.headerFontSize = headerFontSize;
    }

    public static HeaderRow when(Predicate<Row> predicate, boolean withUnderline, int headerFontSize) {
        return new HeaderRow(predicate, withUnderline, headerFontSize);
    }

    @Override
    public void apply(Row row, PdfPTable table, PDFGenerationSettings settings) {
        var numberOfColumns = table.getNumberOfColumns();
        var headerRow = createHeaderCell(row, numberOfColumns, settings);
        table.addCell(headerRow);
    }

    private PdfPCell createHeaderCell(Row row, int numberOfColumns, PDFGenerationSettings settings) {
        var headerCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        var headerValue = headerCell.getStringCellValue();
        var font = settings.getFont();
        font.setSize(headerFontSize);
        var headerRow = new PdfPCell(new Phrase(headerValue, font));

        headerRow.setColspan(numberOfColumns);
        headerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerRow.setBorder(withUnderline ? Rectangle.BOTTOM : Rectangle.NO_BORDER);

        return headerRow;
    }
}

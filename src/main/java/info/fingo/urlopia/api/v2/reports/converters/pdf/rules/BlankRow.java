package info.fingo.urlopia.api.v2.reports.converters.pdf.rules;

import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import info.fingo.urlopia.api.v2.reports.converters.pdf.PDFGenerationSettings;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.Predicate;

public class BlankRow extends RowRule {

    private BlankRow(Predicate<Row> predicate) {
        super(ApplyMethod.POST, predicate);
    }

    public static BlankRow after(Predicate<Row> predicate) {
        return new BlankRow(predicate);
    }

    @Override
    public void apply(Row excelRow, PdfPTable table, PDFGenerationSettings settings) {
        var numberOfColumns = table.getNumberOfColumns();
        var blankRow = createBlankRowCell(numberOfColumns);
        table.addCell(blankRow);
    }

    private PdfPCell createBlankRowCell(int numberOfColumns) {
        var blankRow = new PdfPCell(new Phrase(" "));
        blankRow.setColspan(numberOfColumns);
        blankRow.setHorizontalAlignment(Element.ALIGN_CENTER);
        blankRow.setBorder(Rectangle.NO_BORDER);
        return blankRow;
    }
}

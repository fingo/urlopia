package info.fingo.urlopia.api.v2.reports.converters.pdf.rules;

import com.itextpdf.text.pdf.PdfPTable;
import info.fingo.urlopia.api.v2.reports.converters.pdf.ExcelToPDFConverterSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.Predicate;

@AllArgsConstructor
@Getter
public abstract class RowRule {
    public enum ApplyMethod {
        PRE,
        POST,
        REPLACE,
    }

    private ApplyMethod applyMethod;
    private Predicate<Row> predicate;

    public abstract void apply(Row row, PdfPTable table, ExcelToPDFConverterSettings settings);

    public boolean isApplicable(Row row) {
        return predicate.test(row);
    }
}

package info.fingo.urlopia.api.v2.reports.converters.pdf;

import com.itextpdf.text.Element;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ExcelToPDFConverterUtils {
    public static String resolveCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> mapNumericValueToString(String.valueOf(cell.getNumericCellValue()));
            default -> "";
        };
    }

    public static String mapNumericValueToString(String numericValue) {
        var result = numericValue;
        var idx = numericValue.indexOf('.');

        if (idx != -1) {
            var substr = numericValue.substring(idx + 1);
            if (!substr.matches("[1-9]+")) {
                result = numericValue.substring(0, idx);
            }
        }

        return result;
    }

    public static int maxNumberOfColumnsIn(Sheet sheet) {
        var spliterator = Spliterators.spliteratorUnknownSize(sheet.rowIterator(), Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false)
                .map(Row::getLastCellNum)
                .mapToInt(Short::intValue)
                .max()
                .orElse(0);
    }

    public static int mapHorizontalAlignment(HorizontalAlignment alignment) {
        return switch (alignment) {
            case LEFT -> Element.ALIGN_LEFT;
            case CENTER -> Element.ALIGN_CENTER;
            case RIGHT, GENERAL -> Element.ALIGN_RIGHT;
            default -> Element.ALIGN_UNDEFINED;
        };
    }
}

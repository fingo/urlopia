package info.fingo.urlopia.reports;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class XlsxTemplateResolver {

    private static final String TAG_PREFIX = "\\{";
    private static final String TAG_POSTFIX = "}";

    public void resolve(Workbook template,
                        Map<String, String> model) {
        template.forEach(sheet -> this.resolveSheet(sheet, model));
    }

    private void resolveSheet(Sheet sheet,
                              Map<String, String> model) {
        sheet.forEach(row -> this.resolveRow(row, model));
    }

    private void resolveRow(Row row,
                            Map<String, String> model) {
        row.forEach(cell -> this.resolveCell(cell, model));
    }

    private void resolveCell(Cell cell,
                             Map<String, String> model) {
        var cellValue = this.getCellValue(cell);
        if (cellValue.isEmpty()) {
            return;
        }

        String value = cellValue.get();
        for (Map.Entry<String, String> tag : model.entrySet()) {
            var tagName = tag.getKey();
            String tagValue = tag.getValue();
            value = value.replaceAll(TAG_PREFIX + tagName + TAG_POSTFIX, tagValue);
        }
        this.setCellValue(cell, value);
    }

    private Optional<String> getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case BLANK, STRING -> Optional.of(cell.getStringCellValue());
            case FORMULA -> Optional.of("=" + cell.getCellFormula());
            default -> Optional.empty();
        };
    }

    private void setCellValue(Cell cell,
                              String value) {
        if (value.startsWith("=")) {
            cell.setCellFormula(value.substring(1));
        } else if (this.isNumeric(value)) {
            value = value.replace(',', '.');
            cell.setCellValue(Double.parseDouble(value));
        } else {
            cell.setCellValue(value);
        }
    }

    public boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+([.,]\\d+)?");
    }

}

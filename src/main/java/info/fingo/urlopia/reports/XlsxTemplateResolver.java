package info.fingo.urlopia.reports;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class XlsxTemplateResolver {

    private static final String TAG_PREFIX = "\\{";
    private static final String TAG_POSTFIX = "}";

    public void resolve(Workbook template, Map<String, String> model) {
        template.forEach(sheet -> this.resolveSheet(sheet, model));
    }

    private void resolveSheet(Sheet sheet, Map<String, String> model) {
        sheet.forEach(row -> this.resolveRow(row, model));
    }

    private void resolveRow(Row row, Map<String, String> model) {
        row.forEach(cell -> this.resolveCell(cell, model));
    }

    private void resolveCell(Cell cell,Map<String, String> model) {
        Optional<String> cellValue = this.getCellValue(cell);
        if (!cellValue.isPresent()) {
            return;
        }

        String value = cellValue.get();
        for (Map.Entry<String, String> tag : model.entrySet()) {
            String tagName = tag.getKey();
            String tagValue = tag.getValue();
            value = value.replaceAll(TAG_PREFIX + tagName + TAG_POSTFIX, tagValue);
        }
        this.setCellValue(cell, value);
    }

    private Optional<String> getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case BLANK:
            case STRING:
                return Optional.of(cell.getStringCellValue());
            case FORMULA:
                return Optional.of("=" + cell.getCellFormula());
            default:
                return Optional.empty();
        }
    }

    private void setCellValue(Cell cell, String value) {
        if (value.startsWith("=")) {
            cell.setCellFormula(value.substring(1));
        } else if (this.isNumeric(value)) {
            value = value.replace(',', '.');
            cell.setCellValue(Double.valueOf(value));
        } else {
            cell.setCellValue(value);
        }
    }

    public boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+([.,]\\d+)?");
    }

}

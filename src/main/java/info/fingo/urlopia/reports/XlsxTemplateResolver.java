package info.fingo.urlopia.reports;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class XlsxTemplateResolver {
    private static final Pattern PARAMS_PATTERN = Pattern.compile("\\{[^{]*}");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+([.,]\\d+)?");
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
        value = getValueWithResolveParams(value,model);
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
        var matcher = NUMERIC_PATTERN.matcher(strNum);
        return matcher.matches();
    }


    private String getValueWithResolveParams(String value, Map<String, String> model){
        var matcher = PARAMS_PATTERN.matcher(value);
        int start = 0;
        while (matcher.find(start)) {
            var param = matcher.group();
            var paramWithoutParentheses  = param.substring(1,param.length()-1);
            start = matcher.start() + 1;
            if (model.containsKey(paramWithoutParentheses)){
                value = value.replace(param,model.get(paramWithoutParentheses));
            }
        }
        return value;
    }
}

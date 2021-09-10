package info.fingo.urlopia.api.v2.reports.converters.pdf;

import com.itextpdf.text.pdf.PdfPTable;
import info.fingo.urlopia.api.v2.reports.converters.pdf.rules.RowRule;
import org.apache.poi.ss.usermodel.Row;

import java.util.LinkedList;
import java.util.List;

public class RowRules {
    private final List<RowRule> rules;
    private final PDFGenerationSettings settings;

    private RowRules(PDFGenerationSettings settings) {
        this.rules = new LinkedList<>();
        this.settings = settings;
    }

    public static RowRules withSettings(PDFGenerationSettings settings) {
        return new RowRules(settings);
    }

    public void addRule(RowRule rule) {
        rules.add(rule);
    }

    public boolean applyTo(Row row, PdfPTable table) {
        var applicableRules = rulesApplicableTo(row);
        applicableRules.forEach(rule -> rule.apply(row, table, settings));
        return !applicableRules.isEmpty();
    }

    public List<RowRule> rulesApplicableTo(Row row) {
        return rules.stream()
                .filter(rule -> rule.isApplicable(row))
                .toList();
    }
}

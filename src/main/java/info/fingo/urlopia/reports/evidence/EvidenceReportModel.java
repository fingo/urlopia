package info.fingo.urlopia.reports.evidence;

import java.util.Collections;
import java.util.Map;

public class EvidenceReportModel {

    public static final String DATE_FORMATTING = "%02d_%02d";

    private Map<String, String> model;

    public EvidenceReportModel(Map<String, String> model) {
        this.model = Collections.unmodifiableMap(model);
    }

    public Map<String, String> getModel() {
        return this.model;
    }

    public String getValue(String key) {
        return this.model.get(key);
    }
}

package info.fingo.urlopia.reports.evidence;

import java.util.Collections;
import java.util.Map;

public class EvidenceReportModel {

    private Map<String, String> model;

    EvidenceReportModel(Map<String, String> model) {
        this.model = Collections.unmodifiableMap(model);
    }

    public Map<String, String> getModel() {
        return this.model;
    }

    public String getValue(String key) {
        return this.model.get(key);
    }
}

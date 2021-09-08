
package info.fingo.urlopia.api.v2.reports.attendance;

import java.util.Collections;
import java.util.Map;

public class MonthlyAttendanceListReportModel {
    private Map<String, String> model;

    MonthlyAttendanceListReportModel(Map<String, String> model) {
        this.model = Collections.unmodifiableMap(model);
    }

    public Map<String, String> getModel() {
        return this.model;
    }

    public String getValue(String key) {
        return this.model.get(key);
    }

}

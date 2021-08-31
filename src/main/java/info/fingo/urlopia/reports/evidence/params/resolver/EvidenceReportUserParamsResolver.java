package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EvidenceReportUserParamsResolver implements ParamResolver {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    private final User user;

    @Override
    public Map<String, String> resolve() {
        Map<String, String> params = new HashMap<>();

        params.put("firstName", user.getFirstName());
        params.put("lastName", user.getLastName());

        String workTime = DECIMAL_FORMAT.format(user.getWorkTime());
        params.put("workTime", workTime);

        return params;
    }
}

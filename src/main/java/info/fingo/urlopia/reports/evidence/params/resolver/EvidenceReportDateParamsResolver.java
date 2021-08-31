package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.reports.ParamResolver;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class EvidenceReportDateParamsResolver implements ParamResolver {
    private final int year;

    @Override
    public Map<String, String> resolve() {
        return Collections.singletonMap("year", String.valueOf(year));
    }
}

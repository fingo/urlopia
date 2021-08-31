package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.util.Map;

@RequiredArgsConstructor
public class EvidenceReportUsedTimeDuringTheYearParamsResolver implements ParamResolver {
    private static final String FULL_TIME_WORKER_SYMBOL = "dni";
    private static final String NOT_FULL_TIME_WORKER_SYMBOL = "godz";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(); // uses default JVM locale
    private static final double FULL_TIME_DIVIDER = 8.0f;

    private final User user;
    private final int year;
    private final HistoryLogService historyLogService;

    @Override
    public Map<String, String> resolve() {
        return Map.of("usedTimeDuringTheYear", resolveUsedTimeDuringYear(),
                "timeUnit", resolveTimeUnit());
    }

    private String resolveUsedTimeDuringYear() {
        var usedHours = historyLogService.countTheHoursUsedDuringTheYear(user.getId(), year);
        if (historyLogService.checkIfWorkedFullTimeForTheWholeYear(user.getId(), year)) {
            return DECIMAL_FORMAT.format(usedHours / FULL_TIME_DIVIDER);
        } else {
            return DECIMAL_FORMAT.format(usedHours);
        }
    }

    private String resolveTimeUnit() {
        if (historyLogService.checkIfWorkedFullTimeForTheWholeYear(user.getId(), year)){
            return FULL_TIME_WORKER_SYMBOL;
        }
        else {
            return NOT_FULL_TIME_WORKER_SYMBOL;
        }
    }
}

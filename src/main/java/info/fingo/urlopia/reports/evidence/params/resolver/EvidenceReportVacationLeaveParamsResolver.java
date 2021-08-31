package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.history.HistoryLog;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class EvidenceReportVacationLeaveParamsResolver implements ParamResolver {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(); // uses default JVM locale
    private static final double FULL_TIME_DIVIDER = 8.0f;

    private final User user;
    private final int year;
    private final HistoryLogService historyLogService;

    @Override
    public Map<String, String> resolve() {
        return Collections.singletonMap("remainingTimeAtYearStart", resolveRemainingHoursAtYearStart());
    }

    private String resolveRemainingHoursAtYearStart() {
        var previousYearRemainingHours = historyLogService
                .countRemainingHoursForYear(user.getId(), year - 1);
        var currentYearAddedHours = this.historyLogService
                .getFromYear(user.getId(), year).stream()
                .filter(historyLog -> historyLog.getRequest() == null)
                .map(HistoryLog::getHours)
                .filter(hours -> hours > 0)
                .reduce(Float::sum)
                .orElse(0f);
        var remainingHoursAtYearStart = previousYearRemainingHours + currentYearAddedHours;
        if (historyLogService.checkIfWorkedFullTimeForTheWholeYear(user.getId(), year)) {
            return DECIMAL_FORMAT.format(remainingHoursAtYearStart / FULL_TIME_DIVIDER);
        } else {
            return DECIMAL_FORMAT.format(remainingHoursAtYearStart);
        }
    }
}
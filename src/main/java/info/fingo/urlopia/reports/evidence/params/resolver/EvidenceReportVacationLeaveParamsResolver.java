package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.history.HistoryLog;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EvidenceReportVacationLeaveParamsResolver implements ParamResolver {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(); // uses default JVM locale
    private static final double FULL_TIME_DIVIDER = 8.0f;
    private static final String EC_LABEL = "urlop";
    private static final String B2B_LABEL = "przerwa";
    private static final int NUMBER_OF_MONTHS = 12;
    public static final String MONTH_FORMATTING = "month_%02d";

    private final User user;
    private final int year;
    private final HistoryLogService historyLogService;

    @Override
    public Map<String, String> resolve() {
        var resolve = resolveVacationLeaveUsedHours();
        resolve.put("remainingTimeAtYearStart", resolveRemainingHoursAtYearStart());
        resolve.put("label",resolveLabel());
        return resolve;
    }

    private String resolveLabel(){
        if (user.getEc()){
            return EC_LABEL ;
        }
        return B2B_LABEL;
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
        return formatResolvedHours(remainingHoursAtYearStart);
    }

    private Map<String,String> resolveVacationLeaveUsedHours(){
        var result = new HashMap<String, String>();
        for (int month = 1; month <= NUMBER_OF_MONTHS; month++) {
            var formattedValue = "0";
            if (monthIsInPast(month, year)){
                var usedHours = historyLogService.countUsedHoursInMonth(user.getId(), year, month);
                formattedValue = formatResolvedHours(usedHours);
            }
            var formattedKey = String.format(MONTH_FORMATTING, month);
            result.put(formattedKey, formattedValue);
        }
        return result;
    }

    private String formatResolvedHours(double resolvedValue) {
        if (historyLogService.checkIfWorkedFullTimeForTheWholeYear(user.getId(), year)) {
            return DECIMAL_FORMAT.format(resolvedValue / FULL_TIME_DIVIDER);
        } else {
            return DECIMAL_FORMAT.format(resolvedValue);
        }
    }

    private boolean monthIsInPast(Integer month,
                                  Integer year){
        var present = LocalDate.now();
        return (year == present.getYear() && month < present.getMonth().getValue()) || year < present.getYear();
    }
}

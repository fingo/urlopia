package info.fingo.urlopia.reports.evidence;

import info.fingo.urlopia.history.HistoryLog;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.user.User;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class EvidenceReportModelFactory {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(); // uses default JVM locale

    private final RequestService requestService;
    private final HolidayService holidayService;
    private final HistoryLogService historyLogService;

    public EvidenceReportModelFactory(RequestService requestService,
                                      HolidayService holidayService,
                                      HistoryLogService historyLogService) {
        this.requestService = requestService;
        this.holidayService = holidayService;
        this.historyLogService = historyLogService;
    }

    public EvidenceReportModel create(User user, int year) {
        Map<String, String> model = new HashMap<>();
        this.putAllWithPrefix(this.reportDateParams(year), model, "reportDate");
        this.putAllWithPrefix(this.userMetadataParams(user), model, "user");
        this.putAllWithPrefix(this.vacationLeaveParams(user, year), model, "vacationLeave");
        this.putAllWithPrefix(this.dayStatusesParams(user, year), model, "day");
        return new EvidenceReportModel(model);
    }

    private void putAllWithPrefix(Map<String, String> from, Map<String, String> to, String prefix) {
        from.forEach((key, value) -> to.put(prefix + "." + key, value));
    }

    private Map<String, String> reportDateParams(int year) {
        return Collections.singletonMap("year", String.valueOf(year));
    }

    private Map<String, String> userMetadataParams(User user) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("firstName", user.getFirstName());
        parameters.put("lastName", user.getLastName());
        parameters.put("workTime", DECIMAL_FORMAT.format(user.getWorkTime()));
        return Collections.unmodifiableMap(parameters);
    }

    private Map<String, String> vacationLeaveParams(User user, int year) {
        float remainingHoursAtYearStart = this.historyLogService.getFromYear(user.getId(), year).stream()
                .map(HistoryLog::getHours)
                .filter(hours -> hours > 0)
                .reduce(Float::sum)
                .orElse(0f) + this.historyLogService.countRemainingHoursForYear(user.getId(), year - 1);
        return Collections.singletonMap("remainingHoursAtYearStart", DECIMAL_FORMAT.format(remainingHoursAtYearStart));
    }

    private Map<String, String> dayStatusesParams(User user, int year) {
        Map<String, String> parameters = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                String dayStatus = this.getDayStatus(user, year, month, dayOfMonth);
                String paramName = String.format("%02d_%02d", month, dayOfMonth);
                parameters.put(paramName, dayStatus);
            }
        }
        return parameters;
    }

    private String getDayStatus(User user, int year, int month, int dayOfMonth) {
        LocalDate currentDate = LocalDate.now();
        if (year > currentDate.getYear()
                || (year == currentDate.getYear() && month >= currentDate.getMonthValue())) {
            return "";
        }

        try {
            LocalDate date = LocalDate.of(year, month, dayOfMonth);
            if (this.holidayService.isWorkingDay(date)) {
                return this.requestService.getByUserAndDate(user.getId(), date).stream()
                        .filter(req -> req.getStatus() == Request.Status.ACCEPTED)
                        .map(req -> this.getRequestStatus(req.getType()))
                        .findFirst()
                        .orElse(DECIMAL_FORMAT.format(user.getWorkTime()));
            }
        } catch (DateTimeException ignore) {
            // if day is not exists then default value
        }
        return "-";
    }

    private String getRequestStatus(RequestType requestType) {
        switch (requestType) {
            case NORMAL:
                return "uw";
            case OCCASIONAL:
                return "uo";
            default:
                return "undefined";
        }
    }

}

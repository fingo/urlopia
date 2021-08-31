package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.reports.evidence.EvidenceReportModel;
import info.fingo.urlopia.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceReportPresenceConfirmationTimeParamResolver implements ParamResolver {
    private final User user;
    private final int year;
    private final PresenceConfirmationService presenceConfirmationService;
    private final HolidayService holidayService;
    private final Function<PresenceConfirmation, LocalTime> toTimeFunction;


    public static EvidenceReportPresenceConfirmationTimeParamResolver ofStartTime(User user,
                                                                                  int year,
                                                                                  PresenceConfirmationService presenceConfirmationService,
                                                                                  HolidayService holidayService) {
        return new EvidenceReportPresenceConfirmationTimeParamResolver(user,
                year,
                presenceConfirmationService,
                holidayService,
                PresenceConfirmation::getStartTime);
    }


    public static EvidenceReportPresenceConfirmationTimeParamResolver ofEndTime(User user,
                                                                                int year,
                                                                                PresenceConfirmationService presenceConfirmationService,
                                                                                HolidayService holidayService) {
        return new EvidenceReportPresenceConfirmationTimeParamResolver(user,
                year,
                presenceConfirmationService,
                holidayService,
                PresenceConfirmation::getEndTime);
    }

    @Override
    public Map<String, String> resolve() {
        Map<String, String> parameters = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                var endTime = resolveSpecificDay(month, dayOfMonth);
                var paramName = String.format(EvidenceReportModel.DATE_FORMATTING, month, dayOfMonth);
                parameters.put(paramName, endTime);
            }
        }
        return parameters;
    }

    private String resolveSpecificDay(int month, int dayOfMonth) {
        var currentDate = LocalDate.now();
        if (year > currentDate.getYear()
                || (year == currentDate.getYear() && month >= currentDate.getMonthValue())) {
            return "";
        }
        try {
            var date = LocalDate.of(year, month, dayOfMonth);
            if (this.holidayService.isWorkingDay(date)) {
                return presenceConfirmationService
                        .getByUserAndDate(user.getId(), date)
                        .stream()
                        .map(toTimeFunction)
                        .map(this::formatTime)
                        .findFirst()
                        .orElse("-");
            }
        } catch (DateTimeException ignore) {
            // if day does not exist then default value
        }
        return "-";
    }

    private String formatTime(LocalTime localTime){
        var dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(dateTimeFormatter);
    }

}

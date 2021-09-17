package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.evidence.EvidenceReportModel;
import info.fingo.urlopia.reports.evidence.EvidenceReportPresenceConfirmationTimeDTO;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EvidenceReportPresenceConfirmationTimeParamResolver{
    private final User user;
    private final int year;
    private final PresenceConfirmationService presenceConfirmationService;
    private final HolidayService holidayService;



    public EvidenceReportPresenceConfirmationTimeParamResolve resolve() {
        Map<String, String> startTimeParameters = new HashMap<>();
        Map<String, String> endTimeParameters = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                var specificDayResolve = resolveSpecificDay(month, dayOfMonth);
                var paramName = String.format(EvidenceReportModel.DATE_FORMATTING, month, dayOfMonth);
                startTimeParameters.put(paramName, specificDayResolve.startTimeResolve());
                endTimeParameters.put(paramName,specificDayResolve.endTimeResolve());
            }
        }
        return new EvidenceReportPresenceConfirmationTimeParamResolve(startTimeParameters,endTimeParameters);
    }

    private EvidenceReportPresenceConfirmationTimeDTO resolveSpecificDay(int month, int dayOfMonth) {
        var currentDate = LocalDate.now();
        if (year > currentDate.getYear()
                || (year == currentDate.getYear() && month >= currentDate.getMonthValue())) {
            return EvidenceReportPresenceConfirmationTimeDTO.unspecified();
        }
        try {
            var date = LocalDate.of(year, month, dayOfMonth);
            if (this.holidayService.isWorkingDay(date)) {
                return presenceConfirmationService
                        .getByUserAndDate(user.getId(), date)
                        .stream()
                        .map(this::mapToDTO)
                        .findFirst()
                        .orElse(EvidenceReportPresenceConfirmationTimeDTO.absent());
            }
        } catch (DateTimeException ignore) {
            // if day does not exist then default value
        }
        return EvidenceReportPresenceConfirmationTimeDTO.absent();
    }

    private String formatTime(LocalTime localTime){
        var dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(dateTimeFormatter);
    }

    private EvidenceReportPresenceConfirmationTimeDTO mapToDTO(PresenceConfirmation presenceConfirmation){
        return new EvidenceReportPresenceConfirmationTimeDTO(formatTime(presenceConfirmation.getStartTime()),
                                                            formatTime(presenceConfirmation.getEndTime()));
    }

}

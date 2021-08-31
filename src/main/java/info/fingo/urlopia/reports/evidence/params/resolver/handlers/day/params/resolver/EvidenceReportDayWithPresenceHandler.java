package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;

@RequiredArgsConstructor
public class EvidenceReportDayWithPresenceHandler {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    private final PresenceConfirmationService presenceConfirmationService;

    public String handle(User user,
                         LocalDate date){
        var presenceConfirmation = presenceConfirmationService.getByUserAndDate(user.getId(),date);
        if(presenceConfirmation.isEmpty()){
            return "-";
        }
        return countWorkingHoursInDay(presenceConfirmation.get(0));
    }

        private String countWorkingHoursInDay(PresenceConfirmation presenceConfirmation){
        var startTime = presenceConfirmation.getStartTime();
        var endTime = presenceConfirmation.getEndTime();
        var workTime = Duration.between(startTime,endTime);
        var minutesInHour = 60.0;
        double workingHours = workTime.toHours() + (workTime.toMinutesPart()/minutesInHour);
        return DECIMAL_FORMAT.format(workingHours);
    }
}

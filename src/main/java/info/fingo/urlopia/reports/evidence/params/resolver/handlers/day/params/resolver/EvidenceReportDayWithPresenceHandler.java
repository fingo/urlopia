package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver;


import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
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
        var countHours = presenceConfirmationService.countWorkingHoursInDay(presenceConfirmation.get(0));
        return DECIMAL_FORMAT.format(countHours);
    }
}

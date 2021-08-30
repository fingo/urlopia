package info.fingo.urlopia.api.v2.calendar;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUserInformationOutput {
    private boolean isAbsent;
    private PresenceConfirmationOutput presenceConfirmation;
    private List<VacationHoursModificationOutput> vacationHoursModifications;
}

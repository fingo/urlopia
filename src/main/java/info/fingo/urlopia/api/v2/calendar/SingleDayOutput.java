package info.fingo.urlopia.api.v2.calendar;

import lombok.Data;

import java.util.List;

@Data
public class SingleDayOutput {
    private boolean isWorkingDay;
    private List<String> holidays;
    private List<AbsentUserOutput> absentUsers;
    private CurrentUserInformationOutput currentUserInformation;
}

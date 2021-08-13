package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.history.WorkTimeResponse;

public record VacationDaysOutput(int remainingDays,
                                 double remainingHours){

    public static VacationDaysOutput fromWorkTimeResponse(WorkTimeResponse workTimeResponse){
        return new VacationDaysOutput(workTimeResponse.getDays(),
                workTimeResponse.getHours());
    }
}

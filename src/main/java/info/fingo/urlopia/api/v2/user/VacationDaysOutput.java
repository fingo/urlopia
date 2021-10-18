package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.history.WorkTimeResponse;

public record VacationDaysOutput(int remainingDays,
                                 double remainingHours,
                                 float workTime){

    public static VacationDaysOutput fromWorkTimeResponse(WorkTimeResponse workTimeResponse){
        var isFullTime = workTimeResponse.getWorkTime() == 8;
        if (isFullTime) {
            return VacationDaysOutput.fullTime(workTimeResponse);
        }
        return VacationDaysOutput.partTime(workTimeResponse);
    }

    private static VacationDaysOutput fullTime(WorkTimeResponse workTimeResponse) {
        var days = workTimeResponse.getDays();
        var hours = workTimeResponse.getHours();
        var workTime = workTimeResponse.getWorkTime();
        return new VacationDaysOutput(days, hours, workTime);
    }

    private static VacationDaysOutput partTime(WorkTimeResponse workTimeResponse) {
        var hoursPool = workTimeResponse.getPool();
        var workTime = workTimeResponse.getWorkTime();
        return new VacationDaysOutput(0, hoursPool, workTime);
    }
}

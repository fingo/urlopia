package info.fingo.urlopia.api.v2.automatic.vacation.days.model;

public record AutomaticVacationDayDTO(Long userId,
                                      String userFullName,
                                      Float workTime,
                                      Double nextYearProposition,
                                      Integer nextYearDaysBase) {


    public static AutomaticVacationDayDTO from(AutomaticVacationDay automaticVacationDay){
        var userId = automaticVacationDay.getUser().getId();
        var userFullName = automaticVacationDay.getUser().getFullName();
        var workTime = automaticVacationDay.getUser().getWorkTime();
        var nextYearProposition = automaticVacationDay.getNextYearHoursProposition();
        var nextYearDaysBase = automaticVacationDay.getNextYearDaysBase();
        return new AutomaticVacationDayDTO(userId, userFullName, workTime, nextYearProposition, nextYearDaysBase);
    }
}

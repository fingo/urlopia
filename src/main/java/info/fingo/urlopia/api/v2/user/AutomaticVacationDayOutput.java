package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDay;

public record AutomaticVacationDayOutput(Double nextYearProposition,
                                         Integer nextYearDaysBase,
                                         Boolean isEc) {
    public static AutomaticVacationDayOutput from(AutomaticVacationDay automaticVacationDay){
        var nextYearProposition = automaticVacationDay.getNextYearHoursProposition();
        var nextYearDaysBase = automaticVacationDay.getNextYearDaysBase();
        var isEc = automaticVacationDay.getUser().getEc();
        return new AutomaticVacationDayOutput(nextYearProposition, nextYearDaysBase, isEc);
    }
}

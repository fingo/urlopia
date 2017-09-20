package info.fingo.urlopia.holidays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class WorkingDaysCalculator {

    private final HolidayService holidayService;

    @Autowired
    public WorkingDaysCalculator(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    public int calculate(LocalDate begin, LocalDate end) {
        int workingDays = 0;

        for (LocalDate date = begin; date.isBefore(end) || date.isEqual(end); date = date.plusDays(1)) {
            if (holidayService.isWorkingDay(date)) {
                workingDays++;
            }
        }

        return workingDays;
    }

}

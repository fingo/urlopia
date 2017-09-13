package info.fingo.urlopia.holidays;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class WorkDaysCalculator {

    private final HolidayRepository holidayRepository;

    public WorkDaysCalculator(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public int calculate(LocalDate begin, LocalDate end) {
        int workDays = 0;

        for (LocalDate date = begin; date.isBefore(end) || date.isEqual(end); date = date.plusDays(1)) {
            if (this.isWorkingDay(date)) {
                workDays++;
            }
        }

        return workDays;
    }

    private boolean isWorkingDay(LocalDate date) {
        return !this.isWeekend(date) & !this.isHoliday(date);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByDate(date);
    }

}

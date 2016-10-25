package info.fingo.urlopia.history;

import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.RequestDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;


/**
 * @author Józef Grodzicki
 */
public class DurationCalculator {

    private DurationCalculator() {
    }

    private static int calculateDays(RequestDTO request, HolidayService holidayService) {
        int workDays = 0;

        LocalDate endDate = request.getEndDate();
        for (LocalDate date = request.getStartDate(); date.isBefore(endDate) || date.isEqual(endDate); date = date.plusDays(1)) {
            if (!isWeekend(date) && !isHoliday(date, holidayService))
                ++workDays;
        }

        return workDays;
    }

    public static float calculate(RequestDTO request, HolidayService holidayService) {
        return (float) calculateDays(request, holidayService) * request.getRequester().getWorkTime();
    }

    public static boolean isWeekend(LocalDate localDate) {
        return localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    public static boolean isHoliday(LocalDate date, HolidayService holidayService) {
        List<LocalDate> holidays = holidayService.getAllHolidaysDates();

        return holidays.contains(date);
    }
}

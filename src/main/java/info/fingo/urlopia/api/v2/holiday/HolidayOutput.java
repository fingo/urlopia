package info.fingo.urlopia.api.v2.holiday;

import info.fingo.urlopia.holidays.Holiday;

import java.time.LocalDate;

public record HolidayOutput(Long id,
                            String name,
                            LocalDate date) {

    public static HolidayOutput fromHoliday(Holiday holiday){
        return  new HolidayOutput(holiday.getId(),
                holiday.getName(),
                holiday.getDate());
    }
}

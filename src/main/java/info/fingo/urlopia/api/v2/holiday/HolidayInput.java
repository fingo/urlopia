package info.fingo.urlopia.api.v2.holiday;

import info.fingo.urlopia.holidays.Holiday;

import java.time.LocalDate;
import java.util.List;

public record HolidayInput(LocalDate startDate,
                           LocalDate endDate,
                           List<Holiday> holidaysToSave) {
}

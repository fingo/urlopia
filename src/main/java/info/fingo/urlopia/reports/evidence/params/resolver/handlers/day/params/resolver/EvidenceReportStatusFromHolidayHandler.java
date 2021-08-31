package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver;

import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.holidays.HolidayService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EvidenceReportStatusFromHolidayHandler {
    private static final String FREE_FOR_SATURDAY_STATUS = "dwś";
    private final HolidayService holidayService;

    public String handle(Holiday holiday){
        if (checkIsFreeDayForHolidayOnSaturday(holiday)){
            return FREE_FOR_SATURDAY_STATUS;
        }
        else {
            return "-";
        }
    }

    private boolean checkIsFreeDayForHolidayOnSaturday(Holiday holiday){
        var holidayYear = holiday.getDate().getYear();
        var defaultHolidays = holidayService.generateHolidaysList(holidayYear);
        return defaultHolidays.stream()
                .noneMatch(defaultHoliday -> checkIsTheSameHolidays(holiday,defaultHoliday));
    }

    private boolean checkIsTheSameHolidays(Holiday holiday,Holiday defaultHoliday){
        return checkIsHolidaysOnTheSameDate(holiday,defaultHoliday)
                && checkIsHolidaysWithTheSameName(holiday,defaultHoliday);
    }

    private boolean checkIsHolidaysOnTheSameDate(Holiday holiday,Holiday defaultHoliday){
        var holidayDate = holiday.getDate();
        var defaultHolidayDate = defaultHoliday.getDate();
        return holidayDate.equals(defaultHolidayDate);
    }

    private boolean checkIsHolidaysWithTheSameName(Holiday holiday,Holiday defaultHoliday){
        var holidayName = holiday.getName();
        var defaultHolidayName = defaultHoliday.getName();
        return holidayName.equals(defaultHolidayName);
    }
}

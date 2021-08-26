package info.fingo.urlopia.api.v2.holiday;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.holidays.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v2/holidays")
@RequiredArgsConstructor
@Slf4j
public class HolidayControllerV2 {

    private final HolidayService holidayService;

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @GetMapping(produces= MediaType.APPLICATION_JSON_VALUE)
    public List<HolidayOutput> getAll(@RequestParam(name = "filter", defaultValue = "") String[] filters) {
        var filter = Filter.from(filters);
        var holidays =  holidayService.getAll(filter);
        return mapHolidaysListToHolidayOutputList(holidays);
    }


    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(value = "/default",
            produces= MediaType.APPLICATION_JSON_VALUE)
    public List<HolidayOutput> generateDefault(@RequestParam Integer year){
        var holidays =  holidayService.generateHolidaysList(year);
        return mapHolidaysListToHolidayOutputList(holidays);
    }


    @RolesAllowed("ROLES_ADMIN")
    @PutMapping(produces= MediaType.APPLICATION_JSON_VALUE)
    public List<HolidayOutput> save(@RequestBody HolidayInput holidaysInput) {
        var startDate = holidaysInput.startDate();
        var endDate = holidaysInput.endDate();
        var holidays = holidaysInput.holidaysToSave();
        for (var holiday: holidays) {
            var holidayDate = holiday.getDate();
            var isWithinRange = isWithinRange(holidayDate,startDate,endDate);
            if(!isWithinRange){
                log.error("Holidays are not in specified time period");
                throw HolidayOutsideSpecifiedRange.holidaysOutsideTimePeriod();
            }
        }
        holidayService.deleteBetweenDates(startDate,endDate);
        var createdHolidays = holidayService.saveHolidays(holidays);
        return mapHolidaysListToHolidayOutputList(createdHolidays);
    }


    private List<HolidayOutput> mapHolidaysListToHolidayOutputList(List<Holiday> holidays){
            return holidays.stream()
                    .map(HolidayOutput::fromHoliday)
                    .toList();
    }

    private boolean isWithinRange(LocalDate holidayDate,
                                  LocalDate startDate,
                                  LocalDate endDate) {
        return !(holidayDate.isBefore(startDate) || holidayDate.isAfter(endDate));
    }
}

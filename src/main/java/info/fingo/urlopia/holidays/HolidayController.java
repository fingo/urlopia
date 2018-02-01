package info.fingo.urlopia.holidays;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HolidayController {

    @Autowired
    HolidayService holidayService;

    @Autowired
    HolidayRepository holidayRepository;

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/holiday", method = RequestMethod.GET)
    public List<HolidayResponse> getAll(@RequestParam Integer year,
                                        @RequestParam(name = "filter", defaultValue = "") String[] filters) {
        Filter filter = Filter.from(filters);
        return holidayService.getAllHolidaysInYear(year, filter);
    }

    /**
     * It is allowed to save only holidays within one year at method call
     */
    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/holiday/save", method = RequestMethod.POST)
    public HttpStatus save(@RequestBody List<HolidayResponse> holidays) {
        int currentYear = Instant.ofEpochMilli(holidays.get(0).getDate()).atZone(ZoneId.systemDefault()).toLocalDate().getYear();

        for (HolidayResponse h: holidays) {
            if(Instant.ofEpochMilli(h.getDate()).atZone(ZoneId.systemDefault()).toLocalDate().getYear() != currentYear)
                return HttpStatus.EXPECTATION_FAILED;
        }

        holidayService.deleteYear(currentYear);
        holidayService.addHolidays(holidays);

        return HttpStatus.OK;
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/holiday/default", method = RequestMethod.GET)
    public List<HolidayResponse> generate(@RequestParam Integer year) {
        List<HolidayResponse> holidays = holidayService.generateHolidaysList(year).stream().map(HolidayResponse::new).collect(Collectors.toList());
        return holidays;
    }
}

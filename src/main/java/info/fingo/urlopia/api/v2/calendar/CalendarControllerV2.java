package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.config.authentication.AuthInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v2/calendar")
@RequiredArgsConstructor
public class CalendarControllerV2 {
    private final CalendarService calendarService;

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CalendarOutput getCalendarInformation(@RequestParam("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
                                                 @RequestParam("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate,
                                                 @RequestParam(name = "filter", defaultValue = "") String[] filters,
                                                 HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        var filter = Filter.from(filters);
        return calendarService.getCalendarInfo(authenticatedId, startDate, endDate, filter);
    }
}

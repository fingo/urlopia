package info.fingo.urlopia.api.v2.history;

import info.fingo.urlopia.config.authentication.UserIdInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2/absence-history")
@RequiredArgsConstructor
@Slf4j
public class HistoryLogControllerV2 {

    private final HistoryLogService historyLogService;

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @GetMapping(value="/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<HistoryLogOutput> getHistoryLogs(
            HttpServletRequest request,
            @RequestParam(required = false) Integer year,
            @RequestParam(name = "filter", defaultValue = "") String[] filters,
            Pageable pageable) {

        var authenticatedUserId = (Long) request.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        var filter = Filter.from(filters);
        var historyLogsPage = historyLogService.get(authenticatedUserId, year, filter, pageable);
        return historyLogsPage.map(HistoryLogOutput::from);
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(value="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<HistoryLogOutput> getSpecificHistoryLogs(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(name = "filter", defaultValue = "") String[] filters,
            Pageable pageable) {

        var filter = Filter.from(filters);
        var historyLogsPage = historyLogService.get(userId, year, filter, pageable);
        return historyLogsPage.map(HistoryLogOutput::from);
    }

    @RolesAllowed("ROLES_ADMIN")
    @PutMapping(value="/{logId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoryLogOutput updateCountingYearForLog(@RequestBody UpdateLogCountingYearInput updateLogCountingYear) {
       return historyLogService.updateCountingYear(updateLogCountingYear);
    }

    @RolesAllowed("ROLES_ADMIN")
    @PostMapping(value = "/details-change",  produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoryLogOutput addNewDetailsChangeEvent(@RequestBody DetailsChangeEventInput detailsChangeEventInput) {
        var historyLog = historyLogService.addNewDetailsChangeEvent(detailsChangeEventInput);
        return HistoryLogOutput.from(historyLog);
    }

}

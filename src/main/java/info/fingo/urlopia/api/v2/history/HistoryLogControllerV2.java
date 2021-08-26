package info.fingo.urlopia.api.v2.history;

import info.fingo.urlopia.config.authentication.AuthInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v2/absence-history")
@RequiredArgsConstructor
@Slf4j
public class HistoryLogControllerV2 {

    private final HistoryLogService historyLogService;

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @GetMapping(value="/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoryLogOutput> getHistoryLogs(
            HttpServletRequest request,
            @RequestParam(required = false) Integer year,
            @RequestParam(name = "filter", defaultValue = "") String[] filters) {

        var authenticatedUserId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        var filter = Filter.from(filters);
        var historyLog = historyLogService.get(authenticatedUserId, year, filter);
        Collections.reverse(historyLog);
        return HistoryLogOutput.from(historyLog);
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(value="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoryLogOutput> getSpecificHistoryLogs(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(name = "filter", defaultValue = "") String[] filters) {

        var filter = Filter.from(filters);
        var historyLog = historyLogService.get(userId, year, filter);
        Collections.reverse(historyLog);
        return HistoryLogOutput.from(historyLog);
    }

}

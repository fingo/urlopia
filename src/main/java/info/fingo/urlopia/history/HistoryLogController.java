package info.fingo.urlopia.history;

import info.fingo.urlopia.config.authentication.AuthInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/api/users/{userId}/days")
public class HistoryLogController {

    private final HistoryLogService historyService;

    @Autowired
    public HistoryLogController(HistoryLogService historyService) {
        this.historyService = historyService;
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getFromUser(@PathVariable Long userId,
                                            @RequestParam(required = false) Integer year,
                                            @RequestParam(name = "filter", defaultValue = "") String[] filters) {
        Filter filter = Filter.from(filters);
        List<HistoryLogExcerptProjection> historyLogs = historyService.get(userId, year, filter);
        return ResponseEntity.ok(historyLogs);
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@PathVariable Long userId,
                                    @RequestBody HistoryLogInput historyLog,
                                    HttpServletRequest httpRequest) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        historyService.create(historyLog, userId, authenticatedUserId);
        return ResponseEntity.ok().build();
    }

    // *** ACTIONS ***

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER"})
    @RequestMapping(value = "/remaining", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkTimeResponse> getRemainingDays(@PathVariable Long userId) {
        WorkTimeResponse response = historyService.countRemainingDays(userId);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/recent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getRecentLogs(@PathVariable Long userId) {
        List<HistoryLogExcerptProjection> histories = historyService.getRecent(userId);
        return ResponseEntity.ok(histories);
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @RequestMapping(value = "/employment-year", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getEmploymentYear(@PathVariable Long userId) {
        Integer employmentYear = historyService.getEmploymentYear(userId);
        return ResponseEntity.ok(employmentYear);
    }

}

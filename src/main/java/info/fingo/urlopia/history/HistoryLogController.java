package info.fingo.urlopia.history;

import info.fingo.urlopia.config.authentication.UserIdInterceptor;
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HistoryLogExcerptProjection>> getFromUser(@PathVariable Long userId,
                                            @RequestParam(required = false) Integer year,
                                            @RequestParam(name = "filter", defaultValue = "") String[] filters) {
        var filter = Filter.from(filters);
        var historyLogs = historyService.get(userId, year, filter);
        return ResponseEntity.ok(historyLogs);
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@PathVariable Long userId,
                                    @RequestBody HistoryLogInput historyLog,
                                    HttpServletRequest httpRequest) {
        var authenticatedUserId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        historyService.create(historyLog, userId, authenticatedUserId);
        return ResponseEntity.ok().build();
    }

    // *** ACTIONS ***

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER"})
    @GetMapping(value = "/remaining",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkTimeResponse> getRemainingDays(@PathVariable Long userId) {
        var response = historyService.countRemainingDays(userId);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @GetMapping(value = "/recent",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HistoryLogExcerptProjection>> getRecentLogs(@PathVariable Long userId) {
        var histories = historyService.getRecent(userId);
        return ResponseEntity.ok(histories);
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_LEADER", "ROLES_ADMIN"})
    @GetMapping(value = "/employment-year",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getEmploymentYear(@PathVariable Long userId) {
        var employmentYear = historyService.getEmploymentYear(userId);
        return ResponseEntity.ok(employmentYear);
    }

}

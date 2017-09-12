package info.fingo.urlopia.history;

import info.fingo.urlopia.authentication.AuthInterceptor;
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

    @RolesAllowed({"ROLES_LEADER", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getHistoryLogs(@PathVariable Long userId,
                                               @RequestParam(required = false) Integer year) {
        List<HistoryLogExcerptProjection> historyLogs = historyService.getHistoryLogs(userId, year);
        return ResponseEntity.ok(historyLogs);
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addHistoryLog(@PathVariable Long userId, @RequestBody HistoryLogInput historyLog,
                                              HttpServletRequest httpRequest) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        historyService.addLog(historyLog, userId, authenticatedUserId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/remaining", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkTimeResponse> getHolidaysPool(@PathVariable Long userId) {
        WorkTimeResponse response = historyService.countRemainingDays(userId);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/recent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getRecentHistoryFromUser(@PathVariable Long userId) {
        List<HistoryLogExcerptProjection> histories = historyService.getRecent(userId);
        return ResponseEntity.ok(histories);
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/employment-year", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getEmploymentYear(@PathVariable Long userId) {
        Integer employmentYear = historyService.getEmploymentYear(userId);
        return ResponseEntity.ok(employmentYear);
    }

}

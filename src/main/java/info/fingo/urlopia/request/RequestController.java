package info.fingo.urlopia.request;

import info.fingo.urlopia.config.authentication.AuthInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.request.normal.DayHourTime;
import info.fingo.urlopia.request.normal.NormalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RequestController {

    private final RequestService requestService;

    private final NormalRequestService normalRequestService;

    @Autowired
    public RequestController(RequestService requestService,
                             NormalRequestService normalRequestService) {
        this.requestService = requestService;
        this.normalRequestService = normalRequestService;
    }

    @RolesAllowed("ROLES_WORKER")
    @GetMapping(path = "/users/{userId}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RequestExcerptProjection>> getAllFromUser(@PathVariable Long userId,
                                                                         @RequestParam(name = "filter", defaultValue = "") String[] filters,
                                                                         Pageable pageable) {
        var filter = Filter.from(filters);
        var requestsPage = requestService.getFromUser(userId, filter, pageable);
        return ResponseEntity.ok(requestsPage);
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RequestExcerptProjection>> getAll(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                                                 Pageable pageable) {
        var filter = Filter.from(filters);
        var requestsPage = requestService.get(filter, pageable);
        return ResponseEntity.ok(requestsPage);
    }

    @RolesAllowed("ROLES_WORKER")
    @GetMapping(path = "/users/{userId}/requests/pendingTime", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DayHourTime> getPendingRequestsTime(@PathVariable Long userId) {
        var pendingRequestsTime = normalRequestService.getPendingRequestsTime(userId);
        return ResponseEntity.ok(pendingRequestsTime);
    }

    @RolesAllowed("ROLES_WORKER")
    @PostMapping(value = "/users/{userId}/requests", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@PathVariable Long userId, 
                                       @RequestBody RequestInput input) {
        requestService.create(userId, input);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_WORKER")
    @GetMapping(path = "/users/{userId}/teammates/vacation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VacationDay>> getTeammatesVacationsForNexTwoWeeks(@PathVariable Long userId) {
        var teammatesVacations = requestService.getTeammatesVacationsForNextTwoWeeks(userId);
        return ResponseEntity.ok(teammatesVacations);
    }

    // *** ACTIONS ***

    @RolesAllowed("ROLES_ADMIN")
    @PostMapping(path = "/requests/{requestId}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> accept(@PathVariable Long requestId, 
                                       HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.accept(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_ADMIN")
    @PostMapping(path = "/requests/{requestId}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@PathVariable Long requestId) {
        requestService.reject(requestId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @PostMapping(path = "/requests/{requestId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancel(@PathVariable Long requestId, 
                                       HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.cancel(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

}

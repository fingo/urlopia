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
    @RequestMapping(path = "/users/{userId}/requests", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page> getAllFromUser(@PathVariable Long userId,
                                               @RequestParam(name = "filter", defaultValue = "") String[] filters,
                                               Pageable pageable) {
        Filter filter = Filter.from(filters);
        Page<RequestExcerptProjection> requests = requestService.getFromUser(userId, filter, pageable);
        return ResponseEntity.ok(requests);
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page> getAll(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                       Pageable pageable) {
        Filter filter = Filter.from(filters);
        Page<RequestExcerptProjection> requests = requestService.get(filter, pageable);
        return ResponseEntity.ok(requests);
    }

    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(path = "/users/{userId}/requests/pendingTime",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DayHourTime> getPendingRequestsTime(@PathVariable Long userId) {
        DayHourTime pendingRequestsTime = normalRequestService.getPendingRequestsTime(userId);
        return ResponseEntity.ok(pendingRequestsTime);
    }

    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(value = "/users/{userId}/requests", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@PathVariable Long userId,
                                       @RequestBody RequestInput input) {
        requestService.create(userId, input);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(path = "/users/{userId}/teammates/vacation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getTeammatesVacationsForNexTwoWeeks(@PathVariable Long userId) {
        List<VacationDay> teammatesVocations = this.requestService.getTeammatesVacationsForNexTwoWeeks(userId);
        return ResponseEntity.ok(teammatesVocations);
    }

    // *** ACTIONS ***

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests/{requestId}/accept", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> accept(@PathVariable Long requestId,
                                       HttpServletRequest httpRequest) {
        Long authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.accept(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests/{requestId}/reject", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@PathVariable Long requestId) {
        requestService.reject(requestId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @RequestMapping(path = "/requests/{requestId}/cancel", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancel(@PathVariable Long requestId,
                                       HttpServletRequest httpRequest) {
        Long authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.cancel(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

}

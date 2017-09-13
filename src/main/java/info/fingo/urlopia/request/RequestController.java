package info.fingo.urlopia.request;

import info.fingo.urlopia.authentication.AuthInterceptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class RequestController {

    private final RequestServiceX requestService;

    public RequestController(RequestServiceX requestService) {
        this.requestService = requestService;
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(Pageable pageable) {
        Page<RequestExcerptProjection> requests = requestService.get(pageable);
        return ResponseEntity.ok(requests);
    }

    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(path = "/users/{userId}/requests", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page> getFromUser(@PathVariable Long userId, Pageable pageable) {
        Page<RequestExcerptProjection> requests = requestService.get(userId, pageable);
        return ResponseEntity.ok(requests);
    }

    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(value = "/users/{userId}/requests", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@PathVariable Long userId, @RequestBody RequestInput input) {
        requestService.create(userId, input);
        return ResponseEntity.ok().build();
    }

    // *** ACTIONS ***

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests/{requestId}/accept", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> accept(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.accept(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests/{requestId}/reject", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.reject(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/requests/{requestId}/cancel", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancel(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long authenticatedId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        requestService.cancel(requestId, authenticatedId);
        return ResponseEntity.ok().build();
    }

}

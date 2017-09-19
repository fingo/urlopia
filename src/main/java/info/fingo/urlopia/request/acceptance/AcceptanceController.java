package info.fingo.urlopia.request.acceptance;

import info.fingo.urlopia.authentication.AuthInterceptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class AcceptanceController {

    private final AcceptanceService acceptanceService;

    public AcceptanceController(AcceptanceService acceptanceService) {
        this.acceptanceService = acceptanceService;
    }

    @RolesAllowed("ROLES_LEADER")
    @RequestMapping(path = "/users/{userId}/acceptances", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page> getForLeader(HttpServletRequest httpRequest, Pageable pageable) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        Page<AcceptanceExcerptProjection> acceptances = acceptanceService.get(userId, pageable);
        return ResponseEntity.ok(acceptances);
    }

    // *** ACTIONS ***

    @RolesAllowed("ROLES_LEADER")
    @RequestMapping(path = "/acceptances/{acceptanceId}/accept", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> accept(@PathVariable Long acceptanceId) {
        acceptanceService.accept(acceptanceId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_LEADER")
    @RequestMapping(path = "/acceptances/{acceptanceId}/reject", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@PathVariable Long acceptanceId) {
        acceptanceService.reject(acceptanceId);
        return ResponseEntity.ok().build();
    }

}

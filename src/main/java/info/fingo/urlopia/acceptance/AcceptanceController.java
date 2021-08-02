package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api")
public class AcceptanceController {

    private final AcceptanceService acceptanceService;

    public AcceptanceController(AcceptanceService acceptanceService) {
        this.acceptanceService = acceptanceService;
    }

    @RolesAllowed("ROLES_LEADER")
    @GetMapping(path = "/users/{userId}/acceptances", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AcceptanceExcerptProjection>> getForLeader(@PathVariable Long userId,
                                             @RequestParam(name = "filter", defaultValue = "") String[] filters,
                                             Pageable pageable) {
        var filter = Filter.from(filters);
        Page<AcceptanceExcerptProjection> acceptances = acceptanceService.get(userId, filter, pageable);
        return ResponseEntity.ok(acceptances);
    }

    // *** ACTIONS ***

    @RolesAllowed("ROLES_LEADER")
    @PostMapping(path = "/acceptances/{acceptanceId}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> accept(@PathVariable Long acceptanceId) {
        acceptanceService.accept(acceptanceId);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed("ROLES_LEADER")
    @PostMapping(path = "/acceptances/{acceptanceId}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@PathVariable Long acceptanceId) {
        acceptanceService.reject(acceptanceId);
        return ResponseEntity.ok().build();
    }

}

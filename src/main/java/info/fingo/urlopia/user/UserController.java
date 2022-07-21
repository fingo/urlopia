package info.fingo.urlopia.user;

import info.fingo.urlopia.ActiveDirectorySynchronizationScheduler;
import info.fingo.urlopia.config.authentication.UserIdInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;
    private final ActiveDirectorySynchronizationScheduler synchronizer;

    public UserController(UserService userService,
                          ActiveDirectorySynchronizationScheduler synchronizer) {
        this.userService = userService;
        this.synchronizer = synchronizer;
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserExcerptProjection>> getAll(
            @RequestParam(name = "filter", defaultValue = "") String[] filters,
            Sort sort) {
        var filter = Filter.from(filters);
        var users = userService.get(filter, sort);
        return ResponseEntity.ok(users);
    }

    // *** ACTIONS ***
    @RolesAllowed("ROLES_ADMIN")
    @PostMapping("{userId}/setWorkTime")
    public ResponseEntity<Void> setWorkTime(@PathVariable Long userId,
                                            @RequestBody Map<String, Object> data) {
        var workTime = (String)data.get("workTime");
        userService.setWorkTime(userId, workTime);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @GetMapping(value = "/contract", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isEC (Long userId) {
        return ResponseEntity.ok(userService.isEC(userId));
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @PostMapping(value="/language", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> setLanguage(HttpServletRequest httpRequest,
                                            @RequestBody Map<String, String> data) {
        var userId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        var language = data.get("language");
        userService.setLanguage(userId, language);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @PostMapping(value="/synchronize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> synchronize() {
        synchronizer.fullSynchronization();
        return ResponseEntity.ok().build();
    }
}

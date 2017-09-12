package info.fingo.urlopia.user;

import info.fingo.urlopia.ActiveDirectorySynchronizationScheduler;
import info.fingo.urlopia.authentication.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserServiceX userService;
    private final ActiveDirectorySynchronizationScheduler synchronizer;

    @Autowired
    public UserController(UserServiceX userService, ActiveDirectorySynchronizationScheduler synchronizer) {
        this.userService = userService;
        this.synchronizer = synchronizer;
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getAll() {
        List<UserExcerptProjection> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value ="{userId}/setWorkTime", method = RequestMethod.POST)
    public ResponseEntity<Void> setWorkTime(@PathVariable Long userId, @RequestBody Map<String, Object> data) {
        String workTime = (String)data.get("workTime");
        userService.setWorkTime(userId, workTime);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/contract", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isEC (Long userId) {
        return ResponseEntity.ok(userService.isEC(userId));
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/language", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> setLanguage(HttpServletRequest httpRequest, @RequestBody Map<String, String> data) {
        long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        String language = data.get("language");
        userService.setLanguage(userId, language);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/synchronize", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> synchronize() {
        synchronizer.dailySynchronization();
        return ResponseEntity.ok().build();
    }
}

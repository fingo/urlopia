package info.fingo.urlopia.user;

import info.fingo.urlopia.authentication.AuthInterceptor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/users")
public class UserControllerX {

    private final UserServiceX userService;

    public UserControllerX(UserServiceX userService) {
        this.userService = userService;
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getAll() {
        List<UserExcerptProjection> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/contract", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean isEC (Long userId) {
        return userService.isEC(userId);
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/language", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setLanguage(HttpServletRequest httpRequest, @RequestBody Map<String, String> data) {
        long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        String language = data.get("language");
        userService.setLanguage(userId, language);
    }
}

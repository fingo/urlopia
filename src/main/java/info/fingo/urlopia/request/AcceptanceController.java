package info.fingo.urlopia.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomasz Urbas
 */

@RestController
public class AcceptanceController {

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private RequestService requestService;

    @RolesAllowed({"ROLES_LEADER", "ROLES_ADMIN"})
    @RequestMapping(value = "/api/acceptance/action", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> acceptanceAction(HttpServletRequest httpRequest, @RequestBody Map<String, String> data) {
        long userId = (Long) httpRequest.getAttribute("userId");
        long acceptanceId = Long.parseLong(data.get("acceptanceId"));
        String action = data.get("action");

        Map<String, Object> map = new HashMap<>();

        if ("accept".equals(action) && !requestService.isValidRequest(acceptanceId)) {
            acceptanceService.reject(acceptanceId, userId);
            map.put("value", false);
            return map;
        }

        if ("accept".equals(action)) {
            acceptanceService.accept(acceptanceId, userId);
        } else if ("reject".equals(action)) {
            acceptanceService.reject(acceptanceId, userId);
        }

        map.put("value", true);
        return map;

    }
}
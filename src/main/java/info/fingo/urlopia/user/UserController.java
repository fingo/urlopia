package info.fingo.urlopia.user;

import info.fingo.urlopia.authentication.AuthInterceptor;
import info.fingo.urlopia.history.HistoryDTO;
import info.fingo.urlopia.history.HistoryResponse;
import info.fingo.urlopia.history.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tomasz Urbas
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private HistoryService historyService;

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/synchronize", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void synchronize() {
        service.synchronize();
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/language", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void setLanguage(HttpServletRequest httpRequest, @RequestBody Map<String, String> data) {
        long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        String language = data.get("language");

        service.setLanguage(userId, language);
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoryResponse> getUserHistory(String userMail, int year) {
        UserDTO user = service.getUser(userMail);
        List<HistoryDTO> histories = historyService.getUserHistoriesFromYear(user.getId(), year);

        return histories.stream()
                .map(HistoryResponse::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/contract", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean isEC (Long userId) {
        return service.getUser(userId).isEC();
    }

}

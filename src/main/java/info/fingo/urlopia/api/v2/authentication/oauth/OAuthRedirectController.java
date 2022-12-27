package info.fingo.urlopia.api.v2.authentication.oauth;

import info.fingo.urlopia.config.authentication.UserData;
import info.fingo.urlopia.config.authentication.UserIdInterceptor;
import info.fingo.urlopia.history.HistoryLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path="/api/v2/user-details")
@RequiredArgsConstructor
public class OAuthRedirectController {

    private final OAuthRedirectService oAuthRedirectService;
    private final HistoryLogService historyLogService;

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @GetMapping()
    public UserData getAuthenticatedUserData(HttpServletRequest httpRequest){
        var authenticatedId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        var userData = oAuthRedirectService.getUserData(authenticatedId);

        var userEmploymentYear = historyLogService.getEmploymentYear(authenticatedId);
        userData.setEmploymentYear(userEmploymentYear);
        return userData;
    }
}

package info.fingo.urlopia.api.v2.authentication.noauth;

import info.fingo.urlopia.api.v2.authentication.oauth.OAuthRedirectService;
import info.fingo.urlopia.api.v2.user.UserOutput;
import info.fingo.urlopia.config.authentication.UserData;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.user.UserExcerptProjection;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v2/noauth/session")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "false")
public class NoAuthSessionController {

    private final OAuthRedirectService oAuthRedirectService;
    private final HistoryLogService historyLogService;
    private final UserService userService;

    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @GetMapping()
    public UserData getUserData(Long userId) {
        var userData = oAuthRedirectService.getUserData(userId);
        var userEmploymentYear = historyLogService.getEmploymentYear(userData.getUserId());
        userData.setEmploymentYear(userEmploymentYear);
        return userData;
    }

    public List<UserOutput> getAll(Sort sort) {
        var users = userService.get(Filter.empty(), sort);
        return mapUserProjectionListToUserOutputList(users);
    }

    private List<UserOutput> mapUserProjectionListToUserOutputList(List<UserExcerptProjection> users){
        return users.stream()
                .map(UserOutput::fromUserExcerptProjection)
                .toList();
    }
}

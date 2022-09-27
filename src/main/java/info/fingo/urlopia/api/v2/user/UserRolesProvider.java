package info.fingo.urlopia.api.v2.user;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.user.NoSuchUserException;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRolesProvider {

    private final AcceptanceService acceptanceService;

    public Set<String> getRolesFromUser(User user){
        var roles = new HashSet<String>();
        if (!user.isActive()){
            throw NoSuchUserException.inactiveAccount(user.getMail());
        }
        roles.add(User.Role.WORKER.toString());
        if (user.getLeader() || acceptanceService.hasActiveAcceptances(user)) {
            roles.add(User.Role.LEADER.toString());
        }
        if (user.getAdmin()) {
            roles.add(User.Role.ADMIN.toString());
        }
        return roles;
    }
}

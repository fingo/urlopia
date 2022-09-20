package info.fingo.urlopia.api.v2.oauth;

import info.fingo.urlopia.api.v2.user.UserRolesProvider;
import info.fingo.urlopia.config.authentication.TeamInfo;
import info.fingo.urlopia.config.authentication.UserData;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthRedirectService {

    public static final String BEARER_PREFIX = "Bearer ";
    private final UserService userService;
    private final UserRolesProvider userRolesProvider;


    public UserData getUserData(Long userId) {
            var user = userService.get(userId);
            var teams = pickTeamsInfo(user);
            var roles = userRolesProvider.getRolesFromUser(user);
            return UserData.from(user, teams, roles);
    }

    private Set<TeamInfo> pickTeamsInfo(User user) {
        Set<TeamInfo> teams = new HashSet<>();
        for (var team : user.getTeams()) {
            var teamName = team.getName();
            var allUsersLeader = userService.getAllUsersLeader();
            var leader = user.equals(team.getLeader()) ? allUsersLeader : team.getLeader();

            if (leader != null) {
                var leaderName = leader.getFirstName() + " " + leader.getLastName();
                var teamInfo = new TeamInfo(teamName, leaderName);
                teams.add(teamInfo);
            }
        }
        return teams;
    }

}

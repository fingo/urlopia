package info.fingo.urlopia.config.authentication;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import info.fingo.urlopia.user.UserService;
import info.fingo.urlopia.user.WrongCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final LDAPConnectionService ldapConnectionService;

    private final UserRepository userRepository;

    private final WebTokenService webTokenService;

    private final BuildProperties buildProperties;

    private final GitProperties gitProperties;

    private final UserService userService;

    private final AcceptanceService acceptanceService;

    public UserData authenticate(Credentials credentials) {
        if (ldapConnectionService.authenticate(credentials)) {
            var user = userRepository
                    .findFirstByMail(credentials.getMail())
                    .orElseThrow(() -> {
                        log.error("Authentication failed for user: {}", Anonymizer.anonymizeMail(credentials.getMail()));
                        return WrongCredentialsException.invalidCredentials();
                    });
            var roles = pickRoles(user);

            var userData = new UserData(user.getId(), roles);
            userData.setName(user.getFirstName());
            userData.setSurname(user.getLastName());
            userData.setMail(user.getMail());
            userData.setLanguage(user.getLang());
            userData.setTeams(this.pickTeamsInfo(user));
            userData.setIsEc(user.getEc());
            userData.setToken(webTokenService.generateWebToken(user.getId(), roles));
            var loggerInfo = "User: %s successfully authenticated"
                    .formatted(Anonymizer.anonymizeMail(credentials.getMail()));
            log.info(loggerInfo);
            return userData;
        }
        var loggerInfo = "Authentication failed for user: %s"
                .formatted(Anonymizer.anonymizeMail(credentials.getMail()));
        log.error(loggerInfo);
        throw WrongCredentialsException.invalidCredentials();
    }

    private List<String> pickRoles(User user) {
        List<String> roles = new ArrayList<>();
        roles.add(User.Role.WORKER.toString());
        if (user.getLeader() || acceptanceService.hasActiveAcceptances(user)) {
            roles.add(User.Role.LEADER.toString());
        }
        if (user.getAdmin()) {
            roles.add(User.Role.ADMIN.toString());
        }
        return roles;
    }

    private Set<Map<String, String>> pickTeamsInfo(User user) {
        Set<Map<String, String>> teams = new HashSet<>();
        for (var team : user.getTeams()) {
            var teamName = team.getName();
            var allUsersLeader = userService.getAllUsersLeader();
            var leader = user.equals(team.getLeader()) ? allUsersLeader : team.getLeader();

            if (leader != null) {
                Map<String, String> teamInfo = new HashMap<>();
                teamInfo.put("name", teamName);
                teamInfo.put("leader", leader.getFirstName() + " " + leader.getLastName());
                teams.add(teamInfo);
            }
        }
        return teams;
    }


    public String getAppVersion() {
        var dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());
        var version = buildProperties.getVersion();
        var commitId = gitProperties.getCommitId().substring(0, 6);
        var buildTime = dateTimeFormatter.format(buildProperties.getTime());
        return version + "-" + commitId + " (" + buildTime + ")";
    }

}

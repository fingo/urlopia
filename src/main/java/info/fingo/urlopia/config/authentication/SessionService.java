package info.fingo.urlopia.config.authentication;

import info.fingo.urlopia.team.Team;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionService.class);

    private final LDAPConnectionService ldapConnectionService;

    private final UserRepository userRepository;

    private final WebTokenService webTokenService;

    private final BuildProperties buildProperties;

    private final GitProperties gitProperties;

    @Autowired
    public SessionService(LDAPConnectionService ldapConnectionService,
                          UserRepository userRepository,
                          WebTokenService webTokenService,
                          BuildProperties buildProperties,
                          GitProperties gitProperties) {
        this.ldapConnectionService = ldapConnectionService;
        this.userRepository = userRepository;
        this.webTokenService = webTokenService;
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
    }

    public UserData authenticate(Credentials credentials) {
        if (ldapConnectionService.authenticate(credentials)) {
            var user = userRepository.findFirstByMail(credentials.getMail());
            var roles = this.pickRoles(user);
            var userData = new UserData(user.getId(), roles);
            userData.setName(user.getFirstName());
            userData.setSurname(user.getLastName());
            userData.setMail(user.getMail());
            userData.setLanguage(user.getLang());
            userData.setTeams(this.pickTeamsInfo(user));
            userData.setToken(webTokenService.generateWebToken(user.getId(), roles));
            return userData;
        }
        LOGGER.info("Authentication failed for user " + credentials.getMail());
        throw new RuntimeException("authentication failed");
    }

    private List<String> pickRoles(User user) {
        List<String> roles = new ArrayList<>();
        roles.add(User.Role.WORKER.toString());
        if (user.getLeader()) {
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
            var leader = user.equals(team.getLeader()) ? team.getBusinessPartLeader() : team.getLeader();

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

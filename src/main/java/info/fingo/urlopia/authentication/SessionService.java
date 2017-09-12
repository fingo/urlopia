package info.fingo.urlopia.authentication;

import info.fingo.urlopia.team.Team;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SessionService {
    private static final Logger LOGGER = Logger.getLogger(SessionService.class);

    private final LDAPConnectionService ldapConnectionService;

    private final UserRepository userRepository;

    private final WebTokenService webTokenService;

    private final BuildProperties buildProperties;

    private final GitProperties gitProperties;

    @Autowired
    public SessionService(LDAPConnectionService ldapConnectionService, UserRepository userRepository, WebTokenService webTokenService, BuildProperties buildProperties, GitProperties gitProperties) {
        this.ldapConnectionService = ldapConnectionService;
        this.userRepository = userRepository;
        this.webTokenService = webTokenService;
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
    }

    public UserData authenticate(Credentials credentials) {
        if (ldapConnectionService.authenticate(credentials)) {
            User user = userRepository.findFirstByMail(credentials.getMail());
            List<String> roles = this.pickRoles(user);
            UserData userData = new UserData(user.getId(), roles);
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
        roles.add(UserDTO.Role.WORKER.toString());
        if (user.getLeader()) {
            roles.add(UserDTO.Role.LEADER.toString());
        }
        if (user.getAdmin()) {
            roles.add(UserDTO.Role.ADMIN.toString());
        }
        return roles;
    }

    private Set<Map> pickTeamsInfo(User user) {
        Set<Map> teams = new HashSet();
        for (Team team : user.getTeams()) {
            Map<String, String> teamInfo = new HashMap<>();
            teamInfo.put("name", team.getName());
            teamInfo.put("leader",
                    String.format("%s %s", team.getLeader().getFirstName(), team.getLeader().getLastName()));
            teams.add(teamInfo);
        }
        return teams;
    }

    public String getAppVersion() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return buildProperties.getVersion() + "-" + gitProperties.getCommitId().substring(0,6)
                + " (" + formatter.format(buildProperties.getTime()) + ")";
    }

}

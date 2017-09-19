package info.fingo.urlopia.team;

import info.fingo.urlopia.ad.ActiveDirectory;
import info.fingo.urlopia.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.ad.Attribute;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TeamSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamSynchronizer.class);

    @Value("${ad.groups.users}")
    private String usersGroup;

    @Value("${ad.identifiers.team}")
    private String teamIdentifier;

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final ActiveDirectory activeDirectory;

    private final ActiveDirectoryTeamMapper teamMapper;

    private LocalDateTime lastModificationsCheck;

    @Autowired
    public TeamSynchronizer(TeamRepository teamRepository, UserRepository userRepository,
                            ActiveDirectory activeDirectory, ActiveDirectoryTeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activeDirectory = activeDirectory;
        this.teamMapper = teamMapper;
        this.lastModificationsCheck = LocalDateTime.now();
    }

    public void findNewTeams() {
        List<String> dbTeams = this.pickTeamsFromDatabase().stream()
                .map(Team::getAdName)
                .collect(Collectors.toList());
        this.pickTeamsFromActiveDirectory().stream()
                .filter(team -> !ActiveDirectoryUtils.pickAttribute(team, Attribute.DISTINGUISHED_NAME).equals(usersGroup))
                .filter(team -> !dbTeams.contains(ActiveDirectoryUtils.pickAttribute(team, Attribute.DISTINGUISHED_NAME)))
                .map(teamMapper::mapToTeam)
                .forEach(teamRepository::save);
        LOGGER.info("Synchronisation succeed: find new teams");
    }

    public void removeDeletedTeams() {
        List<String> adTeams = this.pickTeamsFromActiveDirectory().stream()
                .map(team -> ActiveDirectoryUtils.pickAttribute(team, Attribute.DISTINGUISHED_NAME))
                .collect(Collectors.toList());
        this.pickTeamsFromDatabase().stream()
                .filter(teams -> !adTeams.contains(teams.getAdName()))
                .forEach(teamRepository::delete);
        LOGGER.info("Synchronisation succeed: remove deleted teams");
    }

    public void checkModifications() {
        LocalDateTime checkTime = LocalDateTime.now();
        Stream<SearchResult> teamsToSynchronize = this.pickTeamsFromActiveDirectory().stream()
                .filter(team -> {
                    String changed = ActiveDirectoryUtils.pickAttribute(team, Attribute.CHANGED_TIME);
                    LocalDateTime changedTime = ActiveDirectoryUtils.convertToLocalDateTime(changed);
                    return changedTime.isAfter(this.lastModificationsCheck);
                });
        this.synchronize(teamsToSynchronize);
        this.lastModificationsCheck = checkTime;
        LOGGER.info("Synchronisation succeed: last modified teams");
    }

    public void fullSynchronize() {
        Stream<SearchResult> usersToSynchronize = this.pickTeamsFromActiveDirectory().stream();
        this.synchronize(usersToSynchronize);
        LOGGER.info("Synchronisation succeed: all teams");
    }

    private void synchronize(Stream<SearchResult> adTeams) {
        adTeams.forEach(adTeam -> {
            String adName = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME);
            Team team = teamRepository.findFirstByAdName(adName);
            if (team != null) {
                team = teamMapper.mapToTeam(adTeam, team);
                teamRepository.save(team);
            }
        });
    }

    public void assignUsersToTeams() {
        pickTeamsFromActiveDirectory().forEach(adTeam -> {
            String adName = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME);
            Team team = teamRepository.findFirstByAdName(adName);
            if (team != null) {
                String membersString = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.MEMBER);
                Set<User> members = this.splitMembers(membersString);
                team.setUsers(members);
                teamRepository.save(team);
            }
        });
        LOGGER.info("Synchronisation succeed: assign users to teams");
    }

    private Set<User> splitMembers(String members) {
        String[] groups = members.split(", (?=CN=)");
        return Arrays.stream(groups)
                .map(userRepository::findFirstByAdName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<SearchResult> pickTeamsFromActiveDirectory() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectory.ObjectClass.Group)
                .name(String.format("*%s", teamIdentifier))
                .search();
    }

    private List<Team> pickTeamsFromDatabase() {
        return teamRepository.findAll();
    }
}

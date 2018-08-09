package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ActiveDirectoryTeamSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryTeamSynchronizer.class);

    @Value("${ad.groups.users}")
    private String usersGroup;

    @Value("${ad.identifiers.team}")
    private String teamIdentifier;

    @Value("${ad.identifiers.business-part}")
    private String businessPartIdentifier;

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final ActiveDirectory activeDirectory;

    private final ActiveDirectoryTeamMapper teamMapper;

    @Autowired
    public ActiveDirectoryTeamSynchronizer(TeamRepository teamRepository, UserRepository userRepository,
                                           ActiveDirectory activeDirectory, ActiveDirectoryTeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activeDirectory = activeDirectory;
        this.teamMapper = teamMapper;
    }

    public void addNewTeams() {
        List<String> dbTeams = this.teamRepository.findAllAdNames();
        this.pickBusinessPartsFromAD().forEach(adBusinessPart ->
                this.pickTeamsFromAD(adBusinessPart).stream()
                        .filter(adTeam -> !ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME).equals(usersGroup))
                        .filter(adTeam -> !dbTeams.contains(ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME)))
                        .map(adTeam -> teamMapper.mapToTeam(adTeam, adBusinessPart))
                        .forEach(this.teamRepository::save));
        LOGGER.info("Synchronisation succeed: find new teams");
    }

    public void removeDeletedTeams() {
        List<String> adTeams = this.pickBusinessPartsFromAD().stream()
                .map(this::pickTeamsFromAD)
                .flatMap(Collection::stream)
                .map(team -> ActiveDirectoryUtils.pickAttribute(team, Attribute.DISTINGUISHED_NAME))
                .collect(Collectors.toList());

        this.teamRepository.findAll().stream()
                .filter(teams -> !adTeams.contains(teams.getAdName()))
                .forEach(this.teamRepository::delete);
        LOGGER.info("Synchronisation succeed: remove deleted teams");
    }

    public void synchronize() {
        this.pickBusinessPartsFromAD().forEach(adBusinessPart ->
                this.pickTeamsFromAD(adBusinessPart).forEach(adTeam -> {
                    String adName = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME);
                    Team team = this.teamRepository.findFirstByAdName(adName);
                    if (team != null) {
                        team = this.teamMapper.mapToTeam(adTeam, team, adBusinessPart);
                        this.teamRepository.save(team);
                    }
                })
        );
        LOGGER.info("Synchronisation succeed: update all teams");
    }

    public void assignUsersToTeams() {
        this.pickTeamsFromAD().forEach(adTeam -> {
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
        String[] groups = ActiveDirectoryUtils.split(members);
        return Arrays.stream(groups)
                .map(userRepository::findFirstByAdName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<SearchResult> pickTeamsFromAD() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectory.ObjectClass.Group)
                .name(String.format("*%s", teamIdentifier))
                .search();
    }

    private List<SearchResult> pickTeamsFromAD(SearchResult adBusinessPart) {
        String businessPartAdName = ActiveDirectoryUtils.pickAttribute(adBusinessPart, Attribute.DISTINGUISHED_NAME);
        return this.activeDirectory.newSearch()
                .objectClass(ActiveDirectory.ObjectClass.Group)
                .name("*" + this.teamIdentifier)
                .memberOf(businessPartAdName)
                .search();
    }

    private List<SearchResult> pickBusinessPartsFromAD() {
        return this.activeDirectory.newSearch()
                .objectClass(ActiveDirectory.ObjectClass.Group)
                .name("*" + this.businessPartIdentifier)
                .search();
    }

}

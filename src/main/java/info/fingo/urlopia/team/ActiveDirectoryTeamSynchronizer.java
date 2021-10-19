package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.ActiveDirectoryObjectClass;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private List<String> teamIdentifiers;

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ActiveDirectory activeDirectory;
    private final ActiveDirectoryTeamMapper teamMapper;

    public ActiveDirectoryTeamSynchronizer(TeamRepository teamRepository,
                                           UserRepository userRepository,
                                           ActiveDirectory activeDirectory,
                                           ActiveDirectoryTeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activeDirectory = activeDirectory;
        this.teamMapper = teamMapper;
    }

    public void addNewTeams() {
        var dbTeams = teamRepository.findAllAdNames();
        pickTeamsFromAD().stream()
                .filter(adTeam ->
                        !isTeamAUsersGroup(
                                adTeam))
                .filter(adTeam ->
                        !dbTeams.contains(
                                adNameOf(adTeam)))
                .map(adTeam -> teamMapper.mapToTeam(adTeam))
                .forEach(teamRepository::save);
        LOGGER.info("Synchronisation succeed: find new teams");
    }

    private boolean isTeamAUsersGroup(SearchResult adTeam) {
        return adNameOf(adTeam).equals(usersGroup);
    }

    private String adNameOf(SearchResult searchResult) {
        return ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.DISTINGUISHED_NAME);
    }

    public void removeDeletedTeams() {
        var adTeams = pickTeamsFromAD().stream()
                .map(this::adNameOf)
                .toList();

        teamRepository.findAll().stream()
                .filter(team -> !adTeams.contains(team.getAdName()))
                .forEach(teamRepository::delete);
        LOGGER.info("Synchronisation succeed: remove deleted teams");
    }

    public void synchronize() {
        pickTeamsFromAD().forEach(adTeam -> {
            var adName = adNameOf(adTeam);
            teamRepository.findFirstByAdName(adName).ifPresent(team -> {
                var updatedTeam = teamMapper.mapToTeam(adTeam, team);
                teamRepository.save(updatedTeam);
            });
        });
        LOGGER.info("Synchronisation succeed: update all teams");
    }

    public void assignUsersToTeams() {
        this.pickTeamsFromAD().forEach(adTeam -> {
            var adName = adNameOf(adTeam);
            teamRepository.findFirstByAdName(adName).ifPresent(team -> {
                var membersAsString = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.MEMBER);
                var members = splitMembers(membersAsString);
                team.setUsers(members);
                teamRepository.save(team);
            });
        });
        LOGGER.info("Synchronisation succeed: assign users to teams");
    }

    private Set<User> splitMembers(String members) {
        var groups = ActiveDirectoryUtils.split(members);
        return Arrays.stream(groups)
                .map(userRepository::findFirstByAdName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private List<SearchResult> pickTeamsFromAD() {
        return teamIdentifiers.stream()
                .map(this::pickTeamsFromAD)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<SearchResult> pickTeamsFromAD(String teamIdentifier) {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.Group)
                .name(String.format("*%s", teamIdentifier))
                .search();
    }
}

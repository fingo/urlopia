package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.*;
import info.fingo.urlopia.config.ad.tree.ActiveDirectoryTree;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryTeamSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryTeamSynchronizer.class);

    @Value("${ad.containers.main}")
    private String mainContainer;

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
        var adTeams = pickTeamsFromAD();
        var adTeamsTree = buildTree(adTeams);
        adTeams.stream()
                .filter(adTeam ->
                        !dbTeams.contains(
                                adNameOf(adTeam)))
                .map(adTeam -> teamMapper.mapToTeam(adTeam, adTeamsTree))
                .forEach(teamRepository::save);
        LOGGER.info("Synchronisation succeed: find new teams");
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
        var adTeams = pickTeamsFromAD();
        var adTeamsTree = buildTree(adTeams);
        adTeams.forEach(adTeam -> {
            var adName = adNameOf(adTeam);
            teamRepository.findFirstByAdName(adName).ifPresent(team -> {
                var updatedTeam = teamMapper.mapToTeam(adTeam, team, adTeamsTree);
                teamRepository.save(updatedTeam);
            });
        });
        LOGGER.info("Synchronisation succeed: update all teams");
    }

    public void assignUsersToTeams() {
        var adTeamsAndUsers = pickTeamsAndUsersFromAD();
        var adTree = buildTree(adTeamsAndUsers);
        var adTeams = adTeamsAndUsers.stream()
                .filter(ActiveDirectoryUtils::isOU)
                .toList();
        adTeams.forEach(adTeam -> {
            var adName = adNameOf(adTeam);
            teamRepository.findFirstByAdName(adName).ifPresent(team -> {
                var membersDn = getTeamMembersDn(adTree, adName);
                var members = getMembers(membersDn);
                team.setUsers(members);
                teamRepository.save(team);
            });
        });
        LOGGER.info("Synchronisation succeed: assign users to teams");
    }

    private ActiveDirectoryTree buildTree(List<SearchResult> adTeamsAndUsers) {
        // Sort objects to make sure that all of them are placed inside tree, because tree impl allows replacements
        var sortedObjects = adTeamsAndUsers.stream()
                .sorted(Comparator.comparingLong(o -> {
                    var distinguishedName = ActiveDirectoryUtils.pickAttribute(o, Attribute.DISTINGUISHED_NAME);
                    return distinguishedName.length();
                }))
                .toList();
        var adTree = new ActiveDirectoryTree(mainContainer);
        for (var obj : sortedObjects) {
            adTree.put(obj);
        }
        return adTree;
    }

    private List<String> getTeamMembersDn(ActiveDirectoryTree tree,
                                          String teamDn) {
        return tree.searchDirectChildrenObjectsOf(teamDn).stream()
                .filter(ActiveDirectoryUtils::isPerson)
                .map(teamMember -> ActiveDirectoryUtils.pickAttribute(teamMember, Attribute.DISTINGUISHED_NAME))
                .toList();
    }

    private Set<User> getMembers(List<String> membersDn) {
        return membersDn.stream()
                .map(userRepository::findFirstByAdName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private List<SearchResult> pickTeamsFromAD() {
        return activeDirectory.newSearch()
                .excludeDistinguishedName(mainContainer)
                .objectClass(ActiveDirectoryObjectClass.ORGANIZATIONAL_UNIT)
                .search();
    }

    private List<SearchResult> pickTeamsAndUsersFromAD() {
        return activeDirectory.newSearch()
                .excludeDistinguishedName(mainContainer)
                .objectClasses(List.of(
                        ActiveDirectoryObjectClass.ORGANIZATIONAL_UNIT,
                        ActiveDirectoryObjectClass.PERSON
                ))
                .search();
    }
}

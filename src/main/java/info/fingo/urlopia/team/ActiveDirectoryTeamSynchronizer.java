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
    private String teamIdentifier;

    @Value("${ad.identifiers.business-part}")
    private String businessPartIdentifier;

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
        pickBusinessPartsFromAD().forEach(adBusinessPart ->
                pickTeamsFromAD(adBusinessPart).stream()
                        .filter(adTeam ->
                                !ActiveDirectoryUtils.pickAttribute(
                                        adTeam, Attribute.DISTINGUISHED_NAME).equals(usersGroup))
                        .filter(adTeam ->
                                !dbTeams.contains(
                                        ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME)))
                        .map(adTeam -> teamMapper.mapToTeam(adTeam, adBusinessPart))
                        .forEach(teamRepository::save));
        LOGGER.info("Synchronisation succeed: find new teams");
    }

    public void removeDeletedTeams() {
        var adTeams = pickBusinessPartsFromAD().stream()
                .map(this::pickTeamsFromAD)
                .flatMap(Collection::stream)
                .map(team ->
                        ActiveDirectoryUtils.pickAttribute(team, Attribute.DISTINGUISHED_NAME))
                .collect(Collectors.toList());

        teamRepository.findAll().stream()
                .filter(team ->
                        !adTeams.contains(team.getAdName()))
                .forEach(teamRepository::delete);
        LOGGER.info("Synchronisation succeed: remove deleted teams");
    }

    public void synchronize() {
        pickBusinessPartsFromAD().forEach(adBusinessPart ->
                pickTeamsFromAD(adBusinessPart).forEach(adTeam -> {
                    var adName = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME);
                    var optionalTeam = teamRepository.findFirstByAdName(adName);
                    optionalTeam.ifPresent(team -> teamRepository.save(
                            teamMapper.mapToTeam(adTeam, team, adBusinessPart)
                    ));
                })
        );
        LOGGER.info("Synchronisation succeed: update all teams");
    }

    public void assignUsersToTeams() {
        this.pickTeamsFromAD().forEach(adTeam -> {
            var adName = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME);
            var optionalTeam = teamRepository.findFirstByAdName(adName);
            optionalTeam.ifPresent(team -> {
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
                .collect(Collectors.toUnmodifiableSet());
    }

    private List<SearchResult> pickTeamsFromAD() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.Group)
                .name(String.format("*%s", teamIdentifier))
                .search();
    }

    private List<SearchResult> pickTeamsFromAD(SearchResult adBusinessPart) {
        var businessPartAdName = ActiveDirectoryUtils.pickAttribute(adBusinessPart, Attribute.DISTINGUISHED_NAME);
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.Group)
                .name("*" + this.teamIdentifier)
                .memberOf(businessPartAdName)
                .search();
    }

    private List<SearchResult> pickBusinessPartsFromAD() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.Group)
                .name("*" + this.businessPartIdentifier)
                .search();
    }

}

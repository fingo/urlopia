package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.util.List;

@Component
public class ActiveDirectoryTeamMapper {

    @Value("${ad.identifiers.team}")
    private List<String> teamIdentifiers;

    private final UserRepository userRepository;

    public ActiveDirectoryTeamMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Team mapToTeam(SearchResult adTeam,
                   SearchResult businessPart) {
        return this.mapToTeam(adTeam, new Team(), businessPart);
    }

    Team mapToTeam(SearchResult adTeam,
                   Team team,
                   SearchResult adBusinessPart) {
        team.setAdName(
                ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME));
        team.setName(
                normalizeName(ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.NAME)));
        team.setLeader(
                findUser(ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.MANAGED_BY)));
        team.setBusinessPartLeader(
                findUser(ActiveDirectoryUtils.pickAttribute(adBusinessPart, Attribute.MANAGED_BY)));
        return team;
    }

    private User findUser(String adName) {
        return userRepository
                .findFirstByAdName(adName)
                .orElse(null);
    }

    private String normalizeName(String adName) {
        return teamIdentifiers.stream()
                .filter(adName::contains)
                .findFirst()
                .map(teamIdentifier -> normalizeName(adName, teamIdentifier))
                .orElse("");
    }

    private String normalizeName(String adName, String teamIdentifier) {
        var end = adName.length() - teamIdentifier.length() - 1; // -1 for space between name and identifier
        return adName.substring(0, end);
    }
}

package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;

@Component
public class ActiveDirectoryTeamMapper {

    @Value("${ad.identifiers.team}")
    private String teamIdentifier;

    private final UserRepository userRepository;

    public ActiveDirectoryTeamMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Team mapToTeam(SearchResult searchResult) {
        Team newTeam = new Team();
        return this.mapToTeam(searchResult, newTeam);
    }

    Team mapToTeam(SearchResult searchResult, Team team) {
        team.setAdName(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.DISTINGUISHED_NAME));
        team.setName(this.normalizeName(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.NAME)));
        team.setLeader(userRepository.findFirstByAdName(
                ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MANAGED_BY)));
        return team;
    }

    private String normalizeName(String adName) {
        int end = adName.length() - teamIdentifier.length() - 1; // -1 for space between name and identifier
        return adName.substring(0, end);
    }
}

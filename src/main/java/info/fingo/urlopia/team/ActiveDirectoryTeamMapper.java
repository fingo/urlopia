package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.config.ad.tree.ActiveDirectoryTree;
import info.fingo.urlopia.user.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;

@Component
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryTeamMapper {

    private final ActiveDirectoryTeamLeaderProvider activeDirectoryTeamLeaderProvider;

    public ActiveDirectoryTeamMapper(ActiveDirectoryTeamLeaderProvider activeDirectoryTeamLeaderProvider) {
        this.activeDirectoryTeamLeaderProvider = activeDirectoryTeamLeaderProvider;
    }

    Team mapToTeam(SearchResult adTeam,
                   ActiveDirectoryTree adTeamsTree) {
        return this.mapToTeam(adTeam, new Team(), adTeamsTree);
    }

    Team mapToTeam(SearchResult adTeam,
                   Team team,
                   ActiveDirectoryTree adTeamsTree) {
        team.setAdName(ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME));
        team.setName(ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.NAME));
        team.setLeader(findTeamLeader(adTeam, adTeamsTree));
        return team;
    }

    private User findTeamLeader(SearchResult adTeam,
                                ActiveDirectoryTree adTeamsTree) {
        var adTeamDN = ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.DISTINGUISHED_NAME);
        return activeDirectoryTeamLeaderProvider
                .getTeamLeader(adTeamDN, adTeamsTree)
                .orElse(null);
    }
}

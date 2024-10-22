package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.config.ad.tree.ActiveDirectoryTree;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryTeamLeaderProvider {

    private final UserRepository userRepository;

    public Optional<User> getTeamLeader(String adTeamDN,
                                        ActiveDirectoryTree adTeamsTree) {
        return adTeamsTree.search(adTeamDN)
                .flatMap(adTeam -> {
                    var managedBy = getManagedBy(adTeam);
                    return managedBy
                            .map(this::getUser)
                            .orElseGet(() -> checkParentTeam(adTeamDN, adTeamsTree));
                });
    }

    private Optional<String> getManagedBy(SearchResult adTeam) {
        return Optional.ofNullable(ActiveDirectoryUtils.pickAttribute(adTeam, Attribute.MANAGED_BY))
                .filter(managedBy -> !managedBy.isBlank());
    }

    private Optional<User> checkParentTeam(String adTeamDN,
                                           ActiveDirectoryTree adTeamsTree) {
        var parentTeamDN = ActiveDirectoryUtils.getParentDN(adTeamDN);
        return getTeamLeader(parentTeamDN, adTeamsTree);
    }

    private Optional<User> getUser(String userDN) {
        return userRepository.findFirstByAdNameAndActiveTrue(userDN);
    }
}

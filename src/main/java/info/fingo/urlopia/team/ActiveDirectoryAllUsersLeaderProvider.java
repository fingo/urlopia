package info.fingo.urlopia.team;

import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.ActiveDirectoryObjectClass;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryAllUsersLeaderProvider implements AllUsersLeaderProvider{

    @Value("${ad.groups.users}")
    private String usersGroup;
    private final ActiveDirectory activeDirectory;
    private final UserRepository userRepository;


    @Override
    public User getAllUsersLeader() {
        var groups = activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.Group)
                .distinguishedName(usersGroup)
                .search();

        return groups.stream()
                .map(group -> ActiveDirectoryUtils.pickAttribute(group, Attribute.MANAGED_BY))
                .findFirst()
                .flatMap(userRepository::findFirstByAdName)
                .orElse(null);
    }
}

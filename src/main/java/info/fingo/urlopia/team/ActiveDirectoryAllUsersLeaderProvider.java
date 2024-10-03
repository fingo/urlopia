package info.fingo.urlopia.team;

import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryAllUsersLeaderProvider implements AllUsersLeaderProvider{


    @Override
    public User getAllUsersLeader() {
        return null;
    }
}

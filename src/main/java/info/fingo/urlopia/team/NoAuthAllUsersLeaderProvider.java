package info.fingo.urlopia.team;

import info.fingo.urlopia.user.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "false")
public class NoAuthAllUsersLeaderProvider implements AllUsersLeaderProvider{
    @Override
    public User getAllUsersLeader() {
        return null; //in this mode we don't need this
    }
}

package info.fingo.urlopia.config.ad;

import info.fingo.urlopia.config.authentication.LDAPConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActiveDirectory {
    private final LDAPConnectionService ldapConnectionService;

    @Value("${ad.containers.main}")
    private String mainContainer;

    @Autowired
    public ActiveDirectory(LDAPConnectionService ldapConnectionService) {
        this.ldapConnectionService = ldapConnectionService;
    }

    public ActiveDirectorySearcher newSearch() {
        return new ActiveDirectorySearcher(mainContainer, ldapConnectionService);
    }

}

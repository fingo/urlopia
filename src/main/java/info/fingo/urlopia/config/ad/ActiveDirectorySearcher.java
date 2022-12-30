package info.fingo.urlopia.config.ad;

import info.fingo.urlopia.config.authentication.LDAPConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectorySearcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectorySearcher.class);

    private final StringBuilder filter = new StringBuilder("(&");
    private final String mainContainer;
    private final LDAPConnectionService ldapConnectionService;

    public ActiveDirectorySearcher(String mainContainer,
                                   LDAPConnectionService ldapConnectionService) {
        this.mainContainer = mainContainer;
        this.ldapConnectionService = ldapConnectionService;
    }

    public ActiveDirectorySearcher objectClass(ActiveDirectoryObjectClass objectClass) {
        var value = String.format("(objectClass=%s)", objectClass.name());
        filter.append(value);
        return this;
    }

    public ActiveDirectorySearcher memberOf(String group) {
        var value = String.format("(memberOf=%s)", group);
        filter.append(value);
        return this;
    }

    public ActiveDirectorySearcher mail(String mail) {
        var value = String.format("(mail=%s)", mail);
        filter.append(value);
        return this;
    }

    public ActiveDirectorySearcher name(String name) {
        var value = String.format("(name=%s)", name);
        filter.append(value);
        return this;
    }

    public ActiveDirectorySearcher distinguishedName(String distinguishedName) {
        var value = String.format("(distinguishedName=%s)", distinguishedName);
        filter.append(value);
        return this;
    }

    public ActiveDirectorySearcher isDisabled(){
        var builder = new StringBuilder("(|");
        for (var disableKey: ActiveDirectoryUtils.DISABLED_STATUS){
            var value = String.format("(%s=%s)",Attribute.USER_ACCOUNT_CONTROL.getKey(), disableKey);
            builder.append(value);
        }
        builder.append(")");
        filter.append(builder);
        return this;
    }

    public List<SearchResult> search() {
        var filter = this.filter.append(")").toString();
        List<SearchResult> result = new LinkedList<>();

        var controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // connecting to AD and getting data
        DirContext ad = null;
        try {
            ad = ldapConnectionService.getContext();
            result = Collections.list(ad.search(mainContainer, filter, controls));
        } catch (NamingException e) {
            LOGGER.error("Exception when trying to search in Active Directory", e);
        } finally {
            try {
                if (ad != null) {
                    ad.close();
                }
            } catch (NamingException e) {
                LOGGER.error("Exception when trying to close the LDAP connection", e);
            }
        }

        return result;
    }
}

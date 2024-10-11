package info.fingo.urlopia.user;

import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.Attribute;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryUserLeaderProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryUserLeaderProvider.class);

    private final ActiveDirectory activeDirectory;
    private final UserRepository userRepository;

    public User getUserLeader(User user) {
        try {
            return getUserLeaderUnsafe(user);
        } catch (NamingException e) {
            LOGGER.error("NamingException when trying to get a leader for user", e);
            return null;
        }
    }

    private User getUserLeaderUnsafe(User user) throws NamingException {
        // Step 1: Get distinguished names of OUs user belongs to
        var userSearch = activeDirectory.newSearch().principalName(user.getPrincipalName()).search();
        var userDN = userSearch.stream().findFirst().map(NameClassPair::getNameInNamespace).orElse("");
        var organizationalUnits = extractOrganizationalUnitsDNs(userDN);

        // Step 2: Find first existing and valid OU manager.
        for (var ouDn : organizationalUnits) {
            var controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            var ouSearch = activeDirectory.newSearch().distinguishedName(ouDn).search(controls);
            for (var result : ouSearch) {
                var attributes = result.getAttributes();
                var managedBy = attributes.get(Attribute.MANAGED_BY.getKey());
                var managedByDN = managedBy != null ? (String) managedBy.get() : "";
                if (!managedByDN.isBlank() && !managedByDN.equals(userDN)) {
                    var manager = getManagerDetails(managedByDN);
                    if (manager.isPresent()) {
                        return manager.get();
                    }
                }
            }
        }

        return null;
    }

    /**
     * When OUs are nested, they appear next to each other in the DN (i.e. OU=Child,OU=Parent)
     * This helper method extracts all possible organizational unit DNs in order from the most
     * specific (the one user is in directly) to the most generic one.
     */
    private List<String> extractOrganizationalUnitsDNs(String distinguishedName) {
        var result = new ArrayList<String>();

        var components = distinguishedName.split(",");
        for (var i = 0; i < components.length; i++) {
            var component = components[i].trim();
            if (component.startsWith("OU=")) {
                var dn = Arrays.stream(components).skip(i).collect(Collectors.joining(","));
                result.add(dn);
            }
        }

        return result;
    }

    private Optional<User> getManagerDetails(String managerDN) throws NamingException {
        var search = activeDirectory.newSearch().distinguishedName(managerDN).search();
        for (var result : search) {
            var attributes = result.getAttributes();
            var principalNameAttribute = attributes.get(Attribute.PRINCIPAL_NAME.getKey());
            var principalName = principalNameAttribute != null ? (String) principalNameAttribute.get() : "";
            if (!principalName.isBlank()) {
                return this.userRepository.findFirstByPrincipalName(principalName);
            }
        }
        return Optional.empty();
    }
}

package info.fingo.urlopia.ad;

import info.fingo.urlopia.authentication.LDAPConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
public class ActiveDirectoryX {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryX.class);

    @Autowired
    private final LDAPConnectionService ldapConnectionService;

    @Value("${ad.containers.main}")
    private String mainContainer;

    @Autowired
    public ActiveDirectoryX(LDAPConnectionService ldapConnectionService) {
        this.ldapConnectionService = ldapConnectionService;
    }

    public Searcher newSearch() {
        return new Searcher();
    }

    public class Searcher {
        private StringBuilder filter = new StringBuilder("(&");

        private Searcher() {
            /* Private constructor, because object can be created only by ActiveDirectory */
        }

        public ActiveDirectoryX.Searcher objectClass(ObjectClass objectClass) {
            String value = String.format("(objectClass=%s)", objectClass.name());
            filter.append(value);
            return this;
        }

        public ActiveDirectoryX.Searcher memberOf(String group) {
            String value = String.format("(memberOf=%s)", group);
            filter.append(value);
            return this;
        }

        public ActiveDirectoryX.Searcher mail(String mail) {
            String value = String.format("(mail=%s)", mail);
            filter.append(value);
            return this;
        }

        public ActiveDirectoryX.Searcher name(String name) {
            String value = String.format("(name=%s)", name);
            filter.append(value);
            return this;
        }

        public List<SearchResult> search() {
            String filter = this.filter.append(")").toString();
            return search(filter);
        }

        private List<SearchResult> search(String filter) {
            List<SearchResult> result = new LinkedList<>();

            SearchControls controls = new SearchControls();
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

    public enum ObjectClass {
        Person,
        Group
    }
}

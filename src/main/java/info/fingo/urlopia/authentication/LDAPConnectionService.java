package info.fingo.urlopia.authentication;

import info.fingo.urlopia.ad.ActiveDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Class which authenticates username and password with LDAP
 *
 * @author Józef Grodzicki
 */
@Component
public class LDAPConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPConnectionService.class);

    @Value("${ldap.initial.context.factory}")
    private String initialContextFactory;

    @Value("${ldap.security.authentication}")
    private String securityAuthentication;

    @Value("${ldap.security.principal}")
    private String securityPrincipal;

    @Value("${ldap.security.credentials}")
    private String securityCredentials;

    @Value("${ldap.provider.url}")
    private String providerUrl;

    @Value("${ad.team.main.group}")
    private String mainGroup;


    public DirContext getContext() {
        Hashtable<String, String> env = new Hashtable<>(); // NOSONAR

        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
        env.put(Context.PROVIDER_URL, providerUrl);

        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            throw new ActiveDirectoryConnectionException(e);
        }
    }

    public boolean authenticate(Credentials credentials) {
        DirContext ctx = getContext();

        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> results = ctx.search(ActiveDirectory.USERS_CONTAINER,
                    "(&(memberOf=" + ActiveDirectory.MAIN_TEAM_GROUP + ")" +
                            "(userPrincipalName=" + credentials.getMail() + "))", controls);

            if (results.hasMore()) {
                Properties authEnv = new Properties();
                authEnv.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
                authEnv.put(Context.PROVIDER_URL, providerUrl);
                authEnv.put(Context.SECURITY_PRINCIPAL, credentials.getMail());
                authEnv.put(Context.SECURITY_CREDENTIALS, credentials.getPassword());

                new InitialDirContext(authEnv); // NOSONAR

                return true;
            }
        } catch (NamingException e) {
            LOGGER.info("Username or password is incorrect!", e);
        }

        return false;
    }

    private static class ActiveDirectoryConnectionException extends RuntimeException {
        ActiveDirectoryConnectionException(NamingException exception) {
            super(exception);
        }
    }
}
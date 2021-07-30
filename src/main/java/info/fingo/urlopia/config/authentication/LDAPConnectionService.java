package info.fingo.urlopia.config.authentication;

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

    @Value("${ad.containers.main}")
    private String mainContainer;

    @Value("${ad.groups.users}")
    private String usersGroup;


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
            var controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            var filter = "(&(memberOf=" + usersGroup + ")" + "(userPrincipalName=" + credentials.getMail() + "))";
            boolean userFound = ctx.search(mainContainer, filter, controls).hasMore();

            if (userFound) {
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
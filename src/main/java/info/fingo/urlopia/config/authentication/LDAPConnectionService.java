package info.fingo.urlopia.config.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;

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
            LOGGER.error("Something went wrong while connecting to AD");
            throw new ActiveDirectoryConnectionException(e);
        }
    }

    private static class ActiveDirectoryConnectionException extends RuntimeException {
        ActiveDirectoryConnectionException(NamingException exception) {
            super(exception);
        }
    }
}
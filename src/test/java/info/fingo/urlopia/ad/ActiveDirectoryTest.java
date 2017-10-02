package info.fingo.urlopia.ad;

import info.fingo.urlopia.authentication.LDAPConnectionService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Tomasz Urbas
 * @author Jakub Licznerski
 */
public class ActiveDirectoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryTest.class);

    private ActiveDirectory ad;

    @Before
    public void createConnection() throws NamingException {
        ad = new ActiveDirectory();
        LDAPConnectionService ldap = Mockito.mock(LDAPConnectionService.class);

        // LDAPConnectionService::getContext mock
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "info.fingo.urlopia.ad.MockInitialDirContextFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, "ldap://thisIsIgnoredInTests");
        Mockito.when(ldap.getContext()).thenReturn(new InitialLdapContext(env, null));

        //mocking Context.search method with MockNamingEnumeration class
        final DirContext mockContext = MockInitialDirContextFactory.getLatestMockContext();

        Mockito.when(mockContext.search(Mockito.anyString(), Mockito.anyString(), Mockito.any(SearchControls.class)))
                .thenAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        LOGGER.debug("LDAP query:" + invocation.getArguments()[1] );
                        return new MockNamingEnumeration();
                    }
                });

        //mocking configuration variables
        ReflectionTestUtils.setField(ad, "ldapConnectionService", ldap);
        ReflectionTestUtils.setField(ad, "USERS_CONTAINER", "mock");
        ReflectionTestUtils.setField(ad, "LEADERS_GROUP", "mock");
        ReflectionTestUtils.setField(ad, "B2B_EMPLOYEES_GROUP", "mock");
        ReflectionTestUtils.setField(ad, "EC_EMPLOYEES_GROUP", "mock");
        ReflectionTestUtils.setField(ad, "URLOPIA_TEAM_GROUP", "mock");
        ReflectionTestUtils.setField(ad, "TEAM_IDENTIFIER", " Team");
    }


    @Test @Ignore
    public void getUsersTest() {

        List<LocalUser> users = ad.getUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }


    @Test @Ignore
    public void getTeamsTest() {
        List<LocalTeam> teams = ad.getTeams();

        assertNotNull(teams);
        assertFalse(teams.isEmpty());
    }
}

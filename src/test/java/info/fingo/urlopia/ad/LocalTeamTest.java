package info.fingo.urlopia.ad;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Tomasz Urbas
 */
public class LocalTeamTest {

    @Before
    public void setup() {
        ReflectionTestUtils.setField(ActiveDirectory.class, "TEAM_IDENTIFIER", " Team,");
    }

    @Test
    public void createLocalTeam() {
        LocalUser user = new LocalUser();
        user.setMail("aaa@example.com");

        LocalTeam team = new LocalTeam("CN=ABC Team,OU=XX,DC=cc,DC=vv", user);

        assertNotNull(team);
        assertEquals("CN=ABC Team,OU=XX,DC=cc,DC=vv", team.getFullName());
        assertEquals("ABC", team.getName());
        assertNotNull(team.getLeader());
        assertEquals("aaa@example.com", team.getLeader().getMail());
    }
}

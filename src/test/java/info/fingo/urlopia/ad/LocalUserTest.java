package info.fingo.urlopia.ad;

import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * @author Tomasz Urbas
 */
public class LocalUserTest {

    @Test @Ignore
    public void createLocalUserTest() {
        LocalUser user = new LocalUser();

        assertNotNull(user);
        assertEquals("", user.getName());
        assertEquals("", user.getSurname());
        assertEquals("", user.getMail());
        assertFalse(user.isLeader());
        assertNull(user.getTeams());

        user.setName("Rozowa");
        user.setSurname("Pantera");
        user.setMail("rozowa.pantera@example.com");
        user.setLeader(true);
        user.setTeams(new LinkedList<>());

        assertEquals("Rozowa", user.getName());
        assertEquals("Pantera", user.getSurname());
        assertEquals("rozowa.pantera@example.com", user.getMail());
        assertTrue(user.isLeader());
        assertNotNull(user.getTeams());
        assertTrue(user.getTeams().isEmpty());
    }
}

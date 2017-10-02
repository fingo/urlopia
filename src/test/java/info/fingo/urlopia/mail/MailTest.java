package info.fingo.urlopia.mail;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Tomasz Urbas
 */
public class MailTest {

    @Test @Ignore
    public void createMail() {
        Mail mail = new Mail();

        assertNotNull(mail);
        assertNull(mail.getSenderAddress());
        assertNull(mail.getRecipientName());
        assertNull(mail.getRecipientAddress());
        assertNull(mail.getRecipientName());
        assertNull(mail.getSubject());
        assertNull(mail.getContent());

        mail.setSenderAddress("aa123@example.com");
        assertEquals("aa123@example.com", mail.getSenderAddress());

        mail.setSenderName("Me And You");
        assertEquals("Me And You", mail.getSenderName());

        mail.setRecipientAddress("test@example.com");
        assertEquals("test@example.com", mail.getRecipientAddress());

        mail.setRecipientName("Urlopia Application");
        assertEquals("Urlopia Application", mail.getRecipientName());

        mail.setSubject("Subject test sub");
        assertEquals("Subject test sub", mail.getSubject());

        mail.setContent("Contententet fdanisfnodsfsnfasl\nasfsdfdsf");
        assertEquals("Contententet fdanisfnodsfsnfasl\nasfsdfdsf", mail.getContent());
    }
}

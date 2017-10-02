package info.fingo.urlopia.mail;

import info.fingo.urlopia.mail.send.MailConverter;
import org.junit.Ignore;
import org.junit.Test;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Tomasz Urbas
 */
public class MailConverterTest {

    @Test @Ignore
    public void createWithMail() throws Exception {
        Mail mail = new Mail();
        mail.setSenderAddress("aa123@example.com");
        mail.setSenderName("Me And You");
        mail.setRecipientAddress("test@example.com");
        mail.setRecipientName("XXX");
        mail.setSubject("Subject test sub");
        mail.setContent("Contentendf safds dsfas\nfafsdf");

        Message message = new MailConverter(mail).toMimeMessage();

        assertNotNull(message);
        assertEquals("[Me And You <aa123@example.com>]", Arrays.toString(message.getFrom()));
        assertEquals("[XXX <test@example.com>]", Arrays.toString(message.getRecipients(Message.RecipientType.TO)));
        assertEquals("Subject test sub", message.getSubject());

        Multipart multipart = (Multipart) message.getContent();
        BodyPart bodyPart = multipart.getBodyPart(0);
        assertEquals("Contentendf safds dsfas\nfafsdf", bodyPart.getContent());
    }

    @Test @Ignore
    public void createWithNull() {
        Message message = new MailConverter(null).toMimeMessage();

        assertNull(message);
    }
}

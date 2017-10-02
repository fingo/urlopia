package info.fingo.urlopia.mail;

import info.fingo.urlopia.mail.receive.MessageConverter;
import org.junit.Ignore;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.junit.Assert.*;

/**
 * @author Tomasz Urbas
 */
public class MessageConverterTest {

    private static Message createMessage(String sender, String senderName, String recipient,
                                         String recipientName, String subject, String body) throws Exception {

        Message message = new MimeMessage(Session.getInstance(System.getProperties()));

        message.setFrom(new InternetAddress(sender, senderName));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient, recipientName));
        message.setSubject(subject);

        // create the message part
        MimeBodyPart content = new MimeBodyPart();

        // fill message
        content.setText(body);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(content);

        // integration
        message.setContent(multipart);

        return message;
    }

    @Test @Ignore
    public void createWithMessage() throws Exception {
        Message message = createMessage("me@example.com", null, "ur@example.pl", null, "Test mail", "qwerty Test");
        Mail mail = new MessageConverter(message).toMail();

        assertNotNull(mail);
        assertEquals("me@example.com", mail.getSenderAddress());
        assertEquals("", mail.getSenderName());
        assertEquals("ur@example.pl", mail.getRecipientAddress());
        assertEquals("", mail.getRecipientName());
        assertEquals("Test mail", mail.getSubject());
        assertEquals("qwerty Test", mail.getContent());

        // another Message
        message = createMessage("aaa@example.com", "Jajo", "sasf@example.com", "Jup", "asdfasfadsf", "Wowoo");
        mail = new MessageConverter(message).toMail();

        assertNotNull(mail);
        assertEquals("aaa@example.com", mail.getSenderAddress());
        assertEquals("Jajo", mail.getSenderName());
        assertEquals("sasf@example.com", mail.getRecipientAddress());
        assertEquals("Jup", mail.getRecipientName());
        assertEquals("asdfasfadsf", mail.getSubject());
        assertEquals("Wowoo", mail.getContent());
    }

    @Test @Ignore
    public void createWithNull() {
        Mail mail = new MessageConverter(null).toMail();

        assertNull(mail);
    }
}

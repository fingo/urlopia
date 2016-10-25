package info.fingo.urlopia.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;

/**
 * Converts Mail to Message
 *
 * @author Tomasz Urbas
 */
class MailConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailConverter.class);

    private final Mail mail;

    MailConverter(Mail mail) {
        this.mail = mail;
    }

    // PRIVATE METHODS
    private InternetAddress pickSender() {
        InternetAddress sender = null;

        try {
            sender = new InternetAddress(mail.getSenderAddress(), mail.getSenderName());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException during creating InternetAddress", e);
        }

        return sender;
    }

    private InternetAddress pickRecipient() {
        InternetAddress recipient = null;

        try {
            recipient = new InternetAddress(mail.getRecipientAddress(), mail.getRecipientName());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException during creating InternetAddress", e);
        }

        return recipient;
    }

    private Multipart pickContent() {
        Multipart content = new MimeMultipart();

        try {
            // create the message part
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            // fill message
            mimeBodyPart.setText(mail.getContent());

            content.addBodyPart(mimeBodyPart);
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during setting adding MimeBodyPart", e);
        }

        return content;
    }

    // PUBLIC METHODS
    MimeMessage toMimeMessage() {
        MimeMessage message = null;

        if (mail != null) {
            message = new MimeMessage(Session.getInstance(System.getProperties()));

            try {
                message.setFrom(pickSender());
                message.setRecipient(Message.RecipientType.TO, pickRecipient());
                message.setSubject(mail.getSubject());
                message.setContent(pickContent());
            } catch (MessagingException e) {
                LOGGER.error("Exception during creating the Message", e);
            }
        }

        return message;
    }
}

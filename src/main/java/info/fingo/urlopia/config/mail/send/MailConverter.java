package info.fingo.urlopia.config.mail.send;

import info.fingo.urlopia.config.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
 */
@Component
class MailConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailConverter.class);

    @Value("${mail.title.prefix:}")
    private String titlePrefix;


    private InternetAddress pickSender(Mail mail) {
        InternetAddress sender = null;

        try {
            sender = new InternetAddress(mail.getSenderAddress(), mail.getSenderName());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException during creating InternetAddress", e);
        }

        return sender;
    }

    private InternetAddress pickRecipient(Mail mail) {
        InternetAddress recipient = null;

        try {
            recipient = new InternetAddress(mail.getRecipientAddress(), mail.getRecipientName());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException during creating InternetAddress", e);
        }

        return recipient;
    }

    private Multipart pickContent(Mail mail) {
        var content = new MimeMultipart();

        try {
            // create the message part
            var mimeBodyPart = new MimeBodyPart();
            // fill message
            mimeBodyPart.setText(mail.getContent());

            content.addBodyPart(mimeBodyPart);
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during setting adding MimeBodyPart", e);
        }

        return content;
    }

    MimeMessage toMimeMessage(Mail mail) {
        MimeMessage message = null;

        if (mail != null) {
            message = new MimeMessage(Session.getInstance(System.getProperties()));

            try {
                var title = !StringUtils.hasLength(titlePrefix) ? mail.getSubject() : String.format("%s - %s", titlePrefix, mail.getSubject());
                message.setFrom(pickSender(mail));
                message.setRecipient(Message.RecipientType.TO, pickRecipient(mail));
                message.setSubject(title);
                message.setContent(pickContent(mail), "text/plain");
            } catch (MessagingException e) {
                LOGGER.error("Exception during creating the Message", e);
            }
        }

        return message;
    }
}

package info.fingo.urlopia.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.util.Optional;

/**
 * Converts Message to Mail
 *
 * @author Tomasz Urbas
 */
class MessageConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConverter.class);

    private final Message message;

    // CONSTRUCTORS
    MessageConverter(Message message) {
        this.message = message;
    }

    // PRIVATE METHODS
    // methods responsible for getting information about user
    private Optional<InternetAddress> getSenderField() {
        Optional<InternetAddress> senderField = Optional.empty();
        try {
            Address[] sender = message.getFrom();
            senderField = Optional.ofNullable((InternetAddress) sender[0]);
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during getFrom", e);
        }

        return senderField;
    }

    private Optional<InternetAddress> getRecipientField() {
        Optional<InternetAddress> recipientField = Optional.empty();
        try {
            Address[] recipient = message.getRecipients(Message.RecipientType.TO);
            recipientField = Optional.ofNullable((InternetAddress) recipient[0]);
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during getFrom", e);
        }

        return recipientField;
    }

    // methods responsible for getting the content from the Message
    private String getContent(Part part) throws MessagingException,
            IOException {

        String content = "";
        if (part.getContent() instanceof String) {
            content = (String) part.getContent();
        } else {
            Multipart mp = (Multipart) part.getContent();
            if (mp.getCount() > 0) {
                Part bodyPart = mp.getBodyPart(0);
                try {
                    content = dumpPart(bodyPart);
                } catch (IOException e) {
                    LOGGER.error("Exception during dumping the part form the Message", e);
                } catch (MessagingException e) {
                    LOGGER.error("MessagingException during dumping the part form the Message", e);
                }
            }
        }
        return content.trim();
    }

    private String dumpPart(Part part) throws IOException, MessagingException {

        InputStream inputStream = part.getInputStream();
        // if "inputStream" is not already buffered, wrap a BufferedInputStream around it.
        if (!(inputStream instanceof BufferedInputStream)) {
            inputStream = new BufferedInputStream(inputStream);
        }
        return getStringFromInputStream(inputStream);
    }

    private String getStringFromInputStream(InputStream inputStream) {

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            LOGGER.error("IOException during creating bufferedReader from inputStream", e);
        }
        return stringBuilder.toString();
    }

    // methods responsible for getting the Message fields
    private String pickSenderAddress() {
        return getSenderField()
                .map(InternetAddress::getAddress)
                .orElse("");
    }

    private String pickSenderName() {
        return getSenderField()
                .map(InternetAddress::getPersonal)
                .orElse("");
    }

    private String pickRecipientAddress() {
        return getRecipientField()
                .map(InternetAddress::getAddress)
                .orElse("");
    }

    private String pickRecipientName() {
        return getRecipientField()
                .map(InternetAddress::getPersonal)
                .orElse("");
    }

    private String pickSubject() {
        String subject = "";

        try {
            subject = message.getSubject();
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during getSubject", e);
        }

        return subject;
    }

    private String pickContent() {
        String content = "";

        try {
            Object msg = message.getContent();
            if (msg instanceof String) {
                content = (String) msg;
            } else {
                Multipart multipart = (Multipart) message.getContent();
                Part bodyPart = multipart.getBodyPart(0);
                content = getContent(bodyPart);
            }
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during getting content from Message", e);
        } catch (IOException e) {
            LOGGER.error("IOException during getting content from Message", e);
        }

        return content;
    }

    // PUBLIC METHODS
    Mail toMail() {
        Mail mail = null;

        if (message != null) {
            mail = new Mail();

            mail.setSenderAddress(pickSenderAddress());
            mail.setSenderName(pickSenderName());
            mail.setRecipientAddress(pickRecipientAddress());
            mail.setRecipientName(pickRecipientName());
            mail.setSubject(pickSubject());
            mail.setContent(pickContent());
        }

        return mail;
    }
}
package info.fingo.urlopia.config.mail.receive;

import com.sun.mail.imap.IMAPFolder;
import info.fingo.urlopia.config.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Checks for new mails in inbox
 */
@Component
public class MailReceiver extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailReceiver.class);

    private final MailDecider mailDecider;

    @Value("${mail.receiver.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${mail.receiver.folder}")
    private String folderName;

    @Value("${mail.receiver.idle.time}")
    private int keepAliveFreq;    //time unit: milliseconds

    private Store store;
    private IMAPFolder inbox;
    private int currentMessageCount;

    @Autowired
    public MailReceiver(MailDecider mailDecider) {
        this.mailDecider = mailDecider;
    }

    /**
     * Connecting to the mail box
     */
    private void connect() {
        var storeProtocol = "imaps";

        var props = new Properties();
        props.setProperty("mail.store.protocol", storeProtocol);

        try {
            Session session = Session.getInstance(props, null);
            store = session.getStore();
            store.connect(host, username, password);
        } catch (Exception e) {
            LOGGER.error("Exception when trying to configure IMAP server", e);
        }
    }

    /**
     * Choosing the main folder in store
     *
     * @return inbox folder
     */
    private Folder getInbox() {
        Folder folder = null;

        try {
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            LOGGER.error("MessagingException when trying to open inbox folder", e);
        }

        return folder;
    }

    /**
     * Basic configuration for inbox
     */
    private void performConfiguration() {
        // Creating connection with mail box
        connect();
        inbox = (IMAPFolder) getInbox();

        // Getting starting message count
        try {
            currentMessageCount = inbox.getMessageCount();
        } catch (MessagingException e) {
            LOGGER.error("MessagingException when trying get message count", e);
        }

        // Listening for new messages
        inbox.addMessageCountListener(new InboxMessageCountListener());

        // reload the idle process
        keepInboxAlive();
    }

    /**
     * Sending the NOOP command for making any action at inbox to keep the connection alive
     */
    private void keepInboxAlive() { // NOSONAR
        try {
            inbox.doCommand(p -> {
                p.simpleCommand("NOOP", null);
                return null;
            });
        } catch (MessagingException e) {
            LOGGER.error("MessagingException when sending NOOP command", e);
        }
    }

    /**
     * Watching at inbox
     */
    private void keepInboxIdle() {
        while (!Thread.interrupted()) {
            try {
                inbox.idle();
            } catch (MessagingException e) {
                performConfiguration();
            }
        }
    }

    @Override
    public void run() {
        // Configuring the inbox
        performConfiguration();

        // Creating scheduler to keep alive the inbox
        var scheduledExecutorService = Executors.newScheduledThreadPool(1);
        var timeUnit = TimeUnit.MILLISECONDS;
        scheduledExecutorService.scheduleAtFixedRate(this::keepInboxAlive, keepAliveFreq, keepAliveFreq, timeUnit);

        // Keeping the inbox idle
        keepInboxIdle();

        // Closing connections
        try {
            inbox.close(false);
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during closing the inbox folder", e);
        }

        try {
            store.close();
        } catch (MessagingException e) {
            LOGGER.error("MessagingException during closing the store", e);
        }
    }

    /**
     * Listening new messages
     */
    private class InboxMessageCountListener implements MessageCountListener {

        @Override
        public void messagesAdded(MessageCountEvent ex) {
            try {
                var newMessageCount = inbox.getMessageCount();
                for (int messageId = currentMessageCount + 1; messageId <= newMessageCount; messageId++) {
                    var message = inbox.getMessage(messageId);
                    var mail = new MessageConverter(message).toMail();
                    mailDecider.resolve(mail);
                }
                currentMessageCount = newMessageCount;
            } catch (MessagingException e) {
                LOGGER.error("MessagingException when trying get last message", e);
            }
        }

        // Update the current message count
        @Override
        public void messagesRemoved(MessageCountEvent ex) {
            try {
                currentMessageCount = inbox.getMessageCount();
            } catch (MessagingException e) {
                LOGGER.error("MessagingException when trying update currentMessageCount during messageRemoved", e);
            }
        }
    }

    @Bean
    public CommandLineRunner startup() {
        return args -> this.start();
    }
}

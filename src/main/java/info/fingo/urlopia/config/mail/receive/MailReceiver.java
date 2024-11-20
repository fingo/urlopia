package info.fingo.urlopia.config.mail.receive;

import com.sun.mail.imap.IMAPFolder;
import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Checks for new mails in inbox
 */
@Slf4j
@Component
public class MailReceiver extends Thread {

    private final MailDecider mailDecider;

    @Value("${mail.receiver.host}")
    private String host;

    @Value("${mail.receiver.username}")
    private String username;

    @Value("${mail.receiver.password}")
    private String password;

    @Value("${mail.receiver.folder}")
    private String folderName;

    @Value("${mail.receiver.idle.time}")
    private int keepAliveFreq;    //time unit: milliseconds

    private Store store;
    private IMAPFolder inbox;

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
            log.info("Successfully configured IMAP server");
        } catch (Exception e) {
            log.error("Exception while trying to configure IMAP server", e);
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
            log.error("MessagingException while trying to open inbox folder", e);
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
            log.error("MessagingException while sending NOOP command", e);
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
                log.error("Something went wrong while watching at inbox");
                performConfiguration();
            }
        }
    }

    @Override
    public void run() {
        if (!host.isBlank()){
            log.info("Initializing MailReceiver");

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
                log.info("Inbox folder connection closed");
            } catch (MessagingException e) {
                log.error("MessagingException during closing the inbox folder", e);
            }

            try {
                store.close();
                log.info("Store connection closed");
            } catch (MessagingException e) {
                log.error("MessagingException during closing the store", e);
            }
        } else {
            log.warn("MailReceiver has been disabled");
        }
    }

    /**
     * Listening new messages
     */
    private class InboxMessageCountListener implements MessageCountListener {

        @Override
        public void messagesAdded(MessageCountEvent ex) {
            for (var addedMessage : ex.getMessages()) {
                var mail = new MessageConverter(addedMessage).toMail();
                var loggerInfo = "New email sent by %s %n Subject: %s".formatted(
                        Anonymizer.anonymizeMail(mail.getSenderAddress()),
                        Anonymizer.anonymizeSubject(mail.getSubject()));
                log.info(loggerInfo);
                mailDecider.resolve(mail);
            }
        }

        // Update the current message count
        @Override
        public void messagesRemoved(MessageCountEvent ex) {
            var removedMessages = ex.getMessages();
            log.info("{} messages were removed from inbox", removedMessages.length);
        }
    }

    @Bean
    public CommandLineRunner startup() {
        return args -> this.start();
    }
}

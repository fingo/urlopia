package info.fingo.urlopia.config.mail.receive;

import info.fingo.urlopia.config.mail.Mail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailBounceDetector {
    private static final List<String> FORBIDDEN_MAIL_SUBJECTS = List.of(
            "Email Delivery Failure",
            "Delivery Notification: Delivery has failed",
            "Delivery Notification: Delivery has timed out and failed",
            "Delivery Status Notification",
            "Delivery Delayed",
            "Delivery Failure",
            "Failure Notice",
            "Impossibile_recapitare",
            "Mail delivery failed: returning message to sender",
            "Mail Delivery Failure",
            "Returned mail",
            "Returned email",
            "Undeliverable",
            "Undeliverable Mail",
            "Undelivered Mail Returned to Sender");

    private static final List<String> FORBIDDEN_MAIL_SENDER_ADDRESSES = List.of(
            "MAILER-DAEMON",
            "mailer-daemon",
            "noreply",
            "postmaster",
            "postmaster_smtp");



    public boolean isBounce(Mail mail){
        var isBounceEmailAddress = isBounceEmailAddress(mail.getSenderAddress());
        var isBounceEmailSubject = isBounceEmailSubject(mail.getSubject());
        return isBounceEmailAddress || isBounceEmailSubject;
    }

    private boolean isBounceEmailAddress(String address){
        var username = address.substring(0, address.indexOf("@"));
        return FORBIDDEN_MAIL_SENDER_ADDRESSES.contains(username);
    }

    private boolean isBounceEmailSubject(String subject){
        return FORBIDDEN_MAIL_SUBJECTS.contains(subject.trim());
    }

}

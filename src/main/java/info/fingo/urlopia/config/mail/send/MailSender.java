package info.fingo.urlopia.config.mail.send;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.config.mail.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

@Slf4j
public class MailSender {

    private final JavaMailSender mailSender;

    public MailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    protected void send(Mail mail) {
        MimeMessage mimeMessage = new MailConverter(mail).toMimeMessage();
        mailSender.send(mimeMessage);
        var loggerInfo = "New mail with subject: %s has been sent to: %s"
                .formatted(Anonymizer.anonymizeSubject(mail.getSubject()),
                        Anonymizer.anonymizeMail(mail.getRecipientAddress()));
        log.info(loggerInfo);
    }

}

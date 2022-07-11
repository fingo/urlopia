package info.fingo.urlopia.config.mail.send;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.config.mail.Mail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSender mailSender;
    private final MailConverter mailConverter;

    protected void send(Mail mail) {
        MimeMessage mimeMessage = mailConverter.toMimeMessage(mail);
        mailSender.send(mimeMessage);
        var loggerInfo = "New mail with subject: %s has been sent to: %s"
                .formatted(Anonymizer.anonymizeSubject(mail.getSubject()),
                        Anonymizer.anonymizeMail(mail.getRecipientAddress()));
        log.info(loggerInfo);
    }

}

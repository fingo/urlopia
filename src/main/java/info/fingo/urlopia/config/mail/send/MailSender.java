package info.fingo.urlopia.config.mail.send;

import info.fingo.urlopia.config.mail.Mail;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

public class MailSender {

    private final JavaMailSender mailSender;

    public MailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    protected void send(Mail mail) {
        MimeMessage mimeMessage = new MailConverter(mail).toMimeMessage();
//        mailSender.send(mimeMessage);
    }

}

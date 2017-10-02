package info.fingo.urlopia.mail.send;

import info.fingo.urlopia.mail.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailNotificator extends MailSender {

    @Value("${app.name}")
    private String senderName;

    @Value("${mails.bot}")
    private String senderAddress;

    @Autowired
    public MailNotificator(JavaMailSender mailSender) {
        super(mailSender);
    }

    public void notify(MailTemplate template, String recipientAddress) {
        this.notify(template, recipientAddress, recipientAddress);
    }

    public void notify(MailTemplate template, String recipientAddress, String recipientName) {
        Mail mail = Mail.newBuilder()
                .setTemplate(template)
                .setSenderName(senderName)
                .setSenderAddress(senderAddress)
                .setRecipientName(recipientName)
                .setRecipientAddress(recipientAddress)
                .build();
        super.send(mail);
    }
}

package info.fingo.urlopia.config.mail.send;

import info.fingo.urlopia.config.mail.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailStorage extends MailSender {

    @Value("${app.name}")
    private String senderName;

    @Value("${mails.bot}")
    private String senderAddress;

    @Value("${mails.storage}")
    private String storageAddress;

    @Value("${mail.sender.enabled}")
    private boolean isEnabled;

    @Autowired
    public MailStorage(JavaMailSender mailSender,
                       MailConverter mailConverter) {
        super(mailSender, mailConverter);
    }

    public void store(MailTemplate template) {
        if (isEnabled){
            Mail mail = Mail.newBuilder()
                    .setTemplate(template)
                    .setSenderName(senderName)
                    .setSenderAddress(senderAddress)
                    .setRecipientAddress(storageAddress)
                    .build();
            super.send(mail);
        }
    }
}

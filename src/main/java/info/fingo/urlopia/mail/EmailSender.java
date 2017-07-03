package info.fingo.urlopia.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    @Value("${mail.bot.name}")
    private String senderName;

    @Value("${mail.bot.address}")
    private String senderAddress;

    @Autowired
    private JavaMailSender mailSender;

    public void send(Mail mail) {
        mail.setSenderName(senderName);
        mail.setSenderAddress(senderAddress);
//        mailSender.send(new MailConverter(mail).toMimeMessage());
    }
}

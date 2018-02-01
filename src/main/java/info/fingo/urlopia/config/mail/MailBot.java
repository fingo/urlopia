package info.fingo.urlopia.config.mail;

import info.fingo.urlopia.config.mail.send.MailNotificator;
import info.fingo.urlopia.config.mail.send.MailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailBot {

    private final MailNotificator mailNotificator;

    private final MailBotTemplates mailTemplates;

    @Autowired
    public MailBot(MailNotificator mailNotificator, MailBotTemplates mailTemplates) {
        this.mailNotificator = mailNotificator;
        this.mailTemplates = mailTemplates;
    }

    public void parsingProblem(String emailAddress) {
        MailTemplate template = mailTemplates.parsingProblem();
        mailNotificator.notify(template, emailAddress);
    }

    public void requestCreateFailedNoDays(String emailAddress) {
        MailTemplate template = mailTemplates.requestCreateFailedNoDays();
        mailNotificator.notify(template, emailAddress);
    }

    public void requestCreateFailedOverlapping(String emailAddress) {
        MailTemplate template = mailTemplates.requestCreateFailedOverlapping();
        mailNotificator.notify(template, emailAddress);
    }

    public void requestCreateFailed(String emailAddress) {
        MailTemplate template = mailTemplates.requestCreateFailed();
        mailNotificator.notify(template, emailAddress);
    }

    public void userNotFound(String emailAddress) {
        MailTemplate template = mailTemplates.userNotFound();
        mailNotificator.notify(template, emailAddress);
    }

}

package info.fingo.urlopia.mail;

import info.fingo.urlopia.mail.send.MailNotificator;
import info.fingo.urlopia.mail.send.MailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot {

    private final MailNotificator mailNotificator;

    private final BotMailTemplates mailTemplates;

    @Autowired
    public Bot(MailNotificator mailNotificator, BotMailTemplates mailTemplates) {
        this.mailNotificator = mailNotificator;
        this.mailTemplates = mailTemplates;
    }

    public void parsingProblem(String emailAddres) {
        MailTemplate template = mailTemplates.parsingProblem();
        mailNotificator.notify(template, emailAddres);
    }

    public void requestCreateFailedNoDays(String emailAddres) {
        MailTemplate template = mailTemplates.requestCreateFailedNoDays();
        mailNotificator.notify(template, emailAddres);
    }

    public void requestCreateFailedOverlapping(String emailAddres) {
        MailTemplate template = mailTemplates.requestCreateFailedOverlapping();
        mailNotificator.notify(template, emailAddres);
    }

    public void requestCreateFailed(String emailAddres) {
        MailTemplate template = mailTemplates.requestCreateFailed();
        mailNotificator.notify(template, emailAddres);
    }

    public void userNotFound(String emailAddres) {
        MailTemplate template = mailTemplates.userNotFound();
        mailNotificator.notify(template, emailAddres);
    }

}

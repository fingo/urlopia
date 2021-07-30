package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.config.mail.send.MailNotificator;
import info.fingo.urlopia.config.mail.send.MailStorage;
import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.request.occasional.events.OccasionalRequestCreated;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component("occasionalMailSenderListener")
class MailSenderListener {

    private final MailNotificator mailNotificator;

    private final MailStorage mailStorage;

    private final MailTemplates mailTemplates;

    private final UserService userService;

    @Autowired
    public MailSenderListener(MailNotificator mailNotificator, MailStorage mailStorage, MailTemplates mailTemplates, UserService userService) {
        this.mailNotificator = mailNotificator;
        this.mailStorage = mailStorage;
        this.mailTemplates = mailTemplates;
        this.userService = userService;
    }

    @EventListener(condition = "#occasionalRequestCreated.request.requester.ec")
    public void requestAccepted_storage(OccasionalRequestCreated occasionalRequestCreated) {
        var request = occasionalRequestCreated.request();
        var mailTemplate = mailTemplates.requestCreatedLeaderAdmin(request);
        mailStorage.store(mailTemplate);
    }

    @EventListener(condition = "!#occasionalRequestCreated.request.requester.ec")
    public void requestAccepted_admins(OccasionalRequestCreated occasionalRequestCreated) {
        var request = occasionalRequestCreated.request();
        var mailTemplate = mailTemplates.requestCreatedLeaderAdmin(request);
        userService.getAdmins().forEach(admin -> notify(mailTemplate, admin));
    }

    @EventListener
    public void requestCanceled_leader(OccasionalRequestCreated occasionalRequestCreated) {
        var request = occasionalRequestCreated.request();
        var requester = request.getRequester();
        var mailTemplate = mailTemplates.requestCreatedLeaderAdmin(request);
        userService.getLeaders(requester.getId()).forEach(leader -> notify(mailTemplate, leader));
    }

    @EventListener
    public void requestAccepted_requester(OccasionalRequestCreated occasionalRequestCreated) {
        var request = occasionalRequestCreated.request();
        var requester = request.getRequester();
        var mailTemplate = mailTemplates.requestCreatedRequester();
        notify(mailTemplate, requester);
    }

    private void notify(MailTemplate template, 
                        User recipient) {
        var recipientEmail = recipient.getMail();
        var recipientName =  recipient.getFullName();
        mailNotificator.notify(template, recipientEmail, recipientName);
    }
}

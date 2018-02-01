package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.config.mail.send.MailNotificator;
import info.fingo.urlopia.config.mail.send.MailStorage;
import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.occasional.events.OccasionalRequestCreated;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;

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
        Request request = occasionalRequestCreated.getRequest();
        MailTemplate template = mailTemplates.requestCreatedLeaderAdmin(request);
        mailStorage.store(template);
    }

    @EventListener(condition = "!#occasionalRequestCreated.request.requester.ec")
    public void requestAccepted_admins(OccasionalRequestCreated occasionalRequestCreated) {
        Request request = occasionalRequestCreated.getRequest();
        MailTemplate template = mailTemplates.requestCreatedLeaderAdmin(request);

        Set<User> admins = userService.getAdmins();
        admins.forEach(admin -> {
            String recipientName = admin.getFirstName() + " " + admin.getLastName();
            String recipientAddress = admin.getMail();
            mailNotificator.notify(template, recipientAddress, recipientName);
        });
    }

    @EventListener
    public void requestCanceled_leader(OccasionalRequestCreated occasionalRequestCreated) {
        Request request = occasionalRequestCreated.getRequest();
        User requester = request.getRequester();
        MailTemplate template = mailTemplates.requestCreatedLeaderAdmin(request);

        Set<User> leaders = userService.getLeaders(requester.getId());
        leaders.forEach(leader -> {
            String recipientName = leader.getFirstName() + " " + leader.getLastName();
            String recipientAddress = leader.getMail();
            mailNotificator.notify(template, recipientAddress, recipientName);
        });
    }

    @EventListener
    public void requestAccepted_requester(OccasionalRequestCreated occasionalRequestCreated) {
        Request request = occasionalRequestCreated.getRequest();
        User requester = request.getRequester();
        MailTemplate template = mailTemplates.requestCreatedRequester();

        String recipientName = requester.getFirstName() + " " + requester.getLastName();
        String recipientAddress = requester.getMail();
        mailNotificator.notify(template, recipientAddress, recipientName);
    }
}

package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.acceptance.events.AcceptanceCreated;
import info.fingo.urlopia.acceptance.events.AcceptanceRejected;
import info.fingo.urlopia.config.mail.send.MailNotificator;
import info.fingo.urlopia.config.mail.send.MailStorage;
import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.request.normal.events.NormalRequestAccepted;
import info.fingo.urlopia.request.normal.events.NormalRequestCanceled;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;

@Async
@Component("normalMailSenderListener")
class MailSenderListener {

    private final MailNotificator mailNotificator;

    private final MailStorage mailStorage;

    private final MailTemplates mailTemplates;

    private final UserService userService;

    @Autowired
    public MailSenderListener(MailNotificator mailNotificator, MailStorage mailStorage,
                              MailTemplates mailTemplates, UserService userService) {
        this.mailNotificator = mailNotificator;
        this.mailStorage = mailStorage;
        this.mailTemplates = mailTemplates;
        this.userService = userService;
    }

    @EventListener(condition = "#normalRequestAccepted.request.requester.ec")
    public void requestAccepted_storage(NormalRequestAccepted normalRequestAccepted) {
        Request request = normalRequestAccepted.getRequest();
        MailTemplate template = mailTemplates.requestAcceptedAdmin(request);
        mailStorage.store(template);
    }

    @EventListener(condition = "!#normalRequestAccepted.request.requester.ec")
    public void requestAccepted_admins(NormalRequestAccepted normalRequestAccepted) {
        Request request = normalRequestAccepted.getRequest();
        MailTemplate template = mailTemplates.requestAcceptedAdmin(request);

        Set<User> admins = userService.getAdmins();
        admins.forEach(admin -> {
            String recipientName = admin.getFirstName() + " " + admin.getLastName();
            String recipientAddress = admin.getMail();
            mailNotificator.notify(template, recipientAddress, recipientName);
        });
    }

    @EventListener
    public void requestAccepted_requester(NormalRequestAccepted normalRequestAccepted) {
        Request request = normalRequestAccepted.getRequest();
        User requester = request.getRequester();
        MailTemplate template = mailTemplates.requestAcceptedRequester(request);

        String recipientName = requester.getFirstName() + " " + requester.getLastName();
        String recipientAddress = requester.getMail();
        mailNotificator.notify(template, recipientAddress, recipientName);
    }

    @EventListener
    public void requestCanceled_leader(NormalRequestCanceled normalRequestCanceled) {
        Request request = normalRequestCanceled.getRequest();
        MailTemplate template = mailTemplates.requestCanceledLeader(request);

        request.getAcceptances().stream()
                .map(Acceptance::getLeader)
                .forEach(leader -> {
                    String recipientName = leader.getFirstName() + " " + leader.getLastName();
                    String recipientAddress = leader.getMail();
                    mailNotificator.notify(template, recipientAddress, recipientName);
                });
    }

    @EventListener
    public void requestCanceled_requester(NormalRequestCanceled normalRequestCanceled) {
        Request request = normalRequestCanceled.getRequest();
        User requester = request.getRequester();
        MailTemplate template = mailTemplates.requestCanceledRequester(request);

        String recipientName = requester.getFirstName() + " " + requester.getLastName();
        String recipientAddress = requester.getMail();
        mailNotificator.notify(template, recipientAddress, recipientName);
    }

    // *** ACCEPTANCE EVENTS HANDLING ***

    @EventListener(condition = "@normalMailSenderListener.validateRequest(#acceptanceCreated.acceptance.request)")
    public void acceptanceCreated_leader(AcceptanceCreated acceptanceCreated) {
        Acceptance acceptance = acceptanceCreated.getAcceptance();
        User leader = acceptance.getLeader();
        MailTemplate template = mailTemplates.acceptanceCreatedLeader(acceptance.getId(), acceptance.getRequest());

        String recipientName = leader.getFirstName() + " " + leader.getLastName();
        String recipientAddress = leader.getMail();
        mailNotificator.notify(template, recipientAddress, recipientName);
    }

    @EventListener(condition = "@normalMailSenderListener.validateRequest(#acceptanceRejected.acceptance.request)")
    public void acceptanceRejected_requester(AcceptanceRejected acceptanceRejected) {
        Acceptance acceptance = acceptanceRejected.getAcceptance();
        User requester = acceptance.getRequest().getRequester();
        MailTemplate template = mailTemplates.acceptanceRejectedRequester(acceptance);

        String recipientName = requester.getFirstName() + " " + requester.getLastName();
        String recipientAddress = requester.getMail();
        mailNotificator.notify(template, recipientAddress, recipientName);
    }

    public static boolean validateRequest(Request request) {
        return request.getType() == RequestType.NORMAL;
    }

}

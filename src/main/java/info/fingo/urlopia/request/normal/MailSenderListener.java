package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.acceptance.events.AcceptanceCreated;
import info.fingo.urlopia.acceptance.events.AcceptanceRejected;
import info.fingo.urlopia.config.mail.send.MailNotificator;
import info.fingo.urlopia.config.mail.send.MailStorage;
import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.request.normal.events.NormalRequestAccepted;
import info.fingo.urlopia.request.normal.events.NormalRequestCanceled;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component("normalMailSenderListener")
class MailSenderListener {

    private final MailNotificator mailNotificator;

    private final MailStorage mailStorage;

    private final MailTemplates mailTemplates;

    private final UserService userService;

    @Autowired
    public MailSenderListener(MailNotificator mailNotificator,
                              MailStorage mailStorage,
                              MailTemplates mailTemplates,
                              UserService userService) {
        this.mailNotificator = mailNotificator;
        this.mailStorage = mailStorage;
        this.mailTemplates = mailTemplates;
        this.userService = userService;
    }

    @EventListener(condition = "#normalRequestAccepted.request.requester.ec")
    public void requestAccepted_storage(NormalRequestAccepted normalRequestAccepted) {
        var request = normalRequestAccepted.request();
        var mailTemplate = mailTemplates.requestAcceptedAdmin(request);
        mailStorage.store(mailTemplate);
    }

    @EventListener(condition = "!#normalRequestAccepted.request.requester.ec")
    public void requestAccepted_admins(NormalRequestAccepted normalRequestAccepted) {
        var request = normalRequestAccepted.request();
        var mailTemplate = mailTemplates.requestAcceptedAdmin(request);
        userService.getAdmins().forEach(admin -> notify(mailTemplate, admin));
    }

    @EventListener
    public void requestAccepted_requester(NormalRequestAccepted normalRequestAccepted) {
        var request = normalRequestAccepted.request();
        var requester = request.getRequester();
        var mailTemplate = mailTemplates.requestAcceptedRequester(request);
        notify(mailTemplate, requester);
    }

    @EventListener
    public void requestCanceled_leader(NormalRequestCanceled normalRequestCanceled) {
        var request = normalRequestCanceled.request();
        var mailTemplate = mailTemplates.requestCanceledLeader(request);
        request.getAcceptances().stream()
                .map(Acceptance::getLeader)
                .forEach(leader -> notify(mailTemplate, leader));
    }

    @EventListener
    public void requestCanceled_requester(NormalRequestCanceled normalRequestCanceled) {
        var request = normalRequestCanceled.request();
        var requester = request.getRequester();
        var mailTemplate = mailTemplates.requestCanceledRequester(request);
        notify(mailTemplate, requester);
    }

    // *** ACCEPTANCE EVENTS HANDLING ***

    @EventListener(condition = "#acceptanceCreated.acceptance.request.normal")
    public void acceptanceCreated_leader(AcceptanceCreated acceptanceCreated) {
        var acceptance = acceptanceCreated.getAcceptance();
        var leader = acceptance.getLeader();
        var mailTemplate = mailTemplates.acceptanceCreatedLeader(acceptance.getId(), acceptance.getRequest());
        notify(mailTemplate, leader);
    }

    @EventListener(condition = "#acceptanceRejected.acceptance.request.normal")
    public void acceptanceRejected_requester(AcceptanceRejected acceptanceRejected) {
        Acceptance acceptance = acceptanceRejected.getAcceptance();
        User requester = acceptance.getRequest().getRequester();
        var mailTemplate = mailTemplates.acceptanceRejectedRequester(acceptance);
        notify(mailTemplate, requester);
    }

    private void notify(MailTemplate template, User recipient) {
        var recipientEmail = recipient.getMail();
        var recipientName =  recipient.getFullName();
        mailNotificator.notify(template, recipientEmail, recipientName);
    }
}

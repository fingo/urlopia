package info.fingo.urlopia.mail;

import info.fingo.urlopia.request.*;
import info.fingo.urlopia.request.acceptance.AcceptanceService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MailDecider {

    private final UserService userService;

    private final RequestService requestService;

    private final AcceptanceService acceptanceService;

    private final MailParser mailParser;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MailDecider(UserService userService, RequestService requestService, AcceptanceService acceptanceService, MailParser mailParser, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.requestService = requestService;
        this.acceptanceService = acceptanceService;
        this.mailParser = mailParser;
        this.eventPublisher = eventPublisher;
    }

    public void resolve(Mail mail) {
        String senderMail = mail.getSenderAddress();
        User sender = userService.get(senderMail);

        if (sender == null) {
            this.userNotFound(senderMail);
            return;
        }

        mailParser.clear();
        mailParser.parseSubject(mail);

        if (mailParser.isReply()) {
            mailParser.parseReply(mail);
            updateAcceptance();
        } else if (mailParser.parseContent(mail)) {
            createNewRequest(sender);
        } else {
            parsingProblem(sender);
        }
    }

    private void updateAcceptance() {
        long acceptanceId = mailParser.getId();
        String decision = mailParser.getReply().toLowerCase();

        if (mailParser.isAcceptedByMail(decision)) {
            acceptanceService.accept(acceptanceId);
        } else {
            acceptanceService.reject(acceptanceId);
        }
    }

    private void createNewRequest(User requester) {
        Long userId = requester.getId();
        RequestInput requestInput = new RequestInput();
        requestInput.setStartDate(mailParser.getStartDate());
        requestInput.setEndDate(mailParser.getEndDate());
        requestInput.setType(Request.Type.NORMAL);

        try {
            requestService.create(userId, requestInput);
        } catch (NotEnoughDaysException | RequestOverlappingException e) {
            // TODO: send email to user - RequestFailedEvent
        } catch (Exception e) {
            // TODO: send email to user
        }
    }

    private void userNotFound(String mail) {
        // TODO: send email to user
    }

    private void parsingProblem(User sender) {
        // TODO: send email to user - MailParsingProblemEvent
    }
}
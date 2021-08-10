package info.fingo.urlopia.config.mail.receive;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.config.mail.Mail;
import info.fingo.urlopia.config.mail.MailBot;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailDecider {

    private final UserService userService;

    private final RequestService requestService;

    private final AcceptanceService acceptanceService;

    private final MailParser mailParser;

    private final MailBot mailBot;

    private final String mailBotAddress;

    @Autowired
    public MailDecider(UserService userService,
                       RequestService requestService,
                       AcceptanceService acceptanceService,
                       MailParser mailParser,
                       MailBot mailBot,
                       @Value("${mails.bot}") String mailBotAddress) {
        this.userService = userService;
        this.requestService = requestService;
        this.acceptanceService = acceptanceService;
        this.mailParser = mailParser;
        this.mailBot = mailBot;
        this.mailBotAddress = mailBotAddress;
    }

    public void resolve(Mail mail) {
        var senderMail = mail.getSenderAddress();
        if (senderMail.equals(this.mailBotAddress)){
            return;
        }
        var sender = userService.get(senderMail);

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
            parsingProblem(senderMail);
        }
    }

    private void updateAcceptance() {
        var acceptanceId = mailParser.getId();
        var decision = mailParser.getReply().toLowerCase();
        var loggerInfo = "Decision for acceptance with id: %d is: %s"
                .formatted(acceptanceId, decision);
        log.info(loggerInfo);
        if (mailParser.isAcceptedByMail(decision)) {
            acceptanceService.accept(acceptanceId);
        } else {
            acceptanceService.reject(acceptanceId);
        }
    }

    private void createNewRequest(User requester) {
        var userId = requester.getId();
        var userEmail = requester.getMail();
        var requestInput = new RequestInput();
        requestInput.setStartDate(mailParser.getStartDate());
        requestInput.setEndDate(mailParser.getEndDate());
        requestInput.setType(RequestType.NORMAL);
        try {
            requestService.create(userId, requestInput);
        } catch (NotEnoughDaysException e) {
            var loggerInfo = "New request for user with id: %d could not be created. Reason: not enough days"
                    .formatted(userId);
            log.error(loggerInfo);
            mailBot.requestCreateFailedNoDays(userEmail);
        } catch (RequestOverlappingException e) {
            var loggerInfo = "New request for user with id: %d could not be created. Reason: overlapping"
                    .formatted(userId);
            log.error(loggerInfo);
            mailBot.requestCreateFailedOverlapping(userEmail);
        } catch (Exception e) {
            var loggerInfo = "New request for user with id: %d could not be created. Reason: unknown"
                    .formatted(userId);
            log.error(loggerInfo);
            mailBot.requestCreateFailed(userEmail);
        }
    }

    private void userNotFound(String senderMail) {
        var loggerInfo = "Sender with mail: %s not found"
                .formatted(senderMail);
        log.warn(loggerInfo);
        mailBot.userNotFound(senderMail);
    }

    private void parsingProblem(String senderMail) {
        var loggerInfo = "Could not parse email from user: %s"
                .formatted(senderMail);
        log.warn(loggerInfo);
        mailBot.parsingProblem(senderMail);
    }
}
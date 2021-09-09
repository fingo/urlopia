package info.fingo.urlopia.config.mail.receive;

import  info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.config.mail.Mail;
import info.fingo.urlopia.config.mail.MailBot;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.user.NoSuchUserException;
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

    private final EmailBounceDetector emailBounceDetector;

    @Autowired
    public MailDecider(UserService userService,
                       RequestService requestService,
                       AcceptanceService acceptanceService,
                       MailParser mailParser,
                       MailBot mailBot,
                       @Value("${mails.bot}") String mailBotAddress,
                       EmailBounceDetector emailBounceDetector) {
        this.userService = userService;
        this.requestService = requestService;
        this.acceptanceService = acceptanceService;
        this.mailParser = mailParser;
        this.mailBot = mailBot;
        this.mailBotAddress = mailBotAddress;
        this.emailBounceDetector = emailBounceDetector;
    }

    public void resolve(Mail mail) {
        var senderMail = mail.getSenderAddress();
        if (emailBounceDetector.isBounce(mail)
                || senderMail.equals(this.mailBotAddress)) {
            var loggerInfo = "An attempt to parse a message was blocked. Subject: %s, senderMailAddress: %s"
                    .formatted(Anonymizer.anonymizeSubject(mail.getSubject()), Anonymizer.anonymizeMail(senderMail));
            log.warn(loggerInfo);
            return;
        }
        parse(mail);
    }


    private void parse(Mail mail){
        var senderMail = mail.getSenderAddress();
        try{
            var sender = userService.get(senderMail);
            mailParser.clear();
            mailParser.parseSubject(mail);

            if (mailParser.isReply()) {
                mailParser.parseReply(mail);
                updateAcceptance(sender.getId());
            } else if (mailParser.parseContent(mail)) {
                createNewRequest(sender);
            } else {
                parsingProblem(senderMail);
            }
        }catch (NoSuchUserException noSuchUserException){
            log.warn(noSuchUserException.getMessage());
            this.userNotFound(senderMail);
        }

    }

    private void updateAcceptance(Long deciderId) {
        var acceptanceId = mailParser.getId();
        var decision = mailParser.getReply().toLowerCase();
        var loggerInfo = "Decision for acceptance with id: %d is: %s"
                .formatted(acceptanceId, decision);
        log.info(loggerInfo);
        if (mailParser.isAcceptedByMail(decision)) {
            acceptanceService.accept(acceptanceId, deciderId);
        } else {
            acceptanceService.reject(acceptanceId, deciderId);
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
                .formatted(Anonymizer.anonymizeMail(senderMail));
        log.warn(loggerInfo);
        mailBot.userNotFound(senderMail);
    }

    private void parsingProblem(String senderMail) {
        var loggerInfo = "Could not parse email from user: %s"
                .formatted(Anonymizer.anonymizeMail(senderMail));
        log.warn(loggerInfo);
        mailBot.parsingProblem(senderMail);
    }
}
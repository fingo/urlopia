package info.fingo.urlopia.config.mail.receive;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.config.mail.Mail;
import info.fingo.urlopia.config.mail.MailBot;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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
        String senderMail = mail.getSenderAddress();
        if (senderMail.equals(this.mailBotAddress)){
            return;
        }
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
            parsingProblem(senderMail);
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
        String userEmail = requester.getMail();
        RequestInput requestInput = new RequestInput();
        requestInput.setStartDate(mailParser.getStartDate());
        requestInput.setEndDate(mailParser.getEndDate());
        requestInput.setType(RequestType.NORMAL);

        try {
            requestService.create(userId, requestInput);
        } catch (NotEnoughDaysException e) {
            mailBot.requestCreateFailedNoDays(userEmail);
        } catch (RequestOverlappingException e) {
            mailBot.requestCreateFailedOverlapping(userEmail);
        } catch (Exception e) {
            mailBot.requestCreateFailed(userEmail);
        }
    }

    private void userNotFound(String senderMail) {
        mailBot.userNotFound(senderMail);
    }

    private void parsingProblem(String senderMail) {
        mailBot.parsingProblem(senderMail);
    }
}
package info.fingo.urlopia.mail.receive;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.mail.Bot;
import info.fingo.urlopia.mail.Mail;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailDecider {

    private final UserService userService;

    private final RequestService requestService;

    private final AcceptanceService acceptanceService;

    private final MailParser mailParser;

    private final Bot bot;

    @Autowired
    public MailDecider(UserService userService, RequestService requestService, AcceptanceService acceptanceService,
                       MailParser mailParser, Bot bot) {
        this.userService = userService;
        this.requestService = requestService;
        this.acceptanceService = acceptanceService;
        this.mailParser = mailParser;
        this.bot = bot;
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
            bot.requestCreateFailedNoDays(userEmail);
        } catch (RequestOverlappingException e) {
            bot.requestCreateFailedOverlapping(userEmail);
        } catch (Exception e) {
            bot.requestCreateFailed(userEmail);
        }
    }

    private void userNotFound(String senderMail) {
        bot.userNotFound(senderMail);
    }

    private void parsingProblem(String senderMail) {
        bot.parsingProblem(senderMail);
    }
}
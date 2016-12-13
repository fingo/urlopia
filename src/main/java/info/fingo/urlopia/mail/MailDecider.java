package info.fingo.urlopia.mail;

import info.fingo.urlopia.request.AcceptanceService;
import info.fingo.urlopia.request.NotEnoughDaysException;
import info.fingo.urlopia.request.RequestOverlappingException;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author Tomasz Urbas
 */

@Component
public class MailDecider {

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private MailParser mailParser;

    @Autowired
    private MailBot mailBot;


    private void addRequest(UserDTO requester) {
        LocalDate startDate = mailParser.getStartDate();
        LocalDate endDate = mailParser.getEndDate();

        try {
            requestService.insertNormal(requester, startDate, endDate);
        } catch (NotEnoughDaysException | RequestOverlappingException e) {
            mailBot.sendRequestFailed(requester);
        }
    }

    private void updateAcceptance(UserDTO decider) {
        long acceptanceId = mailParser.getId();
        String decision = mailParser.getReply().toLowerCase();

        if (mailParser.isAcceptedByMail(decision)) {
            acceptanceService.accept(acceptanceId, decider.getId());
        } else {
            acceptanceService.reject(acceptanceId, decider.getId());
        }
    }

    private void resolvingProblem(UserDTO sender) {
        mailBot.sendMailParsingProblem(sender);
    }

    public void resolve(Mail mail) {
        UserDTO sender = userService.getUser(mail.getSenderAddress());

        if (sender != null) {
            mailParser.clear();
            mailParser.parseSubject(mail);

            if (mailParser.isReply()) {
                mailParser.parseReply(mail);
                updateAcceptance(sender);
            } else if (mailParser.parseContent(mail)) {
                addRequest(sender);
            } else {
                resolvingProblem(sender);
            }
        }
    }
}
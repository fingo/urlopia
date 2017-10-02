package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.mail.send.MailTemplate;
import info.fingo.urlopia.mail.send.MailTemplateLoader;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("normalMailTemplates")
class MailTemplates {

    private final MailTemplateLoader templateLoader;

    private final String directory = "normal";

    private final String languageCode = "pl";

    @Value("${app.url}")
    private String appUrl;

    @Autowired
    public MailTemplates(MailTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public MailTemplate requestAcceptedAdmin(Request request) {
        User requester = request.getRequester();
        String requesterName = requester.getFirstName() + " " + requester.getLastName();
        String term = request.getStartDate() + " - " + request.getEndDate();

        return templateLoader.load(directory, "request_accepted_admin", languageCode)
                .addProperty("requester-name", requesterName)
                .addProperty("term", term);
    }

    public MailTemplate requestAcceptedRequester(Request request) {
        String term = request.getStartDate() + " - " + request.getEndDate();
        return templateLoader.load(directory, "request_accepted_requester", languageCode)
                .addProperty("term", term);
    }

    public MailTemplate requestCanceledLeader(Request request) {
        User requester = request.getRequester();
        String requesterName = requester.getFirstName() + " " + requester.getLastName();
        String term = request.getStartDate() + " - " + request.getEndDate();

        return templateLoader.load(directory, "request_canceled_leader", languageCode)
                .addProperty("requester-name", requesterName)
                .addProperty("term", term)
                .addProperty("app-url", appUrl);
    }

    public MailTemplate requestCanceledRequester(Request request) {
        String term = request.getStartDate() + " - " + request.getEndDate();

        return templateLoader.load(directory, "request_canceled_requester", languageCode)
                .addProperty("term", term)
                .addProperty("app-url", appUrl);
    }

    public MailTemplate acceptanceCreatedLeader(Long acceptanceId, Request request) {
        User requester = request.getRequester();
        String requesterName = requester.getFirstName() + " " + requester.getLastName();
        String term = request.getStartDate() + " - " + request.getEndDate();

        return templateLoader.load(directory, "acceptance_created_leader", languageCode)
                .addProperty("requester-name", requesterName)
                .addProperty("term", term)
                .addProperty("acceptance-id", acceptanceId)
                .addProperty("app-url", appUrl);
    }

    public MailTemplate acceptanceRejectedRequester(Acceptance acceptance) {
        User leader = acceptance.getLeader();
        Request request = acceptance.getRequest();
        String leaderName = leader.getFirstName() + " " + leader.getLastName();
        String term = request.getStartDate() + " - " + request.getEndDate();

        return templateLoader.load(directory, "acceptance_rejected_requester", languageCode)
                .addProperty("leader-name", leaderName)
                .addProperty("term", term);
    }

}

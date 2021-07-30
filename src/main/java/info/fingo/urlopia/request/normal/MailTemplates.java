package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.config.mail.send.MailTemplateLoader;
import info.fingo.urlopia.request.Request;
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
        var requester = request.getRequester();
        return templateLoader.load(directory, "request_accepted_admin", languageCode)
                .addProperty("requester-name", requester.getFullName())
                .addProperty("term", request.getTerm());
    }

    public MailTemplate requestAcceptedRequester(Request request) {
        return templateLoader.load(directory, "request_accepted_requester", languageCode)
                .addProperty("term", request.getTerm());
    }

    public MailTemplate requestCanceledLeader(Request request) {
        var requester = request.getRequester();
        return templateLoader.load(directory, "request_canceled_leader", languageCode)
                .addProperty("requester-name", requester.getFullName())
                .addProperty("term", request.getTerm())
                .addProperty("app-url", appUrl);
    }

    public MailTemplate requestCanceledRequester(Request request) {
        return templateLoader.load(directory, "request_canceled_requester", languageCode)
                .addProperty("term", request.getTerm())
                .addProperty("app-url", appUrl);
    }

    public MailTemplate acceptanceCreatedLeader(Long acceptanceId,
                                                Request request) {
        var requester = request.getRequester();
        return templateLoader.load(directory, "acceptance_created_leader", languageCode)
                .addProperty("requester-name", requester.getFullName())
                .addProperty("term", request.getTerm())
                .addProperty("acceptance-id", acceptanceId)
                .addProperty("app-url", appUrl);
    }

    public MailTemplate acceptanceRejectedRequester(Acceptance acceptance) {
        var leader = acceptance.getLeader();
        var request = acceptance.getRequest();
        return templateLoader.load(directory, "acceptance_rejected_requester", languageCode)
                .addProperty("leader-name", leader.getFullName())
                .addProperty("term", request.getTerm());
    }
}

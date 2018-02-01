package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.config.mail.send.MailTemplateLoader;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("occasionalMailTemplates")
class MailTemplates {

    private final MailTemplateLoader templateLoader;

    private final String directory = "occasional";

    private final String languageCode = "pl";

    @Autowired
    public MailTemplates(MailTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public MailTemplate requestCreatedLeaderAdmin(Request request) {
        User requester = request.getRequester();
        String requesterName = requester.getFirstName() + " " + requester.getLastName();
        String term = request.getStartDate() + " - " + request.getEndDate();
        String type = request.getTypeInfo().getName();

        return templateLoader.load(directory, "request_created_leader_admin", languageCode)
                .addProperty("requester-name", requesterName)
                .addProperty("term", term)
                .addProperty("type", type);
    }

    public MailTemplate requestCreatedRequester() {
        return templateLoader.load(directory, "request_created_requester", languageCode);
    }


}

package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.config.mail.send.MailTemplateLoader;
import info.fingo.urlopia.request.Request;
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
        var requester = request.getRequester();
        var type = request.getTypeInfo().getName();

        return templateLoader.load(directory, "request_created_leader_admin", languageCode)
                .addProperty("requester-name", requester.getFullName())
                .addProperty("term", request.getTerm())
                .addProperty("type", type);
    }

    public MailTemplate requestCreatedRequester() {
        return templateLoader.load(directory, "request_created_requester", languageCode);
    }


}

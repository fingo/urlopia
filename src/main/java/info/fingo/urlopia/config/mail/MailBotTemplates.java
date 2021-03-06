package info.fingo.urlopia.config.mail;

import info.fingo.urlopia.config.mail.send.MailTemplate;
import info.fingo.urlopia.config.mail.send.MailTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailBotTemplates {

    private final MailTemplateLoader templateLoader;

    private final String directory = "error";

    private final String languageCode = "pl";

    @Autowired
    public MailBotTemplates(MailTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public MailTemplate parsingProblem() {
        return templateLoader.load(directory, "parsing_problem", languageCode);
    }

    public MailTemplate requestCreateFailedNoDays() {
        return templateLoader.load(directory, "request_create_failed_no_days", languageCode);
    }

    public MailTemplate requestCreateFailedOverlapping() {
        return templateLoader.load(directory, "request_create_failed_overlapping", languageCode);
    }

    public MailTemplate requestCreateFailed() {
        return templateLoader.load(directory, "request_create_failed", languageCode);
    }

    public MailTemplate userNotFound() {
        return templateLoader.load(directory, "user_not_found", languageCode);
    }

}

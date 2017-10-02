package info.fingo.urlopia.mail.send;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class MailTemplateLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailTemplateLoader.class);

    @Value("${mail.template.prefix:/}")
    private String prefix;

    @Value("${mail.template.suffix:.hbs}")
    private String suffix;

    private Handlebars handlebars;

    @PostConstruct
    private void setUpHandlebars() {
        TemplateLoader templateLoader = new ClassPathTemplateLoader();
        templateLoader.setPrefix(prefix);
        templateLoader.setSuffix(suffix);

        handlebars = new Handlebars(templateLoader);
        handlebars.registerHelpers(new HandlebarsHelper());
    }

    public MailTemplate load(String name) {
        String defaultLanguage = "pl";
        return this.load(name, defaultLanguage);
    }

    public MailTemplate load(String name, String languageCode) {
        String defaultDirectory = "";
        return this.load(defaultDirectory, name, languageCode);
    }

    public MailTemplate load(String directory, String name, String languageCode) {
        try {
            Template template = handlebars.compile(String.format("%s/%s_%s", directory, name, languageCode));
            return new MailTemplate(template);
        } catch (IOException e) {
            LOGGER.error("IOException when trying to load a template", e);
            throw new TemplateNotFoundException();
        }
    }
}

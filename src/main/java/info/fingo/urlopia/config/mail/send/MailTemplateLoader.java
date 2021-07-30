package info.fingo.urlopia.config.mail.send;

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
        var templateLoader = new ClassPathTemplateLoader();
        templateLoader.setPrefix(prefix);
        templateLoader.setSuffix(suffix);

        handlebars = new Handlebars(templateLoader);
        handlebars.registerHelpers(new HandlebarsHelper());
    }

    public MailTemplate load(String directory, String name, String languageCode) {
        try {
            String templateLocation = String.format("%s/%s_%s", directory, name, languageCode);
            Template template = handlebars.compile(templateLocation);
            return new MailTemplate(template);
        } catch (IOException e) {
            LOGGER.error("IOException when trying to load a template", e);
            throw new TemplateNotFoundException();
        }
    }
}

package info.fingo.urlopia.config.mail.send;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MailTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailTemplateLoader.class);

    private final Template template;
    private final Map<String, Object> model = new HashMap<>();

    MailTemplate(Template template) {
        this.template = template;
    }

    private String getTemplateContent() {
        var content = "";

        try {
            var context = Context.newBuilder(model)
                    .resolver(MapValueResolver.INSTANCE)
                    .build();
            content = template.apply(context);
        } catch (IOException e) {
            LOGGER.error("IOException when trying to get template content", e);
        }

        return content;
    }

    public MailTemplate addProperty(String key, Object value) {
        this.model.put(key, value);
        return this;
    }

    public String getSubject() {
        var templateContent = getTemplateContent();
        var lines = templateContent.split("\r\n|\r|\n", 2);
        return lines[0];
    }

    public String getContent() {
        var templateContent = getTemplateContent();
        var lines = templateContent.split("\r\n|\r|\n", 3);
        return lines[2];
    }
}

package info.fingo.urlopia.mail;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EmailTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateLoader.class);

    private Template template;
    private Map<String, Object> model = new HashMap<>();

    EmailTemplate(Template template) {
        this.template = template;
    }

    private String getTemplateContent() {
        String content = "";

        try {
            Context context = Context.newBuilder(model)
                    .resolver(MapValueResolver.INSTANCE)
                    .build();
            content = template.apply(context);
        } catch (IOException e) {
            LOGGER.error("IOException when trying to get template content", e);
        }

        return content;
    }

    public EmailTemplate addProperty(String key, Object value) {
        this.model.put(key, value);
        return this;
    }

    public String getSubject() {
        String templateContent = getTemplateContent();
        String[] lines = templateContent.split("\r\n|\r|\n", 2);
        return lines[0];
    }

    public String getContent() {
        String templateContent = getTemplateContent();
        String[] lines = templateContent.split("\r\n|\r|\n", 3);
        return lines[2];
    }
}

package info.fingo.urlopia.reports;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ReportTemplateLoader {

    private static final String TEMPLATES_DIRECTORY = "reports";

    public Resource load(String templateName) {
        return new ClassPathResource(TEMPLATES_DIRECTORY + "/" + templateName);
    }

}

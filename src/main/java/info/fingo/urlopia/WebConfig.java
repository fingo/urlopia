package info.fingo.urlopia;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Mateusz Wiśniewski
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * Maps AngularJS routes to index so that they work with direct linking.
     */
    @Controller
    static class Routes {

        @RequestMapping({
                "/worker/**",
                "/leader/**",
                "/admin/**",
                "/history/**"
        })
        public String index() {
            return "forward:/index.html";
        }
    }

    /**
     * Controller for error page.
     */
    @Controller
    static class IndexController implements ErrorController {

        private static final String PATH = "/error";

        @RequestMapping(value = PATH)
        public String error() {
            return "forward:/index.html";
        }

        @Override
        public String getErrorPath() {
            return PATH;
        }
    }
}

package info.fingo.urlopia.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
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

    }
}

package info.fingo.urlopia.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController implements ErrorController {

    @RequestMapping({
            "/worker/**",
            "/leader/**",
            "/admin/**",
            "/history/**"
    })
    public String index() {
        return "forward:/index.html";
    }

    @RequestMapping(value = "/error")
    public String error() {
        return "forward:/index.html";
    }

}

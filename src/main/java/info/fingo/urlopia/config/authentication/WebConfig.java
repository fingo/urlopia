package info.fingo.urlopia.config.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CorsInterceptor corsInterceptor;

    private final AuthInterceptor authInterceptor;

    public WebConfig(CorsInterceptor corsInterceptor,
                     AuthInterceptor authInterceptor) {
        this.corsInterceptor = corsInterceptor;
        this.authInterceptor = authInterceptor;
    }

    // TODO: Use Spring Boot Security and remove these interceptors
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/session/**", "/api/v2/session/**");
    }

}

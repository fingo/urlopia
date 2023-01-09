package info.fingo.urlopia.config.authentication.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class OAuthWebConfig implements WebMvcConfigurer {

    private final OAuthUserIdInterceptor OAuthUserIdInterceptor;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(OAuthUserIdInterceptor);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}

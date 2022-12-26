package info.fingo.urlopia.config.authentication.oauth;

import info.fingo.urlopia.config.authentication.AuthorizationFilterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class OAuthFilterProvider implements AuthorizationFilterProvider {

    private final JwtFilter jwtFilter;

    @Override
    public OncePerRequestFilter getAuthorizationFilter() {
        return jwtFilter;
    }
}

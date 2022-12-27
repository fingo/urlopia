package info.fingo.urlopia.config.authentication.noauth;

import info.fingo.urlopia.config.authentication.AuthorizationFilterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "false")
public class NoAuthFilterProvider implements AuthorizationFilterProvider {

    private final NoAuthFilter noAuthFilter;

    @Override
    public OncePerRequestFilter getAuthorizationFilter() {
        return noAuthFilter;
    }
}

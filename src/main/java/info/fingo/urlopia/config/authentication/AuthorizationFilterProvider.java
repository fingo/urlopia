package info.fingo.urlopia.config.authentication;

import org.springframework.web.filter.OncePerRequestFilter;

public interface AuthorizationFilterProvider {

    OncePerRequestFilter getAuthorizationFilter();

}

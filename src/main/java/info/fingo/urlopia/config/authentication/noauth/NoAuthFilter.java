package info.fingo.urlopia.config.authentication.noauth;

import info.fingo.urlopia.config.authentication.UserAuthoritiesProvider;
import info.fingo.urlopia.config.authentication.oauth.OAuthUserIdInterceptor;
import info.fingo.urlopia.config.authentication.oauth.InvalidTokenException;
import info.fingo.urlopia.user.NoSuchUserException;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "false")
public class NoAuthFilter extends OncePerRequestFilter {

    private final UserAuthoritiesProvider userAuthoritiesProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);
        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var user = userService.get(Long.valueOf(header));
        var authResult = getAuthenticationForUser(user, response);
        if (authResult != null){
            SecurityContextHolder.getContext().setAuthentication(authResult);
            filterChain.doFilter(request, response);
        }
    }

    private Authentication getAuthenticationForUser(User user,
                                                    HttpServletResponse response) {
        try{
            var principal = user.getPrincipalName();
            var authorities = userAuthoritiesProvider.getAuthoritiesFromUser(user);
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        }catch (InvalidTokenException | NoSuchUserException exception){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }
}

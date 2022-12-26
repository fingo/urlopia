package info.fingo.urlopia.config.authentication.oauth;


import info.fingo.urlopia.api.v2.authentication.oauth.OAuthRedirectService;
import info.fingo.urlopia.user.NoSuchUserException;
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
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(OAuthRedirectService.BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        var authResult = getAuthenticationByToken(header, response);
        if (authResult != null){
            SecurityContextHolder.getContext().setAuthentication(authResult);
            chain.doFilter(request, response);
        }
    }
    private Authentication getAuthenticationByToken(String header,
                                                    HttpServletResponse response) {
        try{
            var accessToken = jwtTokenValidator.validateAuthorizationHeader(header);
            var principal = accessToken.getPrincipal();
            var authorities = accessToken.getAuthorities();
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        }catch (InvalidTokenException | NoSuchUserException exception){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }
}

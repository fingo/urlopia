package info.fingo.urlopia.config.authentication;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";

    private final WebTokenService webTokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(SessionService.BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        Authentication authResult = getAuthenticationByToken(header, response);
        if (authResult != null){
            SecurityContextHolder.getContext().setAuthentication(authResult);
            chain.doFilter(request, response);
        }
    }
    private Authentication getAuthenticationByToken(String header,
                                                    HttpServletResponse response) {

        var optionalClaim = JwtUtils.getClaimFromToken(header);
        if (optionalClaim.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        } else {
            var claim = optionalClaim.get();
            webTokenService.setWebToken(claim);
            return createAuthenticationFromClaim(claim);
        }
    }

    @SuppressWarnings("unchecked")
    private Authentication createAuthenticationFromClaim(Claims claim){
        var username = claim.getSubject();
        var roles = (List<String>) claim.get("role", List.class);
        var authorities = getAuthoritiesFromRoles(roles);
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private Set<SimpleGrantedAuthority> getAuthoritiesFromRoles(List<String> roles){
        var authorities = new HashSet<SimpleGrantedAuthority>();
        roles.forEach(role -> addAuthorityFromRole(role, authorities));
        return authorities;
    }

    private void addAuthorityFromRole(String role,
                                      Set<SimpleGrantedAuthority> authorities){
        var authority = role;
        if (!role.startsWith(ROLE_PREFIX)){
            authority = ROLE_PREFIX + role;
        }
        authorities.add(new SimpleGrantedAuthority(authority));
    }
}

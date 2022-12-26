package info.fingo.urlopia.config.authentication.oauth;

import com.auth0.jwt.interfaces.DecodedJWT;
import info.fingo.urlopia.config.authentication.UserAuthoritiesProvider;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtTokenAuthoritiesProvider {
    private final UserService userService;
    private final UserAuthoritiesProvider userAuthoritiesProvider;

    public Set<SimpleGrantedAuthority> getAuthoritiesFromJWT(DecodedJWT decodedToken){
        var principal = JwtUtils.getPrincipalNameFromDecodedToken(decodedToken);
        var user = userService.getByPrincipal(principal);
        return userAuthoritiesProvider.getAuthoritiesFromUser(user);
    }
}

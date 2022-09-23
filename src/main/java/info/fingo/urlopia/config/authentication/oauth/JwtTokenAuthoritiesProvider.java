package info.fingo.urlopia.config.authentication.oauth;

import com.auth0.jwt.interfaces.DecodedJWT;
import info.fingo.urlopia.api.v2.user.UserRolesProvider;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtTokenAuthoritiesProvider {

    public static final String ROLE_PREFIX = "ROLE_";

    private final UserService userService;
    private final UserRolesProvider userRolesProvider;

    public Set<SimpleGrantedAuthority> getAuthoritiesFromJWT(DecodedJWT decodedToken){
        var principal = JwtUtils.getPrincipalNameFromDecodedToken(decodedToken);
        var user = userService.getByPrincipal(principal);
        return getAuthoritiesFromUser(user);
    }

    private Set<SimpleGrantedAuthority> getAuthoritiesFromUser(User user) {
        var authorities = new HashSet<SimpleGrantedAuthority>();
        var roles = userRolesProvider.getRolesFromUser(user);
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

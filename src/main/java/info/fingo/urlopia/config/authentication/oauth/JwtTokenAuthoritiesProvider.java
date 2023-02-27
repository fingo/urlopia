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
//        var principal = JwtUtils.getPrincipalNameFromDecodedToken(decodedToken); TODO: go back to checking @ / sth else than name and surname
//        var user = userService.getByPrincipal(principal);

        var firstName = JwtUtils.getFirstNameFromDecodedToken(decodedToken);
        var lastName = JwtUtils.getLastNameFromDecodedToken(decodedToken);
        var user = userService.getByFirstNameAndLastName(firstName, lastName);

        return userAuthoritiesProvider.getAuthoritiesFromUser(user);
    }
}

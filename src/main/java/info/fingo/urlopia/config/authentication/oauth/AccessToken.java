package info.fingo.urlopia.config.authentication.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class AccessToken {

    private final String value;
    private final JwtTokenAuthoritiesProvider jwtTokenAuthoritiesProvider;


    public String getEmail() {
        var decodedToken = decodeToken(value);
        return JwtUtils.getEmailFromDecodedToken(decodedToken);
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        var decodedJWT = decodeToken(value);
        return jwtTokenAuthoritiesProvider.getAuthoritiesFromJWT(decodedJWT);
    }

    private DecodedJWT decodeToken(String value) {
        if (isNull(value)){
            throw new InvalidTokenException("Token has not been provided");
        }
        return JWT.decode(value);
    }
}

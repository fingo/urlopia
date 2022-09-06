package info.fingo.urlopia.config.authentication;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    private static String SECRET_KEY;

    @Value("${webtoken.secret}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public static Optional<Claims> getClaimFromToken(String token){
        if (token == null){
            return Optional.empty();
        }
        token = token.replace(SessionService.BEARER_PREFIX, "");
        try {
            var claim =  Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(claim);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            LOGGER.error("Invalid token " + e.getMessage());
        } catch (ExpiredJwtException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Unhandled exception ", e);
        }
        return Optional.empty();
    }

}

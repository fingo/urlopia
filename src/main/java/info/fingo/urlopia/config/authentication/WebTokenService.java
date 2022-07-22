package info.fingo.urlopia.config.authentication;

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RequestScope
@Service
@Slf4j
public class WebTokenService {

    static final long SESSION_DURATION = 24 * 60 * 60 * (long) 1000; // 1 day in milliseconds
    private WebToken webToken;

    private String SECRET_KEY;

    public WebTokenService(@Value("${webtoken.secret}") String secretKey){
        SECRET_KEY = secretKey;
    }

    public void setWebToken(Claims claims) {
        webToken = WebToken.fromClaim(claims);
    }

    public List<String> getRoles() {
        return webToken.getRoles();
    }

    public long getUserId() {
        return webToken.getUserId();
    }

    static long getSessionDuration() {
        return SESSION_DURATION;
    }

    public String generateWebToken(Long userId, List<String> roles) {
        return WebToken.fromCredentials(userId, roles).toJsonWebToken(SECRET_KEY);
    }

    public void ensureAdmin() {
        var roles = this.getRoles();
        var isAdmin = roles.contains("ROLES_ADMIN");

        if (!isAdmin) {
            log.error("This action could not be performed because user has no role ADMIN");
            throw UnauthorizedException.unauthorized();
        }
    }

    public boolean isCurrentUserAnAdmin() {
        var roles = this.getRoles();
        return roles.contains("ROLES_ADMIN");
    }
}

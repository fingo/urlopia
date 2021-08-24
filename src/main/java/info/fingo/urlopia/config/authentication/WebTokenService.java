package info.fingo.urlopia.config.authentication;

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestScope
@Service
public class WebTokenService {

    static final long SESSION_DURATION = 24 * 60 * 60 * (long) 1000; // 1 day in milliseconds
    private WebToken webToken;

    private String SECRET_KEY;

    public WebTokenService(@Value("${webtoken.secret}") String secretKey){
        SECRET_KEY = secretKey;
    }

    public void authorize(HttpServletRequest request) {
        webToken = WebToken.fromRequest(request, SECRET_KEY);
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
            throw UnauthorizedException.unauthorized();
        }
    }

    public boolean isCurrentUserAnAdmin() {
        var roles = this.getRoles();
        return roles.contains("ROLES_ADMIN");
    }
}
